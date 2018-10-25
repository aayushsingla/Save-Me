package com.codefundo.saveme.donations;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.Donation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DonationFormActivity extends AppCompatActivity {
    private TextView nameTv, amountTv, phoneTv, emailTv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> new AlertDialog.Builder(DonationFormActivity.this)
                .setTitle("Pay Now")
                .setMessage("Do you want to make the donation of amount INR : " + amountTv.getText().toString())
                .setPositiveButton("Submit", (dialog, which) -> {
                    pushUserData(Long.toHexString((new Random()).nextLong()));
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .show());

        nameTv = findViewById(R.id.et_name);
        amountTv = findViewById(R.id.et_amount);
        phoneTv = findViewById(R.id.et_contact_number);
        emailTv = findViewById(R.id.et_contact_email);
        progressBar = findViewById(R.id.progress_bar);
    }


    private void pushUserData(String id) {
        MobileServiceClient mobileServiceClient = SaveMe.getAzureClient(this);
        MobileServiceTable<Donation> table = mobileServiceClient.getTable(Donation.class);
        Donation donation = new Donation();

        donation.setId(id);
        donation.setName(nameTv.getText().toString() + "");
        donation.setAmount(Integer.parseInt(amountTv.getText().toString()));
        donation.setEmail(emailTv.getText().toString());
        donation.setPhoneNumber(phoneTv.getText().toString());

        ListenableFuture<Donation> listenableFuture = table.insert(donation);
        Futures.addCallback(listenableFuture, new FutureCallback<Donation>() {
            @Override
            public void onSuccess(Donation result) {
                hideShowProgressBar();
                new AlertDialog.Builder(DonationFormActivity.this)
                        .setTitle("Pay Now")
                        .setMessage("Do you want to make the donation of amount INR : " + amountTv.getText().toString())
                        .setPositiveButton("Ok", (dialog, which) -> finish())
                        .show();
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NotNull Throwable t) {
                Log.e("DonationFormActivity:error", t.getMessage());
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
