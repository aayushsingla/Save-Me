package com.codefundo.saveme.donations;

import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.Donation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class DonationsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvName;
    private TextView tvAmount;

    public DonationsViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvAmount = itemView.findViewById(R.id.tv_amount);

    }

    public void populate(Donation data) {
        tvName.setText(data.getName());
        String amount = itemView.getContext().getString(R.string.Rs) + "  " + String.valueOf(data.getAmount());
        tvAmount.setText(amount);
    }
}
