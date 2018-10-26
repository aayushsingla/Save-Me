package com.codefundo.saveme.rescueteam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Switch;

import com.codefundo.saveme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.codefundo.saveme.maps.MapActivity.launchMapActivity;

public class RescueActivity extends AppCompatActivity {
    public static final String VOLUNTEER_LOCATION = "isVolunteerOnline";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new RescueTeamAdapter());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> launchMapActivity(this, "user"));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RescueActivity.this);
        boolean isChecked = sharedPreferences.getBoolean(VOLUNTEER_LOCATION, true);
        Switch swich = findViewById(R.id.switch_volunteer_online);
        swich.setChecked(isChecked);
        swich.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(VOLUNTEER_LOCATION, isChecked1);
            Log.e(VOLUNTEER_LOCATION, isChecked1 + "");
            editor.apply();
        });
    }
}
