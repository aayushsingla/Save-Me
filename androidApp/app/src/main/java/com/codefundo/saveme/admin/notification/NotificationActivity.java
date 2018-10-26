package com.codefundo.saveme.admin.notification;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codefundo.saveme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NotificationActivity extends AppCompatActivity {
    private TextInputEditText textViewMessage;
    private TextInputEditText textViewTitle;
    private String message;
    private String title;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final FloatingActionButton fab = findViewById(R.id.fab);
        textViewMessage = findViewById(R.id.textViewMessage);
        textViewTitle = findViewById(R.id.textViewTitle);
        progressBar = findViewById(R.id.progress_bar);

        fab.setOnClickListener(view -> {
            hideShowProgressBar();
            message = textViewMessage.getText().toString();
            title = textViewTitle.getText().toString();
            RemoteMessage.Builder creator = new RemoteMessage.Builder("Message");
            creator.addData("Title", title);
            creator.addData("Message", message);


            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "key=AAAAZR6bV-0:APA91bEa4OnZN38ZsqHvAryStGS6paFx2o4Va3HSkmunTRQxUfgrYfLFEwsUQjt2UXJWowd5gNCD55akyM_ZGJ_6ALJXTfODNL8NjHXsD0rLJ3rNbl1fcNwZTKuHnYmVAwlMbB6iRknB");
                connection.setDoOutput(true);
                connection.connect();
                Log.e("noti", "CONNECTED");


                DataOutputStream os = new DataOutputStream(connection.getOutputStream());


                Map<String, Object> data = new HashMap<>();
                data.put("to", "/topics/" + "general");
                data.put("data", creator.build().getData());

                JSONObject object = new JSONObject(data);
                Log.e("noti", "o:" + object.toString());

                String s2 = object.toString().replace("\\", "");
                os.writeBytes(s2);
                os.close();

                Log.e("noti", "s:" + s2);

                //recieving response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                rd.close();

                Log.e("noti", "r:" + response.toString());

                textViewMessage.setText("");
                textViewTitle.setText("");
                Toast.makeText(this, "Message Sent", Snackbar.LENGTH_LONG).show();
                hideShowProgressBar();
            } catch (Exception e) {
                hideShowProgressBar();
                Toast.makeText(this, "Unable to send Message", Snackbar.LENGTH_LONG).show();
                Log.e("noti", e.getMessage() + Arrays.toString(e.getStackTrace()));
                e.printStackTrace();

            }

        });


    }

    private void hideShowProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }


}

