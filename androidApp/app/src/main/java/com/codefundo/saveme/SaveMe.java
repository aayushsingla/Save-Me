package com.codefundo.saveme;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class SaveMe extends Application {

    public static MobileServiceClient getAzureClient(Context mContext) {
        try {
            return new MobileServiceClient(
                    "https://rescue-mission.azurewebsites.net", mContext);
        } catch (MalformedURLException e) {
            Toast.makeText(mContext, "We are experiencing some technical issues, please check back later", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }
}
