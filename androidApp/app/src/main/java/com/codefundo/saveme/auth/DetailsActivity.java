package com.codefundo.saveme.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codefundo.saveme.MainActivity;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private UserData item = new UserData();
    private TextView addressTv;
    private TextView cityTv;
    private TextView stateTv;
    private TextView pincodeTv;
    private TextView contactNumberTv;
    private TextView emergencyNumber1Tv;
    private TextView emergencyNumber2Tv;
    private TextView bloodGroupTv;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_form_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //intiatilising the UI
        TextView skipButton = findViewById(R.id.btn_skip);
        TextView nextButton = findViewById(R.id.btn_save);
        addressTv = findViewById(R.id.et_address);
        cityTv = findViewById(R.id.et_city);
        stateTv = findViewById(R.id.et_state);
        pincodeTv = findViewById(R.id.et_pincode);
        contactNumberTv = findViewById(R.id.et_contact_number);
        emergencyNumber1Tv = findViewById(R.id.et_emergency_number_1);
        emergencyNumber2Tv = findViewById(R.id.et_emergency_number_2);
        bloodGroupTv = findViewById(R.id.et_blood_group);
        progressBar = findViewById(R.id.progress_bar);

        skipButton.setOnClickListener(v -> {
            getBasicData();
            pushToDatabase();
            startActivity(new Intent(DetailsActivity.this, MainActivity.class));

        });
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        getBasicData();
        item.setAddress(addressTv.getText().toString());
        item.setContactNumber(contactNumberTv.getText().toString());
        item.setEmergencyNumber1(emergencyNumber1Tv.getText().toString());
        item.setEmergencyNumber2(emergencyNumber2Tv.getText().toString());
        item.setBloodGroup(bloodGroupTv.getText().toString());
        item.setCity(cityTv.getText().toString());
        item.setState(stateTv.getText().toString());
        item.setPincode(pincodeTv.getText().toString());
        pushToDatabase();
    }


    private void getBasicData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = telephonyManager.getDeviceId();
                item.setId(deviceId);

            }
            item.setName(account.getDisplayName());
            item.setEmailAddress(account.getEmail());
            item.setPhotoUrl(account.getPhotoUrl().toString());
            item.setAzureId(LoginActivity.getCurrentUserUniqueId(this));
            item.setMemberType("User");
        }
    }

    private void pushToDatabase() {
        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        if (mClient != null) {
            MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
            table.insert(item, (entity, exception, response) -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }

    }

}
