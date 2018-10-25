package com.codefundo.saveme.admin.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.codefundo.saveme.MainActivity;
import com.codefundo.saveme.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.core.app.NotificationCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;

    public MyFirebaseMessagingService() {
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromiseds. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d("FCM", "Fetch FCM Registration Token");


        RegistrationRunnable registrationAsyncTask = new RegistrationRunnable(this, token);

        (new Thread(registrationAsyncTask)).start();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> nhMessage = remoteMessage.getData();
        String message = nhMessage.get("message");
        sendNotification(message);
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify(message);
        }
    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
