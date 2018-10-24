package com.codefundo.saveme.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.CampData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.schibstedspain.leku.LocationPickerActivity;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.provider.ContactsContract.CommonDataKinds.Email.ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

public class CampMarkingActivity extends AppCompatActivity {
    private MobileServiceClient mClient;
    private Double latitude = null;
    private Double longitude = null;
    private String address = "";
    private String postalcode = "";

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
                latitude = data.getDoubleExtra(LATITUDE, 0.0);
                longitude = data.getDoubleExtra(LONGITUDE, 0.0);
                postalcode = data.getStringExtra(ZIPCODE);
                address = data.getStringExtra(ADDRESS);

//                mClient.getSyncTable(Camp.class).insert(camp);
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            longitude = null;
            latitude = null;
            postalcode = "";
            address = "";
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
        if (latitude == null || longitude == null || postalcode.matches("") || address.matches("")) {
            new AlertDialog.Builder(this)
                    .setMessage("Fields cannot be left blank. Please mention all the details.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        CampData camp = new CampData();
        Random random = new Random();
        camp.setLongitude(longitude);
        camp.setLatitude(latitude);
        camp.setAddress(address);
        camp.setPostalCode(postalcode);
//        camp.setName();
//        camp.setType();
//        camp.setCreatorAzureId();
//        camp.setId(Long.toHexString(random.nextLong()));
    }
}
