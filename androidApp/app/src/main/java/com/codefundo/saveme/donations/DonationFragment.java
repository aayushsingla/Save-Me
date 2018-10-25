package com.codefundo.saveme.donations;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codefundo.saveme.KillableRunnable;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.Donation;
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
public class DonationFragment extends Fragment implements View.OnClickListener {

    private MaterialCardView selfCard;
    private TextView tv_no_of_donors, tv_total_amount;
    private KillableRunnable killableRunnable = null;
    public DonationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment EateriesCardFragment.
     */
    public static DonationFragment newInstance() {
        return new DonationFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selfCard = (MaterialCardView) inflater.inflate(R.layout.fragment_donations,
                container, false);
        tv_no_of_donors = selfCard.findViewById(R.id.tv_no_of_donors);
        tv_total_amount = selfCard.findViewById(R.id.tv_total_donation);

        update();
        return selfCard;
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
        selfCard.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        selfCard.setOnClickListener(null);
        super.onPause();
    }

    @Override
    public void onClick(final View view) {
        final Intent donationActivityIntent = new Intent(view.getContext(), DonationActivity.class);
        startActivity(donationActivityIntent);
    }

    private void update() {

        final Context context = getContext();
        Handler handler = new Handler();
        killableRunnable = new KillableRunnable() {
            @Override
            public void doWork() {
                MobileServiceClient mClient = SaveMe.getAzureClient(context);
                MobileServiceTable<Donation> table = mClient.getTable(Donation.class);
                ListenableFuture<MobileServiceList<Donation>> listListenableFuture = table.where().execute();
                Futures.addCallback(listListenableFuture, new FutureCallback<MobileServiceList<Donation>>() {
                    @Override
                    public void onSuccess(MobileServiceList<Donation> result) {

                        tv_no_of_donors.setText(String.valueOf(result.size()));
                        int total_donation = 0;
                        for (Donation donation : result)
                            total_donation += donation.getAmount();
                        tv_total_amount.setText(String.valueOf(total_donation));
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
}
