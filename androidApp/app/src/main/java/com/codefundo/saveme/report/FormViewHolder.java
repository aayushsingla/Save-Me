package com.codefundo.saveme.report;

import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.MissingPeopleData;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class FormViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView imageVictim;
    private TextView tvName;
    private TextView tvCity;
    private TextView tvState;
    private TextView tvAge;
    private TextView tvGender;

    public FormViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName=itemView.findViewById(R.id.tv_name);
        tvCity=itemView.findViewById(R.id.tv_city);
        tvAge=itemView.findViewById(R.id.tv_age);
        tvState=itemView.findViewById(R.id.tv_state);
        tvGender=itemView.findViewById(R.id.tv_gender);
        imageVictim=itemView.findViewById(R.id.image_victim);
    }

    public void populate(MissingPeopleData data) {
        tvName.setText(data.getName());
        tvState.setText("State: "+data.getState());
        tvAge.setText("Age: "+data.getAge());
        tvCity.setText("City: "+data.getCity());
        if(data.getGender().toLowerCase().matches("male")){
            tvGender.setText("M");
        }else{
            tvGender.setText("F");
        }
        if(data.getPhotoUrl().matches(""))
            imageVictim.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.image_default_profile));
        else
            imageVictim.setImageURI(data.getPhotoUrl());
    }
}
