package com.codefundo.saveme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codefundo.saveme.models.UserData;
import com.codefundo.saveme.victimpanel.MapActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView mBottomNavigationView;
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


        testingAddData();
        checkDataRefresh();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MapActivity.class)));

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBottomNavigationView.setSelectedItemId(R.id.nav_home);
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
        MobileServiceTable<UserData> table = mClient.getTable(UserData.class);
        table.where().execute(new TableQueryCallback<UserData>() {
            @Override
            public void onCompleted(List<UserData> result, int count, Exception exception, ServiceFilterResponse response) {
                for (UserData data : result)
                    Log.d("USER", data.id);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_fragments, HomeFragment.newInstance(), "HomeFragment").commit();
                break;
            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_fragments, HomeFragment.newInstance()).commit();
                break;
            case R.id.nav_user:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_fragments, HomeFragment.newInstance()).commit();
                break;
        }
        return true;
    }
}
