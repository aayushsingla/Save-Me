package com.codefundo.saveme.rescueteam;


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
import com.codefundo.saveme.models.VictimData;
import com.codefundo.saveme.models.VolunteerData;
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
public class RescueFragment extends Fragment implements View.OnClickListener {

    private MaterialCardView selfCard;
    private MobileServiceClient mClient;
    private Handler handlerVictims;
    private KillableRunnable runnerVictims;
    private Handler handlerVolunteers;
    private KillableRunnable runnerVolunteers;
    private TextView victim;
    private TextView volunteer;

    public RescueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment EateriesCardFragment.
     */
    public static RescueFragment newInstance() {
        return new RescueFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selfCard = (MaterialCardView) inflater.inflate(R.layout.fragment_rescue,
                container, false);
        volunteer = selfCard.findViewById(R.id.stat_volunteer);
        victim = selfCard.findViewById(R.id.stat_victim);

        mClient = SaveMe.getAzureClient(getContext());
        retrieveNumberOfVolunteers();

        retrieveNumberOfVictimsSaved();

        return selfCard;
    }

    @SuppressLint("HandlerLeak")
    private void retrieveNumberOfVolunteers() {
        handlerVolunteers = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int stat = bundle.getInt("stat");
                volunteer.setText(String.valueOf(stat));
                Log.e("tag", stat + "");
            }
        };

        runnerVolunteers = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceTable<VolunteerData> table = mClient.getTable(VolunteerData.class);
                ListenableFuture<MobileServiceList<VolunteerData>> listenableFuture = table.where().includeInlineCount().execute();
                Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VolunteerData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<VolunteerData> result) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stat", result.getTotalCount());
                        Log.e("tag", String.valueOf(result.get(0)));
                        Log.e("tag", String.valueOf(result.getTotalCount()));
                        Message message = new Message();
                        message.setData(bundle);
                        handlerVolunteers.dispatchMessage(message);

                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        Log.e("Rescue Fragment:", "Failed Updating VolunteerStats: " + t.getMessage());
                    }
                });

                //Do something after 10 seconds
                handlerVolunteers.postDelayed(this, 60000);
            }
        };
        handlerVolunteers.post(runnerVolunteers);

    }

    @SuppressLint("HandlerLeak")
    private void retrieveNumberOfVictimsSaved() {
        handlerVictims = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int stat = bundle.getInt("stat");
                victim.setText(String.valueOf(stat));
            }
        };

        runnerVictims = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceTable<VictimData> table = mClient.getTable(VictimData.class);
                ListenableFuture<MobileServiceList<VictimData>> listenableFuture = table.where().field("status").eq("safe").execute();
                Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VictimData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<VictimData> result) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stat", result.getTotalCount());
                        Message message = new Message();
                        message.setData(bundle);
                        handlerVictims.dispatchMessage(message);

                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        Log.e("Rescue Fragment:", "Failed Updating VictimStats: " + t.getMessage());
                    }
                });
                //Do something after 10 seconds
                handlerVictims.postDelayed(this, 60000);
            }
        };
        handlerVictims.post(runnerVictims);

    }


    @Override
    public void onResume() {
        super.onResume();
        selfCard.setOnClickListener(this);
        if (handlerVictims != null) {
            handlerVictims.post(runnerVictims);
            runnerVictims.restart();
        }

        if (handlerVolunteers != null) {
            handlerVolunteers.post(runnerVolunteers);
            runnerVolunteers.restart();
        }
    }

    @Override
    public void onPause() {
        selfCard.setOnClickListener(null);
        if (handlerVictims != null) {
            handlerVictims.removeCallbacks(runnerVictims);
            runnerVictims.kill();
        }
        if (handlerVolunteers != null) {
            handlerVolunteers.removeCallbacks(runnerVolunteers);
            runnerVolunteers.kill();

        }
        super.onPause();
    }

    @Override
    public void onClick(final View view) {
        final Intent rescueActivityIntent = new Intent(view.getContext(), QRCodeScannerActivity.class);
        startActivity(rescueActivityIntent);
    }
}
