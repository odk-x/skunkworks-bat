package org.odk.odknotifications;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.odk.odknotifications.Activities.MainActivity;

public class SubscribeNotificationGroup extends AsyncTask<Void, Void, Void> {

    private ProgressDialog dialog;
    private String groupId;
    private String userId;

    public SubscribeNotificationGroup(MainActivity activity, String groupId, String userId){
        dialog = new ProgressDialog(activity);
        this.groupId = groupId;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Joining Notification Groups, Please wait...");
        try{
            dialog.show();
        }catch (Exception e){
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

            FirebaseMessaging.getInstance().subscribeToTopic(this.groupId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Success";
                            if (!task.isSuccessful()) {
                                msg = "Faiure";
                            }
                            Log.e("TAG", msg);
                        }
                    });

            return null;
    }

    @Override
    protected void onPostExecute (Void result){
        super.onPostExecute(result);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}