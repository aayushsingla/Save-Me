package com.codefundo.saveme.report;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.auth.LoginActivity;
import com.codefundo.saveme.models.MissingPeopleData;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codefundo.saveme.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class FormActivity extends AppCompatActivity {
    private TextView nameTv;
    private TextView ageTv;
    private TextView addressTv;
    private TextView cityTv;
    private TextView stateTv;
    private TextView pincodeTv;
    private TextView contactNumberTv;
    private TextView reporterContactNumber;
    private TextView relationTv;
    private TextView genderTv;
    private String photoUrl="";
    private ProgressBar progressBar;
    private SimpleDraweeView simpleDraweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> new AlertDialog.Builder(FormActivity.this)
        .setTitle("Report Victim")
        .setMessage("Clicking submit will report the missing victim to us." +
                "Please verify all the details provided carefully." +
                "Details once provided cannot be changed")
        .setPositiveButton("Submit", (dialog, which) -> pushUserData())
        .setNegativeButton("cancel", (dialog, which) -> {})
        .show());

        nameTv = findViewById(R.id.et_name);
        ageTv = findViewById(R.id.et_age);
        relationTv=findViewById(R.id.et_relation);
        addressTv = findViewById(R.id.et_address);
        cityTv = findViewById(R.id.et_city);
        stateTv = findViewById(R.id.et_state);
        pincodeTv = findViewById(R.id.et_pincode);
        contactNumberTv = findViewById(R.id.et_victim_contact_number);
        reporterContactNumber = findViewById(R.id.et_reporter_contact_number);
        progressBar=findViewById(R.id.progress_bar);
        genderTv=findViewById(R.id.et_gender);
        simpleDraweeView=findViewById(R.id.image_victim);
        simpleDraweeView.setOnClickListener(v -> showImagePicker());
    }

    private void showImagePicker() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent, "Select Victim's Picture"), 1);

    }

    private void pushUserData() {
        hideShowProgressBar();
        MobileServiceClient mobileServiceClient= SaveMe.getAzureClient(this);
        MobileServiceTable<MissingPeopleData> table=mobileServiceClient.getTable(MissingPeopleData.class);
        MissingPeopleData missingPeopleData=new MissingPeopleData();
        Random random = new Random();
        missingPeopleData.setId(Long.toHexString(random.nextLong()));
        missingPeopleData.setName(nameTv.getText().toString()+"");
        missingPeopleData.setAge(ageTv.getText().toString()+"");
        missingPeopleData.setGender(genderTv.getText().toString()+"");
        missingPeopleData.setRelation(relationTv.getText().toString()+"");
        missingPeopleData.setAddress(addressTv.getText().toString()+"");
        missingPeopleData.setCity(cityTv.getText().toString()+"");
        missingPeopleData.setState(stateTv.getText().toString()+"");
        missingPeopleData.setPincode(pincodeTv.getText().toString()+"");
        missingPeopleData.setContactNumber(contactNumberTv.getText().toString()+"");
        missingPeopleData.setReporterContactNumber(nameTv.getText().toString()+"");
        missingPeopleData.setReportedByAzureId(LoginActivity.getCurrentUserUniqueId(this)+"");
        missingPeopleData.setReportedById(LoginActivity.getDeviceIMEI(this)+"");
        missingPeopleData.setReporterContactNumber(reporterContactNumber.getText().toString()+"");
        missingPeopleData.setStatus("missing");
        missingPeopleData.setPhotoUrl(photoUrl);
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            missingPeopleData.setEmailAddress(account.getEmail()+"");
        }
        ListenableFuture<MissingPeopleData> listenableFuture=table.insert(missingPeopleData);
        Futures.addCallback(listenableFuture, new FutureCallback<MissingPeopleData>() {
            @Override
            public void onSuccess(MissingPeopleData result) {
                hideShowProgressBar();
                new AlertDialog.Builder(FormActivity.this)
                        .setTitle("Report Victim")
                        .setMessage("Victim Successfully Reported")
                        .setPositiveButton("Ok", (dialog, which) -> finish())
                        .show();
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                Log.e("FormActivity:error",t.getMessage());
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String filePath = Objects.requireNonNull(data.getData()).getPath(); // "/mnt/sdcard/FileName.mp3"

            if (filePath != null) {
                Log.e("filePath:",filePath);
                simpleDraweeView.setImageURI(data.getData());
            } else {
                Toast.makeText(this, "Problem Selecting File.Please check if that file actually exists on your device.", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_LONG).show();
        }
    }

}
