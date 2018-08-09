package org.odk.odknotifications.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Fragments.NotificationGroupFragment;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.R;
import org.odk.odknotifications.SubscribeNotificationGroup;
import org.odk.odknotifications.UnsubscribeNotificationGroups;
import org.opendatakit.application.CommonApplication;
import org.opendatakit.consts.IntentConsts;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ServicesAvailabilityException;
import org.opendatakit.listener.DatabaseConnectionListener;
import org.opendatakit.logging.WebLogger;
import org.opendatakit.properties.CommonToolProperties;
import org.opendatakit.properties.PropertiesSingleton;
import org.opendatakit.utilities.ODKFileUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatabaseConnectionListener {

    private String appName = "org.odk.odknotifications";
    private DatabaseConnectionListener mIOdkDataDatabaseListener;
    private TextView name_tv;
    private String loggedInUsername;
    private String TAG = "ODK Notifications";
    private PropertiesSingleton mPropSingleton;
    private DBHandler dbHandler;
    private ArrayList<Group> groupArrayList;
    public static final String ARG_GROUP_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.initiateScan();
            }
        });

        dbHandler = new DBHandler(this,null,null,1);

        String appName = getIntent().getStringExtra(IntentConsts.INTENT_KEY_APP_NAME);
        if (appName == null) {
            appName = ODKFileUtils.getOdkDefaultAppName();
        }
        this.appName = appName;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        name_tv = (TextView) headerView.findViewById(R.id.name_tv);

        addMenuItemInNavMenuDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
               String link = result.getContents();
               Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
               startActivity(browserIntent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addMenuItemInNavMenuDrawer() {

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        groupArrayList = dbHandler.getGroups();
        Menu menu = navView.getMenu();
        menu.clear();
        for (int i =0; i<groupArrayList.size();i++){
            if(groupArrayList.get(i).getName()!=null)
            menu.add(0,i,0,groupArrayList.get(i).getName());
        }
        navView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync) {
            syncCloudDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncCloudDatabase() {
        groupArrayList = getGroups();
        addGroupsFromFirebase();
        joinODKGroups(groupArrayList);
        addMenuItemInNavMenuDrawer();
    }

    private void addGroupsFromFirebase() {
        DatabaseReference mRef  = FirebaseDatabase.getInstance().getReference().child("clients").child(getActiveUser());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot group: dataSnapshot.getChildren()){
                    String name =(String) group.child("name").getValue();
                    String id = (String) group.child("id").getValue();
                    Group grp = new  Group(id,name,0);
                    groupArrayList.add(grp);
                    new SubscribeNotificationGroup(MainActivity.this, id, getActiveUser());
                    dbHandler.addNewGroup(grp);
                }
                addMenuItemInNavMenuDrawer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        NotificationGroupFragment fragment = new NotificationGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_GROUP_ID, groupArrayList.get(item.getItemId()).getId());
        FragmentManager manager = getSupportFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public String getActiveUser() {
        try {
            return getDatabase().getActiveUser(getAppName());
        } catch (ServicesAvailabilityException e) {
            WebLogger.getLogger(getAppName()).printStackTrace(e);
            return CommonToolProperties.ANONYMOUS_USER;
        }
    }


    public UserDbInterface getDatabase() {
        return ((CommonApplication) getApplication()).getDatabase();
    }

    public String getAppName() {
        return this.appName;
    }


    @Override
    public void databaseAvailable() {

        if (mIOdkDataDatabaseListener != null) {
            mIOdkDataDatabaseListener.databaseAvailable();
        }
        loggedInUsername = getActiveUser();
        getDeepLink();
        Log.e("Success", "Database available" + loggedInUsername);
        if(loggedInUsername!=null)name_tv.setText(loggedInUsername);
    }

    @Override
    public void databaseUnavailable() {

        if (mIOdkDataDatabaseListener != null) {
            mIOdkDataDatabaseListener.databaseUnavailable();
        }
        Log.e("ERROR", "Database unavailable");

    }


    @Override
    protected void onResume() {
        super.onResume();
        ((CommonApplication) getApplication()).onActivityResume(this);
        ((CommonApplication) getApplication()).establishDoNotFireDatabaseConnectionListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ((CommonApplication) getApplication()).fireDatabaseConnectionListener();
    }

    @Override
    protected void onPause() {
        ((CommonApplication) getApplication()).onActivityPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((CommonApplication) getApplication()).onActivityDestroy(this);
        super.onDestroy();
    }

    public void getDeepLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;

                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        try {
                            if(deepLink!=null) {
                                URL url = new URL(deepLink.toString());
                                Map<String, String> map = splitQuery(url);
                                String id = map.get("id");
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                mRef.child("group").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Group group = new Group((String)dataSnapshot.child("id").getValue(),(String)dataSnapshot.child("name").getValue(),0);
                                        new SubscribeNotificationGroup(MainActivity.this,group.getId(),getActiveUser()).execute();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("clients").child(getActiveUser()).child("groups");
                                        databaseReference.child("id").setValue(group.getId());
                                        databaseReference.child("name").setValue(group.getName());
                                        dbHandler.addNewGroup(group);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else {
                                Log.d(TAG, "getDynamicLink: no link found");
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

   public ArrayList<Group> getGroups(){
        ArrayList<Group> groupsList = new ArrayList<>();
      try {
          String roles_array_string = getDatabase().getRolesList(getAppName());

          JSONArray rolesArray = new JSONArray(roles_array_string);
              for (int i=0;i<rolesArray.length();i++){
                  if(rolesArray.getString(i).startsWith("GROUP_")|| rolesArray.getString(i).startsWith("ROLE_"))
                  groupsList.add(new Group(rolesArray.getString(i), rolesArray.getString(i), 0));
              }

      } catch (JSONException e) {
          e.printStackTrace();
      } catch (ServicesAvailabilityException e) {
          e.printStackTrace();
      } catch (NullPointerException e){
          e.printStackTrace();
      }
       return groupsList;
   }

    public void joinODKGroups(ArrayList<Group> groupArrayList){
        new UnsubscribeNotificationGroups(this).execute();
        for(Group group: groupArrayList) {
            new SubscribeNotificationGroup(this, group.getId(), getActiveUser()).execute();
        }
        dbHandler.newGroupDatabase(groupArrayList);
    }
}


