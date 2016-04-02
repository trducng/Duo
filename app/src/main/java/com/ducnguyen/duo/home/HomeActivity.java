package com.ducnguyen.duo.home;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;

public class HomeActivity extends AppCompatActivity {

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    // The view pager
    ViewPager mViewPager;
    // The adapter that populates mViewPager
    HomeActivityPagerAdapter mPagerAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 1. Set the view for activity
        setContentView(R.layout.activity_home);

        // 2. Create the adapter for page view
        mPagerAdapter = new HomeActivityPagerAdapter(
                        getSupportFragmentManager());

        // 3. Find mViewPager and attach mViewPager with mPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.home_pager);
        mViewPager.setAdapter(mPagerAdapter);


//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.general_search).getActionView();
        searchView.setIconifiedByDefault(false);

        if (Utility.VERBOSITY >= 1) {
            Log.v(LOG_TAG, "Test menu - before set searchable info");
        }
        SearchableInfo test = searchManager.getSearchableInfo(getComponentName());
        if ((test == null) && (Utility.VERBOSITY >=2 )){
            Log.v(LOG_TAG, "Test menu - This shit is null");
        }
        ComponentName name = test.getSearchActivity();
        Log.v(LOG_TAG, "Test menu - name of searchable activity: " + name.toString());
        searchView.setSearchableInfo(test);
        if (Utility.VERBOSITY >= 1) {
            Log.v(LOG_TAG, "Test menu - after set searchable info");
        }


        return true;
    }



    @Override
    protected void onDestroy() {

        searchView.setQuery("", false);
        searchView.clearFocus();

        super.onDestroy();
    }

    public static class HomeActivityPagerAdapter extends FragmentPagerAdapter {

        // There are 3 pages in the home screen
        static final int NUM_ITEMS = 3;
        static final int PERSONAL = 0;
        static final int RECOMMENDATION = 1;
        static final int EVENTS = 2;

        public HomeActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (Utility.VERBOSITY >= 2) {
                Log.v(LOG_TAG, "Position: " + String.valueOf(position));
            }

            switch (position) {
                case PERSONAL:
                    return new PersonalPageFragment();

                case RECOMMENDATION:
                    return new RecommendationPageFragment();

                case EVENTS:
                    return new PersonalPageFragment();

                default:
                    throw new UnsupportedOperationException(
                            "There is no position: " + String.valueOf(position));
            }

        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case PERSONAL:
                    return "Duc";

                case RECOMMENDATION:
                    return "Recommendation";

                case EVENTS:
                    return "Events";

                default:
                    throw new UnsupportedOperationException(
                            "There is no position: " + String.valueOf(position));
            }
        }
    }

}
