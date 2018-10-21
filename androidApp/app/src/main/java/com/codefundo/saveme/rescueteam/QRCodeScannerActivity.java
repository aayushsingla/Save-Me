package com.codefundo.saveme.rescueteam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.models.VictimData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class QRCodeScannerActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //intializing scan object
        CardView cardView = findViewById(R.id.btn_scan);
        TextInputEditText textInputEditText = findViewById(R.id.et_imei);

        cardView.setOnClickListener(v -> {
            qrScan = new IntentIntegrator(QRCodeScannerActivity.this);
            qrScan.setBeepEnabled(true);
            qrScan.initiateScan();
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            imei = textInputEditText.getText().toString();
            handleResults(imei);
        });


    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    imei = obj.get("id").toString();
                    handleResults(imei);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleResults(String imei) {

        MobileServiceClient mClient = SaveMe.getAzureClient(this);
        MobileServiceTable<VictimData> table = mClient.getTable(VictimData.class);
        ListenableFuture<MobileServiceList<VictimData>> listenableFuture =
                table.where().field("id").eq(imei).execute();
        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VictimData>>() {
            @Override
            public void onSuccess(MobileServiceList<VictimData> result) {
                if (result.getTotalCount() > 0) {
                    VictimData victimData = result.get(0);
                    victimData.setStatus("safe");
                    ListenableFuture<VictimData> lf = table.update(victimData);
                    Futures.addCallback(lf, new FutureCallback<VictimData>() {
                        @Override
                        public void onSuccess(VictimData result) {
                            Log.e("Tag", "update succeeded");
                            Toast.makeText(QRCodeScannerActivity.this, "Marked Safe", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("Tag", t.getMessage());

                        }
                    });
                } else {
                    new AlertDialog.Builder(QRCodeScannerActivity.this)
                            .setPositiveButton("OK", (dialog, which) -> {
                            })
                            .setNegativeButton("Retry", (dialog, which) -> {
                                handleResults(imei);
                            })
                            .setCancelable(false)
                            .setTitle("Invalid Id")
                            .setMessage("User not in danger")
                            .show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Tag", t.getMessage());

            }
        });
    }


}

