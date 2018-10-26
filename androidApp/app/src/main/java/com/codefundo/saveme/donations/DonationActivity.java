package com.codefundo.saveme.donations;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.codefundo.saveme.KillableRunnable;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.Donation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DonationActivity extends AppCompatActivity {

    private ArrayList<Donation> list = new ArrayList<>(0);
    private DonationsAdapter donationsAdapter;
    private Handler handler;
    private KillableRunnable killableRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(DonationActivity.this, DonationFormActivity.class)));

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        donationsAdapter = new DonationsAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(donationsAdapter);

        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        handler = new Handler();
        killableRunnable = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceClient mClient = SaveMe.getAzureClient(DonationActivity.this);
                MobileServiceTable<Donation> table = mClient.getTable(Donation.class);
                ListenableFuture<MobileServiceList<Donation>> listListenableFuture = table.where().execute();
                Futures.addCallback(listListenableFuture, new FutureCallback<MobileServiceList<Donation>>() {
                    @Override
                    public void onSuccess(MobileServiceList<Donation> result) {
                        list.clear();
                        list.addAll(result);

                        Collections.sort(list);
                        Collections.reverse(list);
                        donationsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        Log.e("Report Activity:Error: ", t.getMessage());
                    }
                });

                handler.postDelayed(killableRunnable, 10000);
            }
        };
        handler.post(killableRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (killableRunnable != null && handler != null) {
            killableRunnable.restart();
            handler.post(killableRunnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (killableRunnable != null && handler != null) {
            killableRunnable.kill();
            handler.removeCallbacks(killableRunnable);
        }
    }
}
