package com.codefundo.saveme.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private UserData item = new UserData();
    private TextView addressTv;
    private TextView cityTv;
    private TextView stateTv;
    private TextView pincodeTv;
    private TextView ageTv;
    private TextView genderTv;
    private TextView contactNumberTv;
    private TextView emergencyNumber1Tv;
    private TextView emergencyNumber2Tv;
    private TextView bloodGroupTv;
    private ProgressBar progressBar;
    private boolean firstSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_form_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialising the UI
        TextView skipButton = findViewById(R.id.btn_skip);
        FloatingActionButton nextButton = findViewById(R.id.fab);
        addressTv = findViewById(R.id.et_address);
        cityTv = findViewById(R.id.et_city);
        ageTv = findViewById(R.id.et_age);
        genderTv = findViewById(R.id.et_gender);
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
        checkFirstSignUp();
    }

    private void checkFirstSignUp() {
        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        MobileServiceTable<UserData> table = mClient.getTable(UserData.class);

        ListenableFuture<MobileServiceList<UserData>> listenableFuture =
                table.where().field("id").eq(LoginActivity.getDeviceIMEI(this)).execute();

        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<UserData>>() {
            @Override
            public void onSuccess(MobileServiceList<UserData> result) {
                if (result.getTotalCount() > 0) {
                    UserData data = result.get(0);
                    ageTv.setText(data.getAge());
                    genderTv.setText(data.getGender());
                    addressTv.setText(data.getAddress());
                    cityTv.setText(data.getCity());
                    stateTv.setText(data.getState());
                    pincodeTv.setText(data.getPincode());
                    emergencyNumber1Tv.setText(data.getEmergencyNumber1());
                    emergencyNumber2Tv.setText(data.getEmergencyNumber2());
                    contactNumberTv.setText(data.getContactNumber());
                    bloodGroupTv.setText(data.getBloodGroup());
                    firstSignIn = false;
                } else {
                    firstSignIn = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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
        item.setAge(ageTv.getText().toString());
        item.setGender(genderTv.getText().toString());
        item.setPincode(pincodeTv.getText().toString());
        pushToDatabase();
    }


    private void getBasicData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        item.setId(LoginActivity.getDeviceIMEI(this));
        item.setName(account.getDisplayName());
        item.setEmailAddress(account.getEmail());
        item.setPhotoUrl(account.getPhotoUrl().toString());
        item.setAzureId(LoginActivity.getCurrentUserUniqueId(this));
        item.setMemberType("User");
    }

    private void pushToDatabase() {
        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        if (mClient != null) {
            MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
            ListenableFuture<UserData> listenableFuture;
            if (firstSignIn) {
                listenableFuture = table.insert(item);
                Log.e("TAG", "inserted");
            } else {
                listenableFuture = table.update(item);
                Log.e("TAG", "updated");
            }

            Futures.addCallback(listenableFuture, new FutureCallback<UserData>() {
                @Override
                public void onSuccess(UserData result) {
                    startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("DetailsActivity:", "Inserting item in the database failed");
                }
            });

        }
    }

}
