package android.notifications.odk.org.odknotifications;

import android.app.ProgressDialog;
import android.notifications.odk.org.odknotifications.Activities.MainActivity;
import android.notifications.odk.org.odknotifications.Model.Group;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class SubscribeNotificationGroup extends AsyncTask<Void, Void, Void> {

    private ProgressDialog dialog;
    private ArrayList<Group> groupArrayList;
    private String userId;

    public SubscribeNotificationGroup(MainActivity activity, ArrayList<Group> groupArrayList, String userId){
        dialog = new ProgressDialog(activity);
        this.groupArrayList = groupArrayList;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Joining Notification Groups, Please wait...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (Group group : groupArrayList) {
            FirebaseMessaging.getInstance().subscribeToTopic(group.getName())
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
        }
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