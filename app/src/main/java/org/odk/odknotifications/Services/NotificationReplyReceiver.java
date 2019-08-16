package org.odk.odknotifications.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import org.odk.odknotifications.Activities.MainActivity;
import org.odk.odknotifications.utils.ResponseHandler;

import java.util.Date;

import static org.odk.odknotifications.Services.MyFirebaseMessagingService.CHANNEL_ID;
import static org.odk.odknotifications.Services.MyFirebaseMessagingService.KEY_NOTIFICATION_ID;
import static org.odk.odknotifications.Services.MyFirebaseMessagingService.KEY_TEXT_REPLY;
import static org.odk.odknotifications.Services.MyFirebaseMessagingService.NOTIFICATION_ID;

public class NotificationReplyReceiver extends BroadcastReceiver {

    public static final String ACTION_REPLY =
            "org.odk.odknotifications.Services.action.REPLY";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REPLY.equals(action)) {
                String notificationID = intent.getStringExtra(KEY_NOTIFICATION_ID);
                String message = String.valueOf(getMessage(intent));
                ResponseHandler responseHandler = new ResponseHandler(context);
                boolean isDone = responseHandler.saveResponse(notificationID, message,new Date().getTime());
                if(isDone){
                    processInlineReply(context, message);
                }

            }
        }
    }

    private void processInlineReply(Context context, String message) {

        if (message != null) {

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            //Update the notification to show that the reply was received.
            NotificationCompat.Builder repliedNotification =
                    new NotificationCompat.Builder(context,CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.stat_notify_chat)
                            .setContentTitle("Response recorded successfully")
                            .setContentText("Response: "+message)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID,
                    repliedNotification.build());
        }
    }

    private CharSequence getMessage(Intent intent) {
        //getting the remote input bundle from intent
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}
