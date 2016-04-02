package com.ducnguyen.duo.search;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.data.DataContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ducprogram on 3/31/16.
 */
public class SearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    private static final String SEARCHFRAGMENT_TAG = "SEARCH_TAG";
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_activity, new SearchFragment(),
                         SEARCHFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.general_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        handleIntent(getIntent());

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        getContentResolver().delete(
                    DataContract.searchEntry.buildURI(),
                    null, null
        );
        super.onBackPressed();
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // get location and build query
            // TODO: would it be better to put this into AsyncTask
            LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            Map<String, String> qr = new HashMap<>();
            qr.put(Utility.URI_SEARCH_QUERY, query);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                qr.put(Utility.URI_LATITUDE, String.valueOf(loc.getLatitude()));
                qr.put(Utility.URI_LONGITUDE, String.valueOf(loc.getLongitude()));
            } else {
                Log.w(LOG_TAG, ".handleItent - uri: location not enabled");
                Toast locDis = Toast.makeText(this,
                        "Enable location will allows better search result",
                        Toast.LENGTH_SHORT);
                locDis.show();
            }

            // update the database
            Uri uri = Utility.buildUri(Utility.URI_SEARCH, qr);
            new Utility.UpdateDatabase(this, uri, Utility.URI_SEARCH).execute();

            // modify search visual
            searchView.setQuery(query, false);
            searchView.clearFocus();

            if (Utility.VERBOSITY >= 2) {
                Log.v(LOG_TAG, ".handleIntent - uri: " + uri.toString());
            }
        }
    }

}
