package org.odk.odknotifications;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.odk.odknotifications.Activities.MainActivity;
import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Model.Notification;

import java.util.ArrayList;

public class SyncDataWithServices extends AsyncTask<Void,Void,Void> {

    private ProgressDialog progressDialog;
    private ArrayList<Notification>notificationArrayList;
    private DBHandler dbHandler;

    public SyncDataWithServices(MainActivity activity , ArrayList<Notification>notificationArrayList , DBHandler dbHandler){
        this.progressDialog = new ProgressDialog(activity);
        this.notificationArrayList = notificationArrayList;
        this.dbHandler = dbHandler;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Syncing data with services app, please wait...");
        try{
            progressDialog.show();
        }catch (Exception e){
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        dbHandler.clearTable(DBHandler.TABLE_NOTIFICATIONS);

        for(Notification notification : notificationArrayList){
            dbHandler.addNotification(notification);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
