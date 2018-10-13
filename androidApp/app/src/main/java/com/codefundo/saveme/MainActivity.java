package com.codefundo.saveme;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codefundo.saveme.rescueteam.RescueFragment;
import com.codefundo.saveme.victimpanel.VictimFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private final static String TAG_RESCUE_FRAGMENT = "RescueFragment";
    private final static String TAG_VICTIM_FRAGMENT = "VictimFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadFragments();

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
}
