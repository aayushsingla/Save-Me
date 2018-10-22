package com.codefundo.saveme.report;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codefundo.saveme.KillableRunnable;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.MissingPeopleData;
import com.codefundo.saveme.models.VictimData;
import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements View.OnClickListener {

    private MaterialCardView selfCard;
    private Handler handlerReport;
    private KillableRunnable runnerReport;
    private MobileServiceClient mClient;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment EateriesCardFragment.
     */
    public static ReportFragment newInstance() {
        return new ReportFragment();
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selfCard = (MaterialCardView) inflater.inflate(R.layout.fragment_report,
                container, false);
        TextView textView = selfCard.findViewById(R.id.stats);
        mClient = SaveMe.getAzureClient(getContext());
        handlerReport = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int stat = bundle.getInt("stat");
                textView.setText(String.valueOf(stat));
            }
        };

        runnerReport = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceTable<MissingPeopleData> table = mClient.getTable(MissingPeopleData.class);
                ListenableFuture<MobileServiceList<MissingPeopleData>> listenableFuture = table.where().execute();
                Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<MissingPeopleData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<MissingPeopleData> result) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stat", result.getTotalCount());
                        Message message = new Message();
                        message.setData(bundle);
                        handlerReport.dispatchMessage(message);

                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        Log.e("Victim Fragment:", "Failed Updating Stats: ", t);
                    }
                });

                //Do something after 10 seconds
                handlerReport.postDelayed(this, 60000);
            }
        };
        handlerReport.post(runnerReport);

        return selfCard;
    }


    @Override
    public void onResume() {
        super.onResume();
        selfCard.setOnClickListener(this);
        if (handlerReport != null) {
            handlerReport.post(runnerReport);
            runnerReport.restart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        selfCard.setOnClickListener(null);
        if (handlerReport != null) {
            handlerReport.removeCallbacks(runnerReport);
            runnerReport.kill();
        }
    }

    @Override
    public void onClick(final View view) {
        final Intent adminActivityIntent = new Intent(view.getContext(), ReportActivity.class);
        startActivity(adminActivityIntent);
    }


}
