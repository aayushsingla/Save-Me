package com.codefundo.saveme.rescueteam;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;
import com.codefundo.saveme.models.RescueTeamModel;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.codefundo.saveme.victimpanel.MapActivity.launchMapActivity;

public class RescueViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView simpleDraweeView;
    private TextView textView;

    public RescueViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text);
        simpleDraweeView = itemView.findViewById(R.id.drawee_view);
    }

    public void populate(RescueTeamModel rescueTeamModel) {
        textView.setText("   " + rescueTeamModel.getText());
        simpleDraweeView.setActualImageResource(rescueTeamModel.getDrawable());
        itemView.setOnClickListener(v -> {
            switch (rescueTeamModel.getText()) {
                case "Volunteer Map":
                    launchMapActivity(v.getContext(), "volunteer");
                    break;
                case "Mark Victim Safe":
                    v.getContext().startActivity(new Intent(v.getContext(), QRCodeScannerActivity.class));
                    break;

            }
        });
    }

}
