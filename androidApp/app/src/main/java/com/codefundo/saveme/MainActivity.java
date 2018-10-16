package com.codefundo.saveme;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codefundo.saveme.models.UserData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.*;

import com.codefundo.saveme.rescueteam.RescueFragment;
import com.codefundo.saveme.victimpanel.VictimFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private final static String TAG_RESCUE_FRAGMENT = "RescueFragment";
    private final static String TAG_VICTIM_FRAGMENT = "VictimFragment";
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            mClient = new MobileServiceClient(
                    "https://rescue-mission.azurewebsites.net",
                    this
            );
        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this,"We are experiencing some technical issues, please check back later",Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadFragments();

        testingAddData();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void loadFragments() {
        final FragmentManager featuresFragManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = featuresFragManager.beginTransaction();
        if (featuresFragManager.getFragments().size() == 0) {
            fragmentTransaction.add(R.id.container_fragments_home, new VictimFragment(), TAG_VICTIM_FRAGMENT);
            fragmentTransaction.add(R.id.container_fragments_home, new RescueFragment(), TAG_RESCUE_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void testingAddData() {
        MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
        for(int i=0;i<100;i++) {
            UserData item = new UserData();

            item.name = Long.toHexString(( new Random()).nextLong());

            table.insert(item, new TableOperationCallback<UserData>() {
                @Override
                public void onCompleted(UserData entity, Exception exception, ServiceFilterResponse response) {
                    if(exception == null)
                        Log.d("TAG","Success");
                    else
                        Log.d("TAG", exception.getMessage());
                }
            });
        }
    }

    public void checkDataRefresh() {

    }
}
