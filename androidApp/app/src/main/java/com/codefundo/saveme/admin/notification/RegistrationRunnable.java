package com.codefundo.saveme.admin.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.codefundo.saveme.MainActivity;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.Arrays;

public class RegistrationRunnable implements Runnable {
    private static final String TAG = "RegIntentService";
    private String resultString = null;
    private NotificationHub hub;
    private Context ctx;
    private String FCM_TOKEN;

    public RegistrationRunnable(Context ctx, String FCM_TOKEN) {
        this.ctx = ctx;
        this.FCM_TOKEN = FCM_TOKEN;
    }

    @Override
    public void run() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String regID = null;
        String storedToken = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID = sharedPreferences.getString("registrationID", null)) == null)) {

                NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, ctx);
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_TOKEN);
                try {
                    regID = hub.register(FCM_TOKEN).getRegistrationId();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
                }

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_TOKEN).apply();
            }

            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken = sharedPreferences.getString("FCMtoken", "")) != FCM_TOKEN) {

                NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, ctx);
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_TOKEN);
                try {
                    regID = hub.register(FCM_TOKEN).getRegistrationId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_TOKEN).apply();
            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
            }

        } catch (Exception e) {
            Log.e(TAG, resultString = "Failed to complete registration", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify(resultString);
        }

    }
}
