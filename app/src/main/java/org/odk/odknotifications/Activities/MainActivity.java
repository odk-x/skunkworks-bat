package org.odk.odknotifications.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.odknotifications.Adapters.NotificationAdapter;
import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.DatabaseCommunicator.ServerDatabaseCommunicator;
import org.odk.odknotifications.Fragments.FilterNotificationDialogFragment;
import org.odk.odknotifications.Fragments.NotificationGroupFragment;
import org.odk.odknotifications.Fragments.SortingOptionListDialogFragment;
import org.odk.odknotifications.Listeners.FilterNotificationListener;
import org.odk.odknotifications.Listeners.SortingOptionListener;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.R;
import org.odk.odknotifications.SubscribeNotificationGroup;
import org.odk.odknotifications.SyncDataWithServices;
import org.odk.odknotifications.UnsubscribeNotificationGroups;
import org.odk.odknotifications.utils.ResponseHandler;
import org.opendatakit.application.CommonApplication;
import org.opendatakit.consts.IntentConsts;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ActionNotAuthorizedException;
import org.opendatakit.exception.ServicesAvailabilityException;
import org.opendatakit.listener.DatabaseConnectionListener;
import org.opendatakit.logging.WebLogger;
import org.opendatakit.properties.CommonToolProperties;
import org.opendatakit.utilities.ODKFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatabaseConnectionListener, SortingOptionListener, FilterNotificationListener{

    private static final String SORTED_ORDER = "sorted_order";
    private static final String FILTERED_GRP = "filtered_grp";

    public static String appName = ODKFileUtils.getOdkDefaultAppName();

    private DatabaseConnectionListener mIOdkDataDatabaseListener;

    private TextView name_tv;
    private String loggedInUsername;
    private String TAG = "ODK-X Notify";
    private DBHandler dbHandler;
    private ArrayList<Group> groupArrayList;
    public static final String ARG_GROUP_ID = "id";
    private final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    private boolean hasBeenInitialized = false;
    private SearchView searchView;
    private NotificationAdapter notificationAdapter;
    private String filteredGrp = "None";
    private String sortedOrder;
    private MenuItem syncitem;
    private ArrayList<Notification> notificationArrayList;

    private static final String ANONYMOUS_USER_NAME = "anonymous";
    private static final String USERNAME_PREFIX = "username:";

    protected static final String[] STORAGE_PERMISSION = new String[] {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(this);
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                isInitialized(true);
            }
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestStoragePermission();
        sortedOrder = getResources().getString(R.string.date_old_to_new);
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

        findViewById(R.id.sort_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = SortingOptionListDialogFragment.newInstance(sortedOrder);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        findViewById(R.id.filter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> groupNameList = new ArrayList<>();
                groupNameList.add("None");
                for(Group group: groupArrayList){
                    groupNameList.add(group.getName());
                }
                BottomSheetDialogFragment bottomSheetDialogFragment = FilterNotificationDialogFragment.newInstance(groupNameList,filteredGrp);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        addNotifications();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SORTED_ORDER,sortedOrder);
        outState.putString(FILTERED_GRP,filteredGrp);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        filteredGrp = savedInstanceState.getString(FILTERED_GRP);
        sortedOrder = savedInstanceState.getString(SORTED_ORDER);
        sort(sortedOrder);
        filterByGroup(filteredGrp);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            File jsonFile = new File(ODKFileUtils.getAssetsFolder(appName)+"/google-services.json");
            FileInputStream is = new FileInputStream(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.e("JSON", json);
        } catch (IOException ex) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(getString(R.string.json_file_missing_error_message));
            alertDialog.setIcon(R.drawable.ic_error_red_24dp);
            alertDialog.show();
            ex.printStackTrace();
            return null;
        }
        return json;
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
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addNotifications(){

        RecyclerView recyclerView = findViewById(R.id.rv_notifications);
        notificationArrayList = dbHandler.getAllNotificationsWithResponses();
        notificationAdapter = new NotificationAdapter(notificationArrayList, this);
        TextView textView = findViewById(R.id.no_data_text_view);

        if(notificationArrayList == null || notificationArrayList.size()==0) {
            textView.setText(R.string.No_data);
            textView.setVisibility(View.VISIBLE);
        }

        else {

            textView.setVisibility(View.GONE);

            recyclerView.setAdapter(notificationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            notificationAdapter.setonButtonClickListener(new NotificationAdapter.onButtonClickListener() {
                @Override
                public boolean onSendResponseButtonClick(int position, String response) {
                    Notification notification = notificationArrayList.get(position);
                    ResponseHandler responseHandler = new ResponseHandler(getApplicationContext());
                    boolean isDone = responseHandler.saveResponse(notification.getId(), response, new Date().getTime());
                    if (isDone) {
                        Toast.makeText(getApplicationContext(), "Message sent successfully", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            });
        }
    }

    private void addMenuItemInNavMenuDrawer() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        try {
            groupArrayList = ServerDatabaseCommunicator.getInstance().getGroupsList(getActiveUser());
        } catch (ServicesAvailabilityException e) {
            e.printStackTrace();
        }
        Menu menu = navView.getMenu();
        menu.clear();
        for (int i =0; i<groupArrayList.size();i++){
            if(groupArrayList.get(i).getName()!=null)
            menu.add(0,i,0,groupArrayList.get(i).getName());
        }
        navView.invalidate();
    }

    private void setUserName(){

        loggedInUsername = getActiveUser();
        Log.d("Success", "Database available " + loggedInUsername);
        if(loggedInUsername!=null){
            if (!(loggedInUsername.equals(ANONYMOUS_USER_NAME)) && loggedInUsername.length() > 8 && loggedInUsername.substring(0, 9).equals(USERNAME_PREFIX)) {
                loggedInUsername = loggedInUsername.substring(9);   //To remove prefix from the UserID
            }
            name_tv.setText(loggedInUsername);
            SharedPreferences preferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            preferences.edit().putString("username",loggedInUsername).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                notificationAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                notificationAdapter.getFilter().filter(query);
                return false;
            }
        });
        syncitem=(MenuItem)menu.findItem(R.id.action_sync);
        syncitem.setEnabled(hasBeenInitialized);
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

        try {
            groupArrayList = ServerDatabaseCommunicator.getInstance().getGroupsList(getActiveUser());
            notificationArrayList = ServerDatabaseCommunicator.getInstance().getNotifications();
            new SyncDataWithServices(notificationArrayList, dbHandler).syncData();
        } catch (ServicesAvailabilityException e) {
            e.printStackTrace();
        }

        new UnsubscribeNotificationGroups(this).execute();
        joinODKGroups(groupArrayList);

        addMenuItemInNavMenuDrawer();

        notificationAdapter.notifyDataSetChanged();
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

        setUserName();

        if(hasBeenInitialized){
            getDeepLink();
        }
        try {

            ServerDatabaseCommunicator.getInstance().init(getDatabase(), getAppName());

            groupArrayList = ServerDatabaseCommunicator.getInstance().getGroupsList(getActiveUser());

            addMenuItemInNavMenuDrawer();

        } catch (ServicesAvailabilityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ActionNotAuthorizedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void databaseUnavailable() {

        if (mIOdkDataDatabaseListener != null) {
            mIOdkDataDatabaseListener.databaseUnavailable();
        }
        Toast.makeText(getApplicationContext(),"Database not found", Toast.LENGTH_LONG).show();
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
                                final String id = map.get("id");
                                if(id!=null && !id.equals("")) {
                                    try {
                                        ServerDatabaseCommunicator.getInstance().addGroup(id);
                                    } catch (ServicesAvailabilityException e) {
                                        e.printStackTrace();
                                    } catch (ActionNotAuthorizedException e) {
                                        e.printStackTrace();
                                    }
                                    addMenuItemInNavMenuDrawer();
                                    new SubscribeNotificationGroup(MainActivity.this, id, loggedInUsername).execute();

                                    Toast.makeText(getApplicationContext(),"Successfully joined the Group",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Error in Joining group",Toast.LENGTH_LONG).show();
                                }
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

    public void joinODKGroups(ArrayList<Group> groupArrayList){
        for(Group group: groupArrayList) {
            new SubscribeNotificationGroup(this, group.getId(), loggedInUsername).execute();
        }
        dbHandler.newGroupDatabase(groupArrayList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                if (!hasBeenInitialized) {
                    readConfigFile();
                }
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.storage_permission_rationale)
                        .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //For pre Marshmallow devices, this wouldn't be called as they don't need runtime permission.
                                    requestPermissions(
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE);
                                }
                            }
                        })
                        .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                                MainActivity.this.finish();
                            }
                        });
                builder.create().show();
            }
        }
    }

    private void readConfigFile() {
        try {
            String json = loadJSONFromAsset();
            if(json!=null) {
                JSONObject obj = new JSONObject(json);
                //System.out.print(obj.toString());
                String databaseUrl = obj.getJSONObject("project_info").getString("firebase_url");
                String storageBucket = obj.getJSONObject("project_info").getString("storage_bucket");
                String applicationId = obj.getJSONArray("client").getJSONObject(0).getJSONObject("client_info").getString("mobilesdk_app_id");
                String apiKey = obj.getJSONArray("client").getJSONObject(0).getJSONArray("api_key").getJSONObject(0).getString("current_key");

                FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                        .setApplicationId(applicationId)
                        .setApiKey(apiKey)
                        .setDatabaseUrl(databaseUrl)
                        .setStorageBucket(storageBucket);
                FirebaseApp.initializeApp(this, builder.build());
                isInitialized(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void isInitialized(boolean b) {
        hasBeenInitialized=b;
        if(syncitem!=null)
        syncitem.setEnabled(b);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(

                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                    //For pre Marshmallow devices, this wouldn't be called as they don't need runtime permission.
                    requestPermissions(
                            STORAGE_PERMISSION,
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE
                    );
            }
            else{
                // Permission has been granted. Read config file.
                if (!hasBeenInitialized) {
                    readConfigFile();
                }

            }
        }
    }

    @Override
    public void filterByGroup(String group) {
        notificationAdapter.filterByGroup(group);
        filteredGrp = group;
    }

    @Override
    public void sort(String field) {
        notificationAdapter.sort(field);
        sortedOrder = field;
    }
}
