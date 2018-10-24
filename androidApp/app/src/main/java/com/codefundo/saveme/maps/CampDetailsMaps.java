package com.codefundo.saveme.maps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.CampData;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.fragment.app.DialogFragment;

public class CampDetailsMaps extends DialogFragment {
    private TextView nameTv;
    private TextView addressTv;
    private TextView postalCodeTv;
    private TextView typeTv;
    private TextView distanceTv;
    private Location myLocation;
    private Location destination;
    private String id;
    private Button buttonReachHim;
    private Button buttonOk;

    public static CampDetailsMaps newInstance(String id, Location mylocation, Location destination) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putParcelable("location", mylocation);
        bundle.putParcelable("destination", destination);
        CampDetailsMaps campDetailsMaps = new CampDetailsMaps();
        campDetailsMaps.setArguments(bundle);
        return campDetailsMaps;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.fragment_map_camp_details, null);
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
            myLocation = bundle.getParcelable("location");
            destination = bundle.getParcelable("destination");
        }

        initUi(view);

        fetchCampDataFromDB(id);
        getDistanceBetweenLocation(myLocation, destination);

        buttonOk.setOnClickListener(v -> getDialog().dismiss());
        buttonReachHim.setOnClickListener(v -> getDirectionsBetweenLocations(destination));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();


    }

    private void getDirectionsBetweenLocations(Location destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.getLatitude() + "," + destination.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getContext().getPackageManager()) == null) {
            Toast.makeText(getContext(), "Please install Google Maps on your device to perform this action.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(mapIntent);
    }

    private void getDistanceBetweenLocation(Location myLocation, Location destination) {
        float distanceInMeters = myLocation.distanceTo(destination);
        if (distanceInMeters / 1000 > 1)
            distanceTv.setText(String.format("%s Km", distanceInMeters / 1000));
        else
            distanceTv.setText(String.format("%s m", distanceInMeters));
    }

    private void fetchCampDataFromDB(String id) {
        MobileServiceClient mClient = SaveMe.getAzureClient(getContext());
        MobileServiceTable<CampData> table = mClient.getTable(CampData.class);
        ListenableFuture<MobileServiceList<CampData>> listenableFuture = table.where().field("id").eq(id).execute();
        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<CampData>>() {
            @Override
            public void onSuccess(MobileServiceList<CampData> result) {
                CampData campData = result.get(0);
                nameTv.setText(campData.getName());
                addressTv.setText(campData.getAddress());
                postalCodeTv.setText(campData.getPostalCode());
                typeTv.setText(campData.getType());
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                Log.e("Failed Fetching Data", t.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void initUi(View view) {
        nameTv = view.findViewById(R.id.et_name);
        addressTv = view.findViewById(R.id.et_address);
        postalCodeTv = view.findViewById(R.id.et_postal_code);
        typeTv = view.findViewById(R.id.et_type);
        distanceTv = view.findViewById(R.id.et_distance);
        buttonOk = view.findViewById(R.id.btn_ok);
        buttonReachHim = view.findViewById(R.id.btn_reach_him);
    }

}
