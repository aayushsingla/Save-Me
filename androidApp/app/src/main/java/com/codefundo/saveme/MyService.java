package com.codefundo.saveme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.codefundo.saveme.models.UserData;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.net.MalformedURLException;
import java.util.List;

public class MyService extends Service {
    public MyService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public IBinder onBind(Intent intent) {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        MobileServiceClient mClient = null;
        try {
            mClient = new MobileServiceClient(
                    "https://rescue-mission.azurewebsites.net",
                    this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
        table.where().field("id").eq(deviceId).execute(new TableQueryCallback<UserData>() {
            @Override
            public void onCompleted(List<UserData> result, int count, Exception exception, ServiceFilterResponse response) {
                UserData data = new UserData();
                data = result.get(0);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    data.currentLat = latitude;
                    data.currentLong = longitude;

                    table.update(data);

                }
            }
        });
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
