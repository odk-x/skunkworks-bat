package org.odk.odknotifications.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Model.Response;

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
            Response response = new Response(message,username,time);
            DatabaseReference responesRef = FirebaseDatabase.getInstance().getReference("/responses/"+notificationID).push();
            String responseID = responesRef.getKey();
            responesRef.setValue(response);
            response.setResponseID(responseID);
            dbHandler.addResponse(response,notificationID);
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
