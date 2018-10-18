package com.codefundo.saveme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.models.UserData;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        skipButton.setOnClickListener(v -> startActivity(new Intent(DetailsActivity.this, MainActivity.class)));
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        if (mClient != null) {
            MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
        }
        //      getBasicData();
        item.setAddress(addressTv.getText().toString());
        item.setContactNumber(contactNumberTv.getText().toString());
        item.setEmergencyNumber1(emergencyNumber1Tv.getText().toString());
        item.setEmergencyNumber2(emergencyNumber2Tv.getText().toString());
        item.setBloodGroup(bloodGroupTv.getText().toString());
        item.setCity(cityTv.getText().toString());
        item.setState(stateTv.getText().toString());
        item.setPincode(pincodeTv.getText().toString());
    }

    /*
    private void getBasicData() {
        MobileServiceClient mobileServiceClient=SaveMe.getAzureClient();
        item.setName(mobileServiceClient.getCurrentUser());
        item.setEmailAddress(mobileServiceClient.getCurrentUser().getUserId());
        item.setPhotoUrl(mobileServiceClient.getCurrentUser().getUserId());
        item.setId(mobileServiceClient.getCurrentUser().getUserId());
    }
*/

}
