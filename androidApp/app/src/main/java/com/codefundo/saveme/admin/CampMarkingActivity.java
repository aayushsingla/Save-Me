package com.codefundo.saveme.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.auth.LoginActivity;
import com.codefundo.saveme.models.CampData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
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
    private String[] items = new String[]{"Medical Help", "Food Camp"};
    private TextInputEditText nameEt;
    private TextInputEditText addressEt;
    private TextInputEditText postalCodeEt;
    private Spinner spinner;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_marking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        nameEt = findViewById(R.id.et_name);
        addressEt = findViewById(R.id.et_address);
        postalCodeEt = findViewById(R.id.et_postal_code);
        progressBar = findViewById(R.id.progress_bar);

        addNewPlace();
        mClient = SaveMe.getAzureClient(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            new AlertDialog.Builder(CampMarkingActivity.this)
                    .setTitle("Mark a Camp")
                    .setMessage("Please Verify all the details carefully.Camp once added cannot be removed.")
                    .setCancelable(false)
                    .setPositiveButton("Submit", (dialog, which) -> addCamp())
                    .setNegativeButton("Recheck", (dialog, which) -> dialog.dismiss())
                    .show();
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK");
            if (requestCode == 1) {
                latitude = data.getDoubleExtra(LATITUDE, 0.0);
                longitude = data.getDoubleExtra(LONGITUDE, 0.0);
                postalCodeEt.setText(data.getStringExtra(ZIPCODE));
                addressEt.setText(data.getStringExtra(ADDRESS));
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            longitude = null;
            latitude = null;
            Log.d("RESULT****", "CANCELLED");
            Toast.makeText(this, "No Location Selected", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void addNewPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .shouldReturnOkOnBackPressed()
                .build(getApplicationContext());

        startActivityForResult(locationPickerIntent, 1);
    }

    private void addCamp() {
        hideShowProgressBar();
        CampData camp = new CampData();
        Random random = new Random();
        camp.setLongitude(longitude);
        camp.setLatitude(latitude);
        camp.setAddress(addressEt.getText().toString());
        camp.setName(nameEt.getText().toString());
        camp.setPostalCode(addressEt.getText().toString());
        camp.setType(spinner.getSelectedItem().toString());
        camp.setCreatorAzureId(LoginActivity.getCurrentUserUniqueId(this));
        camp.setId(Long.toHexString(random.nextLong()));

        MobileServiceTable<CampData> table = mClient.getTable(CampData.class);
        ListenableFuture<CampData> listenableFuture = table.insert(camp);
        Futures.addCallback(listenableFuture, new FutureCallback<CampData>() {
            @Override
            public void onSuccess(CampData result) {
                hideShowProgressBar();
                new AlertDialog.Builder(CampMarkingActivity.this)
                        .setTitle("Camp Marked")
                        .setMessage("Successfully added the camp")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> finish())
                        .show();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void hideShowProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

}
