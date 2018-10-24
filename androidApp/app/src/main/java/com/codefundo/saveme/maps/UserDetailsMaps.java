package com.codefundo.saveme.maps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.UserData;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.fragment.app.DialogFragment;

public class UserDetailsMaps extends DialogFragment {
    private TextView nameTv;
    private TextView ageTv;
    private TextView cityTv;
    private TextView stateTv;
    private TextView genderTv;
    private TextView distanceTv;
    private SimpleDraweeView simpleDraweeView;
    private Location myLocation;
    private Location destination;
    private String id;
    private Button buttonReachHim;
    private Button buttonOk;

    public static UserDetailsMaps newInstance(String id, Location mylocation, Location destination) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putParcelable("location", mylocation);
        bundle.putParcelable("destination", destination);
        UserDetailsMaps userDetailsMaps = new UserDetailsMaps();
        userDetailsMaps.setArguments(bundle);
        return userDetailsMaps;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.fragment_map_user_details, null);
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
            myLocation = bundle.getParcelable("location");
            destination = bundle.getParcelable("destination");
        }

        initUi(view);

        fetchUserDataFromDB(id);
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

    private void fetchUserDataFromDB(String id) {
        MobileServiceClient mClient = SaveMe.getAzureClient(getContext());
        MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
        ListenableFuture<MobileServiceList<UserData>> listenableFuture = table.where().field("id").eq(id).execute();
        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<UserData>>() {
            @Override
            public void onSuccess(MobileServiceList<UserData> result) {
                UserData userData = result.get(0);
                nameTv.setText(userData.getName());
                ageTv.setText(userData.getAge());
                cityTv.setText(userData.getCity());
                stateTv.setText(userData.getState());
                genderTv.setText(userData.getGender());
                if (userData.getPhotoUrl().matches("")) {
                    simpleDraweeView.setImageResource(R.drawable.image_default_profile);
                } else {
                    simpleDraweeView.setImageURI(userData.getPhotoUrl());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void initUi(View view) {
        nameTv = view.findViewById(R.id.et_name);
        ageTv = view.findViewById(R.id.et_age);
        cityTv = view.findViewById(R.id.et_city);
        stateTv = view.findViewById(R.id.et_state);
        genderTv = view.findViewById(R.id.et_gender);
        distanceTv = view.findViewById(R.id.et_distance);
        simpleDraweeView = view.findViewById(R.id.image_victim);
        buttonOk = view.findViewById(R.id.btn_ok);
        buttonReachHim = view.findViewById(R.id.btn_reach_him);
    }

}
