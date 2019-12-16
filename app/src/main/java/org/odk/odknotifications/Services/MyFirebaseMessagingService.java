package org.odk.odknotifications.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.odk.odknotifications.Activities.MainActivity;
import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "notification_channel_1";
    public static final String KEY_NOTIFICATION_ID = "notificationID";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "From: " + String.valueOf(remoteMessage.getData()));

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String,String> receivedData = remoteMessage.getData();
            if(receivedData.containsKey("img")&&receivedData.get("img")!=null) {
                Random random = new Random();
                File img_file = new File(getApplicationContext().getFilesDir(), random.nextInt(100000) +".png");
                BufferedInputStream inputStream;
                FileOutputStream fileOutputStream ;
                try {
                    inputStream = new BufferedInputStream( new URL(receivedData.get("img")).openStream());
                    fileOutputStream = new FileOutputStream(img_file);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    System.out.println("Error in downloading file");
                    e.printStackTrace();
                }
                finally {
                    Notification notification = new Notification(receivedData.get("id"), receivedData.get("title"), receivedData.get("message"), remoteMessage.getSentTime(), receivedData.get("group"), receivedData.get("type"), img_file.getAbsolutePath());
                    if (notification.getType().compareTo(Notification.SIMPLE) == 0) {
                        sendSimpleNotification(notification);
                    } else if (notification.getType().compareTo(Notification.INTERACTIVE) == 0) {
                        sendInteractiveNotification(notification);
                    }
                }
            }
            else{
                Notification notification = new Notification(receivedData.get("id"), receivedData.get("title"), receivedData.get("message"), remoteMessage.getSentTime(), receivedData.get("group"), receivedData.get("type"), null);
                if (notification.getType().compareTo(Notification.SIMPLE) == 0) {
                    sendSimpleNotification(notification);
                } else if (notification.getType().compareTo(Notification.INTERACTIVE) == 0) {
                    sendInteractiveNotification(notification);
                }
            }

            }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(org.odk.odknotifications.Services.MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    private void sendSimpleNotification(Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getMessage())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if(notification.getImg_uri()!=null){
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeFile(notification.getImg_uri())));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        DBHandler dbHandler = new DBHandler(this,null,null,1);
        dbHandler.addNotification(notification);
        System.out.println("Date:"+notification.getDate());
    }

    private void sendInteractiveNotification(Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String replyLabel = "Enter your reply here";
        //Initialise RemoteInput
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent replyIntent = new Intent(this, NotificationReplyReceiver.class);
            replyIntent.setAction(NotificationReplyReceiver.ACTION_REPLY);
            replyIntent.putExtra(KEY_NOTIFICATION_ID,notification.getId());
            replyActionPendingIntent = PendingIntent.getBroadcast(this, 878, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            replyActionPendingIntent = pendingIntent;
        }

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "REPLY", replyActionPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getMessage())
                        .setAutoCancel(true)
                        .addAction(replyAction)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if(notification.getImg_uri()!=null){
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeFile(notification.getImg_uri())));
        }

        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 76, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        DBHandler dbHandler = new DBHandler(this,null,null,1);
        dbHandler.addNotification(notification);
        System.out.println("Date:"+notification.getDate());
    }
}
