package org.odk.odknotifications;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.google.firebase.iid.FirebaseInstanceId;
import org.odk.odknotifications.Activities.MainActivity;
import java.io.IOException;

public class UnsubscribeNotificationGroups extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;

    public UnsubscribeNotificationGroups(MainActivity activity){
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Unsubscribing from Notification Groups, please wait...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
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
