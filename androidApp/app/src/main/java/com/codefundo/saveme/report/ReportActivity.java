package com.codefundo.saveme.report;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.codefundo.saveme.KillableRunnable;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.MissingPeopleData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReportActivity extends AppCompatActivity {
    private MobileServiceList<MissingPeopleData> serviceList;
    private ArrayList<MissingPeopleData> list=new ArrayList<>(0);
    private FormAdapter formAdapter;
    private Handler handler;
    private KillableRunnable killableRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        serviceList= new MobileServiceList<>(list, 0);
        MissingPeopleData missingPeopleData=new MissingPeopleData();
        missingPeopleData.setName("Aayush Singla");
        missingPeopleData.setAge(18);
        serviceList.add(missingPeopleData);
        Log.e("Tag:count",""+serviceList.getTotalCount());
        FloatingActionButton fab=findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(ReportActivity.this,FormActivity.class)));

        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        formAdapter= new FormAdapter(serviceList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(formAdapter);

        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        handler=new Handler();
        killableRunnable=new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceClient mClient= SaveMe.getAzureClient(ReportActivity.this);
                MobileServiceTable<MissingPeopleData> table=mClient.getTable(MissingPeopleData.class);
                ListenableFuture<MobileServiceList<MissingPeopleData>> listListenableFuture= table.where().execute();
                Futures.addCallback(listListenableFuture, new FutureCallback<MobileServiceList<MissingPeopleData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<MissingPeopleData> result) {
                        serviceList=result;
                        formAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        Log.e("Report Activity:Error: ",t.getMessage());
                    }
                });

                handler.postDelayed(killableRunnable,60000);
            }
        };
        handler.post(killableRunnable);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(killableRunnable!=null && handler!=null){
            killableRunnable.restart();
            handler.post(killableRunnable);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(killableRunnable!=null && handler!=null){
            killableRunnable.kill();
            handler.removeCallbacks(killableRunnable);
        }
    }
}
