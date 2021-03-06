package com.codefundo.saveme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.admin.AdminFragment;
import com.codefundo.saveme.donations.DonationFragment;
import com.codefundo.saveme.report.ReportFragment;
import com.codefundo.saveme.rescueteam.RescueFragment;
import com.codefundo.saveme.victimpanel.VictimFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class HomeFragment extends Fragment {

    private final static String TAG_RESCUE_FRAGMENT = "RescueFragment";
    private final static String TAG_VICTIM_FRAGMENT = "VictimFragment";
    private static final String TAG_ADMIN_FRAGMENT = "AdminFragment";
    private static final String TAG_REPORT_FRAGMENT = "ReportFragment";
    private static final String TAG_DONATION_FRAGMENT = "DonationFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadFragments();
        return view;
    }

    private void loadFragments() {
        final FragmentManager featuresFragManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = featuresFragManager.beginTransaction();
        if (featuresFragManager.getFragments().size() == 0) {

            fragmentTransaction.add(R.id.container_fragments_home, new VictimFragment(), TAG_VICTIM_FRAGMENT);
            fragmentTransaction.add(R.id.container_fragments_home, new RescueFragment(), TAG_RESCUE_FRAGMENT);
            fragmentTransaction.add(R.id.container_fragments_home, new DonationFragment(), TAG_DONATION_FRAGMENT);
            fragmentTransaction.add(R.id.container_fragments_home, new ReportFragment(), TAG_REPORT_FRAGMENT);
            fragmentTransaction.add(R.id.container_fragments_home, new AdminFragment(), TAG_ADMIN_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

}
