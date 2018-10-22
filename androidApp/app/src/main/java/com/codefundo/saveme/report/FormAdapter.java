package com.codefundo.saveme.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.MissingPeopleData;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FormAdapter extends RecyclerView.Adapter<FormViewHolder> {
    private ArrayList<MissingPeopleData> missingPeopleData;

    public FormAdapter(ArrayList<MissingPeopleData> missingPeopleData){
        this.missingPeopleData=missingPeopleData;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.view_missing_people, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        holder.populate(missingPeopleData.get(position));
    }

    @Override
    public int getItemCount() {
        return missingPeopleData.size();
    }
}
