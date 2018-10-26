package com.codefundo.saveme.donations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.Donation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsViewHolder> {
    private ArrayList<Donation> donations;

    public DonationsAdapter(ArrayList<Donation> missingPeopleData) {
        this.donations = missingPeopleData;
    }

    @NonNull
    @Override
    public DonationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.view_donations, parent, false);
        return new DonationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationsViewHolder holder, int position) {
        holder.populate(donations.get(position));
    }

    @Override
    public int getItemCount() {
        return donations.size();
    }
}
