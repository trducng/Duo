package com.ducnguyen.duo.bus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.data.DataContract;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ducprogram on 3/18/16.
 * This class implements the activity (the screen) to view business page
 */
public class BusActivity extends AppCompatActivity {

    public static final String LOG_TAG = BusActivity.class.getSimpleName();

    ViewPager mViewPager;
    BusActivityPagerAdapter mPagerAdapter;
    String receivedUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Retrieve the uri sent by PersonalPageFragment/RecommendationPageFragment
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_ORIGINATING_URI)) {
            receivedUri = intent.getStringExtra(Intent.EXTRA_ORIGINATING_URI);
        }

        // 2. Set the view for activity
        setContentView(R.layout.activity_bus);

        // 3. Create the adapter for page view
        mPagerAdapter = new BusActivityPagerAdapter(
                                getSupportFragmentManager(), Uri.parse(receivedUri));

        // 4. Find mViewPager and attach mViewPager with mPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.bus_pager);
        mViewPager.setAdapter(mPagerAdapter);

    }

    public static class BusActivityPagerAdapter extends FragmentStatePagerAdapter {

        protected int NUM_ITEMS;

        protected ArrayList<String> ALL_TABS;

        public BusActivityPagerAdapter(FragmentManager fm, Uri uri) {
            super(fm);
            String service = uri.getQueryParameter(DataContract.bookmarkEntry.COL_BUSSERVICES);
            String[] services = null;
            if (service != null) service.split("-");
            ALL_TABS = new ArrayList<>(
                    Arrays.asList(Utility.TAB_BASIC_INFO)
            );

            // Get the types of tabs and the number of types of tabs that a business will have;
            for (String eService : services) {
                if (!eService.equals(Utility.CODE_MESSAGE)) {
                    ALL_TABS.add(Utility.CODE_TO_NAME.get(eService));
                }
            }
            NUM_ITEMS = ALL_TABS.size();
        }

        @Override
        public Fragment getItem(int position) {
            String tabTitle = ALL_TABS.get(position);
            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "Fragment (" + String.valueOf(position) + ", " + tabTitle + ")");
            }

            switch (tabTitle) {
                case Utility.TAB_BASIC_INFO:
                    return new BusInfoFragment();
                case Utility.TAB_PRODUCTS:
                    return new BusInfoFragment();
                case Utility.TAB_DELIVERY:
                    return new BusInfoFragment();
                case Utility.TAB_SCHEDULE:
                    return new BusInfoFragment();
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
            return ALL_TABS.get(position);
        }
    }
}
