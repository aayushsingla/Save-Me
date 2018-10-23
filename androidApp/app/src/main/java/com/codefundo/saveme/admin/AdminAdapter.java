package com.codefundo.saveme.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.AdminModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminAdapter extends RecyclerView.Adapter<AdminViewHolder> {
    private ArrayList<AdminModel> adminModels = new ArrayList<>(0);

    public AdminAdapter() {
        adminModels.add(new AdminModel(R.drawable.image_help_camp, "Mark Camps"));
        adminModels.add(new AdminModel(R.drawable.image_send_notifications, "Send Notifications"));
    }


    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.view_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        holder.populate(adminModels.get(position));
    }

    @Override
    public int getItemCount() {
        return adminModels.size();
    }
}
