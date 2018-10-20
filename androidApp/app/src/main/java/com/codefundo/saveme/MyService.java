package com.codefundo.saveme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.codefundo.saveme.auth.LoginActivity;
import com.codefundo.saveme.models.VictimData;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

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

        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        MobileServiceTable<VictimData> table = mClient.getTable(VictimData.class);
        table.where().field("id").eq(LoginActivity.getDeviceIMEI(this))
                .execute((result, count, exception, response) -> {
            VictimData data;
            data = result.get(0);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                data.setCurrentLat(latitude);
                data.setCurrentLong(longitude);
                Log.e("location", latitude + " " + longitude);
                table.update(data);
            }
        });
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
