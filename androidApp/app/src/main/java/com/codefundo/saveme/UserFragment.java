package com.codefundo.saveme;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codefundo.saveme.auth.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new UserFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView nameTv = view.findViewById(R.id.tv_name);
        TextView idTv = view.findViewById(R.id.tv_id);
        ImageView imageView = view.findViewById(R.id.imageView);
        String id = LoginActivity.getDeviceIMEI(Objects.requireNonNull(getContext()));
        String azureId = LoginActivity.getCurrentUserUniqueId(Objects.requireNonNull(getContext()));

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        nameTv.setText(account.getDisplayName());
        idTv.setText(id);
        try {
            imageView.setImageBitmap(getQRCodeImage(id, azureId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }


    private Bitmap getQRCodeImage(String id, String azureId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("azureId", azureId);
        String text = jsonObject.toString();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
