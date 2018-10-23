package com.codefundo.saveme.rescueteam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.RescueTeamModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RescueTeamAdapter extends RecyclerView.Adapter<RescueViewHolder> {
    private ArrayList<RescueTeamModel> rescueTeamModels = new ArrayList<>(0);

    public RescueTeamAdapter() {
        rescueTeamModels.add(new RescueTeamModel(R.drawable.image_volunteer_map, "Volunteer Map"));
        rescueTeamModels.add(new RescueTeamModel(R.drawable.image_volunteer_card, "Mark Victim Safe"));
    }


    @NonNull
    @Override
    public RescueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.view_volunteer, parent, false);
        return new RescueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RescueViewHolder holder, int position) {
        holder.populate(rescueTeamModels.get(position));
    }

    @Override
    public int getItemCount() {
        return rescueTeamModels.size();
    }
}
