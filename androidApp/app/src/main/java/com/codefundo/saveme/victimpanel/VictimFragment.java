package com.codefundo.saveme.victimpanel;


import android.annotation.SuppressLint;
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

import static com.codefundo.saveme.victimpanel.MapActivity.launchMapActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VictimFragment extends Fragment implements View.OnClickListener {

    private MaterialCardView selfCard;
    private Handler handlerVictims;
    private KillableRunnable runnerVictims;
    private MobileServiceClient mClient;

    public VictimFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment EateriesCardFragment.
     */
    public static VictimFragment newInstance() {
        return new VictimFragment();
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selfCard = (MaterialCardView) inflater.inflate(R.layout.fragment_victim,
                container, false);
        TextView textView = selfCard.findViewById(R.id.stats);
        mClient = SaveMe.getAzureClient(getContext());
        handlerVictims = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int stat = bundle.getInt("stat");
                textView.setText(String.valueOf(stat));
            }
        };

        runnerVictims = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceTable<VictimData> table = mClient.getTable(VictimData.class);
                ListenableFuture<MobileServiceList<VictimData>> listenableFuture = table.where().execute();
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
                        Log.e("Victim Fragment:", "Failed Updating Stats: ", t);
                    }
                });

                //Do something after 10 seconds
                handlerVictims.postDelayed(this, 60000);
            }
        };
        handlerVictims.post(runnerVictims);

        return selfCard;
    }


    @Override
    public void onResume() {
        super.onResume();
        selfCard.setOnClickListener(this);
        if (handlerVictims != null) {
            handlerVictims.post(runnerVictims);
            runnerVictims.restart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        selfCard.setOnClickListener(null);
        if (handlerVictims != null) {
            handlerVictims.removeCallbacks(runnerVictims);
            runnerVictims.kill();
        }
    }

    @Override
    public void onClick(final View view) {
        launchMapActivity(getContext(), "victim");
    }


}
