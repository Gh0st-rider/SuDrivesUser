package com.sudrives.sudrives.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;


/**
 *
 */
public class FirebaseMessagService extends FirebaseMessagingService {
    private SessionPref mSessionPref;
    private static final String TAG = FirebaseMessagService.class.getSimpleName();
    private NotificationCompat.Builder notificationBuilder;
    private Uri defaultSoundUri;
    public static final String NOTIFICATION_CHANNEL_ID = "4565";
    public static final String NOTIFICATION_CHANNEL_NAME = "ANDROID CHANNEL";
    private JSONObject object;
    private int x;
    private NotificationUtils notificationUtils;
    private NotificationManager notificationManager;
    private String notification_type, msg, responseCancel, dateTime, response;
    private Intent intent;

    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mSessionPref = new SessionPref(getApplicationContext());

        if (remoteMessage == null)
            return;

        Map<String, String> data = remoteMessage.getData();
        sendNotification(data.get("title"), data.get("text"));

    }


    /****************************** */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification(String title, String message) {
        try {
            msg = message;
            Random ran = new Random();
            x = ran.nextInt(6) + 5;
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                if (mSessionPref.user_userid != null) {
                    Intent pushNotification = new Intent(ConfigNotif.PUSH_NOTIFICATION_USER);
                    pushNotification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pushNotification.putExtra("response", title);
                    pushNotification.putExtra("notification_type", msg);
                    pushNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                       showNotification(title, msg, pushNotification);
                }

            } //application in background
            else {

                intent = new Intent(this, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("notification", "notification");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                showNotification(title, msg, intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification(String title, String message, Intent intent) {

        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= 26) {

            int notifyID = 1;
            String CHANNEL_ID = "2";// The id of the channel.s
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.s
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_logo)
                    .setColor(getColor(R.color.colorYellow))
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);

// Issue the notification.
            Long tsLong = System.currentTimeMillis() / 1000;
            mNotificationManager.notify((int) (long) tsLong, notification);

        } else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(x/* ID of notification */, notificationBuilder.build());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //Log.e(TAG, "Refreshed token: " + token);
        String refreshedToken = token;

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(ConfigNotif.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

        Config.DEVICE_TOKEN = refreshedToken;
        // Log.v(TAG, "refreshedToken: " + refreshedToken);
        SessionPref.saveDataIntoSharedPref(getApplicationContext(), SessionPref.KEY_DEVICE_TOKEN, refreshedToken);
        //  Log.v(TAG, "refreshedToken: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //   sendRegistrationToServer(token);
    }
}


