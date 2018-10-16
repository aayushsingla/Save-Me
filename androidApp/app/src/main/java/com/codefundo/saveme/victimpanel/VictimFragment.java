package com.codefundo.saveme.victimpanel;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.R;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class VictimFragment extends Fragment implements View.OnClickListener {

    private MaterialCardView selfCard;

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selfCard = (MaterialCardView) inflater.inflate(R.layout.fragment_victim,
                container, false);
        return selfCard;
    }


    @Override
    public void onResume() {
        super.onResume();
        selfCard.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        selfCard.setOnClickListener(null);
        super.onPause();
    }

    @Override
    public void onClick(final View view) {
        final Intent mapActivityIntent = new Intent(view.getContext(), MapActivity.class);
        startActivity(mapActivityIntent);
    }


}
