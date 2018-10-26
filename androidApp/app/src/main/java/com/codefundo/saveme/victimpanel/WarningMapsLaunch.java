package com.codefundo.saveme.victimpanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.codefundo.saveme.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.fragment.app.DialogFragment;

import static com.codefundo.saveme.maps.MapActivity.launchMapActivity;

public class WarningMapsLaunch extends DialogFragment {
    public static final String SENDING_VICTIM_LOCATION = "Sending Location";
    private TextView buttonOk;
    private TextView buttonGoBack;

    public static WarningMapsLaunch newInstance() {
        return new WarningMapsLaunch();
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.layout_warning_maps, null);
        initUi(view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean sending = sharedPreferences.getBoolean(SENDING_VICTIM_LOCATION, false);
        if (sending) {
            launchMapActivity(getContext(), "victim");
            dismiss();
        }

        buttonOk.setOnClickListener(v -> launchMapActivity(getContext(), "victim"));
        buttonGoBack.setOnClickListener(v -> getDialog().dismiss());

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .create();


    }

    private void initUi(View view) {
        buttonOk = view.findViewById(R.id.btn_ok);
        buttonGoBack = view.findViewById(R.id.btn_go_back);
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


}
