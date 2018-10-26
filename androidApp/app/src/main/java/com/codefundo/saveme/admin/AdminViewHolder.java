package com.codefundo.saveme.admin;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.admin.notification.NotificationActivity;
import com.codefundo.saveme.models.AdminModel;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView simpleDraweeView;
    private TextView textView;

    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text);
        simpleDraweeView = itemView.findViewById(R.id.drawee_view);
    }

    public void populate(AdminModel adminModel) {
        textView.setText("   " + adminModel.getText());
        simpleDraweeView.setActualImageResource(adminModel.getDrawable());
        itemView.setOnClickListener(v -> {
            switch (adminModel.getText()) {
                case "Send Notifications":
                    v.getContext().startActivity(new Intent(v.getContext(), NotificationActivity.class));
                    break;
                case "Mark Camps":
                    v.getContext().startActivity(new Intent(v.getContext(), CampMarkingActivity.class));
                    break;

            }
        });
    }

}
