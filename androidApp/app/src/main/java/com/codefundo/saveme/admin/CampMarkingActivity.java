package com.codefundo.saveme.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.Camp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.schibstedspain.leku.LocationPickerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.provider.ContactsContract.CommonDataKinds.Email.ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

public class CampMarkingActivity extends AppCompatActivity {
    private MobileServiceClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_marking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addNewPlace();
        mClient = SaveMe.getAzureClient(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addCamp());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK");
            if (requestCode == 1) {
                Double latitude = data.getDoubleExtra(LATITUDE, 0.0);
                Double longitude = data.getDoubleExtra(LONGITUDE, 0.0);
                String postalcode = data.getStringExtra(ZIPCODE);
                String address = data.getStringExtra(ADDRESS);

                Camp camp = new Camp();
                camp.currentLat = latitude;
                camp.currentLong = longitude;
                camp.address = address;
                camp.postalCode = postalcode;

                mClient.getSyncTable(Camp.class).insert(camp);
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED");
        }
    }

    void addNewPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .shouldReturnOkOnBackPressed()
                .build(getApplicationContext());

        startActivityForResult(locationPickerIntent, 1);
    }

    private void addCamp() {

    }
}
