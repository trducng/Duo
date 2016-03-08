package com.ducnguyen.duo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    // collect user's location, current time and user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create view
        setContentView(R.layout.activity_welcome);

        // to receive location update from device requires LocationManager
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    // wait 3000 milliseconds
                    sleep(3000);
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(WelcomeActivity.this, SearchActivity.class);

                    // start searching activity
                    startActivity(i);
                    finish();
                    return;
                }
            }
        };

        // SharedPreferences to check if this is the first time the user runs the app
        SharedPreferences prefs = getSharedPreferences("com.ducnguyen.duo", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // TODO: redirect to setting up activity
            prefs.edit().putBoolean("firstrun", false).commit();
        } else {
            welcomeThread.start();
            // check if user grants location permission, if it is granted, perform
            // sending location and time uri to the server
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Utility.ll);
            } else {
                // TODO: if the user does not grant location permission, just send
                // a query uri to the server, goes to the search page and indicate
                // that the app would return more correct results if user grant
                // location permission
            }
        }


    }
}
