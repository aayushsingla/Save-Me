package com.codefundo.saveme.report;

import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.MissingPeopleData;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class FormViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView imageVictim;
    private TextView tvName;
    private TextView tvCity;
    private TextView tvState;
    private TextView tvAge;

    public FormViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName=itemView.findViewById(R.id.tv_name);
        tvCity=itemView.findViewById(R.id.tv_city);
        tvAge=itemView.findViewById(R.id.tv_age);
        tvState=itemView.findViewById(R.id.tv_state);
        imageVictim=itemView.findViewById(R.id.image_victim);
    }

    public void populate(MissingPeopleData data) {
        tvName.setText(data.getName());
        tvState.setText(data.getState());
        tvAge.setText(data.getAge());
        tvCity.setText(data.getState());
        imageVictim.setImageURI(data.getPhotoUrl());
    }
}
