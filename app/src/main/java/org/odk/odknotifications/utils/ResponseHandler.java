package org.odk.odknotifications.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.DatabaseCommunicator.ServerDatabaseCommunicator;
import org.odk.odknotifications.Model.Response;

import java.util.UUID;

public class ResponseHandler {
    SharedPreferences preferences;
    private Context context;
    private DBHandler dbHandler;

    public ResponseHandler(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        dbHandler = new DBHandler(context,null,null,1);
    }
    public boolean saveResponse(String notificationID,String message,long time){
        boolean result = false;
        try{
            String username = preferences.getString("username","anonymous");
            String responseId = UUID.randomUUID().toString();
            Response response = new Response(message,username,time);
            response.setResponseID(responseId);
            response.setNotificationId(notificationID);
            dbHandler.addResponse(response,notificationID);
            ServerDatabaseCommunicator.getInstance().addResponse(response);
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
