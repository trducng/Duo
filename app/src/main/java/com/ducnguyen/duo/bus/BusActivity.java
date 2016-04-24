package com.ducnguyen.duo.bus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ducnguyen.duo.bus.delivery.BusDelFragment;
import com.ducnguyen.duo.bus.loyalty.BusLoyaltyFragment;
import com.ducnguyen.duo.data.DataContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ducprogram on 3/18/16.
 * This class implements the activity (the screen) to view business page
 */
public class BusActivity extends AppCompatActivity {

    public static final String LOG_TAG = BusActivity.class.getSimpleName();

    ViewPager mViewPager;
    BusActivityPagerAdapter mPagerAdapter;
    Uri receivedUri = null;
    private String busID;
    DownloadThread dl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1.a Retrieve the uri sent by PersonalPageFragment/RecommendationPageFragment
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_ORIGINATING_URI)) {
            receivedUri = Uri.parse(intent.getStringExtra(Intent.EXTRA_ORIGINATING_URI));
        }
        String service = receivedUri.getQueryParameter(DataContract.bookmarkEntry.COL_SERVS);
        busID = receivedUri.getQueryParameter(DataContract.bookmarkEntry.COL_BUSID);
        String[] services = null;
        Set<String> set_services = null;
        if (service != null) {
            services = service.split("-");
            set_services = new HashSet<>(Arrays.asList(services));
        }

        if (Utility.VERBOSITY >= 2) {
            Log.v(LOG_TAG, "set_services include: " + set_services.toString());
        }

        // 1.b Check if the business ID is stored in SharedPreferences
        SharedPreferences cache = getPreferences(MODE_PRIVATE);
        Set<String> savedID = cache.getStringSet(
                Utility.SAVED_BUSID, null);
        // if savedID contains real value
        if (savedID != null) {
            if (!savedID.contains(busID)) {
                Set<String> toDownload = checkDownloadCache(cache,
                        busID, set_services);
                dl = new DownloadThread("Download data for " + busID,
                        toDownload, busID, this);
                dl.start();
                Log.v(LOG_TAG, "savedID != null");
            } else {
                Log.v(LOG_TAG, "saveID.contains(busID)");
            }
        } else {
            Set<String> toDownload = checkDownloadCache(cache,
                    busID, set_services);
            if (Utility.VERBOSITY >= 2) {
                Log.v(LOG_TAG, "toDownload is deliberately changed");
                toDownload = new HashSet<>(
                        Arrays.asList(Utility.TEMP_BUSID_DELIVERY,
                                Utility.TEMP_BUSID_INFO,
                                Utility.TEMP_BUSID_LOYALTY,
                                Utility.TEMP_BUSID_PRODUCTS,
                                Utility.TEMP_BUSID_SCHEDULE));
            }
            dl = new DownloadThread("Download data for " + busID,
                    toDownload, busID, this);
            dl.start();
            Log.v(LOG_TAG, "savedID == null");
        }

        // 2. Set the view for activity
        setContentView(R.layout.activity_bus);

        // 3. Create the adapter for ViewPager
        mPagerAdapter = new BusActivityPagerAdapter(
                                getSupportFragmentManager(),
                                busID, services, dl);

        // 4. Find mViewPager and attach mViewPager with mPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.bus_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (dl != null) {
                    // set priority to download this current tab,
                    // set secondary to the next tab
                    dl.setPriority(Utility.NAME_TO_DOWNLOAD.get(
                            mPagerAdapter.getAllTabs().get(position)));
                    dl.setSecondary(Utility.NAME_TO_DOWNLOAD.get(
                            mPagerAdapter.getAllTabs().get((position + 1) %
                            mPagerAdapter.getNumItems())));
                    Log.v(LOG_TAG + ".DownloadThread",
                          "The thread is alive?: " + dl.isAlive());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.v("TEST VIEWPAGER", "onPageScrolled is called");
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (dl != null) {
            dl.interrupt();
        }
        super.onBackPressed();
    }


    public String getBusId() {
        return busID;
    }


    /**
     * This function checks if the clicked busID has which components
     * already stored in the device to see which components need to be
     * downloaded. It will return the components to download.
     * @param cache         the location where cache will be stored
     * @param busID         the busID in question
     * @param set_services  the components that a business with busID has
     * @return              the components to download
     */
    private static Set<String> checkDownloadCache(SharedPreferences cache,
                                          String busID,
                                          Set<String> set_services) {

        Set<String> toDownload = new HashSet<>();

        // check business info
        String tempBusInfo = cache.getString(
                Utility.TEMP_BUSID_INFO, null);
        if (tempBusInfo != null) {
            Utility.Name tempInfo = (Utility.Name) Utility
                    .deserializeFromString(tempBusInfo);
            if (tempInfo.has(busID) == -1) {
                toDownload.add(Utility.TEMP_BUSID_INFO);
            }
        } else {
            toDownload.add(Utility.TEMP_BUSID_INFO);
        }

        // check business loyalty
        String tempBusLoy = cache.getString(
                Utility.TEMP_BUSID_LOYALTY, null);
        if (tempBusLoy != null) {
            Utility.Name tempLoy = (Utility.Name) Utility
                    .deserializeFromString(tempBusLoy);
            if ((tempLoy.has(busID) == -1) &&
                    (set_services.contains(Utility.CODE_LOYALTY))) {
                toDownload.add(Utility.TEMP_BUSID_LOYALTY);
            }
        } else {
            if (set_services.contains(Utility.CODE_LOYALTY)) {
                toDownload.add(Utility.TEMP_BUSID_LOYALTY);
            }
        }

        // check business products
        String tempBusProd = cache.getString(
                Utility.TEMP_BUSID_PRODUCTS, null);
        if (tempBusProd != null) {
            Utility.Name tempProd = (Utility.Name) Utility
                    .deserializeFromString(tempBusProd);
            if ((tempProd.has(busID) == -1) &&
                    (set_services.contains(Utility.CODE_PRODUCTS))) {
                toDownload.add(Utility.TEMP_BUSID_PRODUCTS);
            }
        } else {
            if (set_services.contains(Utility.CODE_PRODUCTS)) {
                toDownload.add(Utility.TEMP_BUSID_PRODUCTS);
            }
        }

        // check business delivery
        String tempBusDel = cache.getString(
                Utility.TEMP_BUSID_DELIVERY, null);
        if (tempBusDel != null) {
            Utility.Name tempDel = (Utility.Name) Utility
                    .deserializeFromString(tempBusDel);
            if ((tempDel.has(busID) == -1) &&
                    (set_services.contains(Utility.CODE_DELIVERY))) {
                toDownload.add(Utility.TEMP_BUSID_DELIVERY);
            }
        } else {
            if (set_services.contains(Utility.CODE_DELIVERY)) {
                toDownload.add(Utility.TEMP_BUSID_DELIVERY);
            }
        }

        // check business schedule
        String tempBusSche = cache.getString(
                Utility.TEMP_BUSID_SCHEDULE, null);
        if (tempBusSche != null) {
            Utility.Name tempSche = (Utility.Name) Utility
                    .deserializeFromString(tempBusSche);
            if ((tempSche.has(busID) == -1) &&
                    (set_services.contains(Utility.CODE_SCHEDULE))) {
                toDownload.add(Utility.TEMP_BUSID_SCHEDULE);
            }
        } else {
            if (set_services.contains(Utility.CODE_SCHEDULE)) {
                toDownload.add(Utility.TEMP_BUSID_SCHEDULE);
            }
        }



        if (Utility.VERBOSITY >= 2) {
            Log.v(LOG_TAG + ".checkDownloadCache",
                    toDownload.toString());
        }
        if (Utility.IMPORTANCE) {
            Log.v(LOG_TAG + ".checkDownloadCache",
                    toDownload.toString());
        }

        return toDownload;
    }

    /**
     * This thread is responsible for downloading the data and plug
     * those data into SQLiteDatabase. Brief description on how
     * this thread works:
     *      This thread will always try to download all items in
     * toDownload. It will prefer to download the information that
     * has 'priority' first, then it will download 'secondary'.
     */
    public static class DownloadThread extends Thread {

        // priority can be any of these values from Utility: TEMP_BUSID_INFO,
        // TEMP_BUSID_PRODUCTS, TEMP_BUSID_DELIVERY, TEMP_BUSID_SCHEDULE
        private String priority;
        private String secondary;
        public Set<String> toDownload;
        public String busID;
        public Context mContext;

        public DownloadThread(String name, Set<String> download,
                              String busId, Context context) {
            super(name);
            this.toDownload = download;
            // priority will always be defaulted to BasicInfo
            this.priority = Utility.TEMP_BUSID_INFO;
            this.secondary = null;
            this.busID = busId;
            this.mContext = context;
        }

        @Override
        public void run() {
            super.run();
            // until there is nothing to download
            while (!toDownload.isEmpty()) {
                String tempPriority = priority;
                if (toDownload.contains(tempPriority)) {

                    // create an appropriate URL
                    Map<String, String> query = new HashMap<>();
                    query.put(Utility.URI_BUSID, busID);
                    query.put(Utility.URI_BUS_KEY, tempPriority);
                    Uri url = Utility.buildUri(Utility.URI_BUS, query);

                    Log.v(LOG_TAG+".DownloadThread", "Uri: " + url.toString());

                    // download the data and put it into the database
                    Utility.updateDatabase(mContext, tempPriority, url);

                    toDownload.remove(tempPriority);

                    if (Utility.VERBOSITY >= 2) {
                        Log.v(LOG_TAG + ".DownloadThread",
                                "Begin to sleep 3 seconds");
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    if (tempPriority.equals(priority)) {
                        // if priority hasn't changed (user doesn't view any
                        // other tab immediately), move the secondary tab to
                        // priority, and pick a random secondary
                        priority = secondary;
                        for (String each_object: toDownload) {
                            secondary = each_object;
                            break;
                        }

                        // if priority has changed (user views a specific tab,
                        // we want to download that information as soon as possible
                        // And if this is the case, both priority and secondary are
                        // already modified so we don't need to change them
                    }
                } else {
                    // just pick a random item from toDownload to download
                    for (String each_object: toDownload) {

                        // create an appropriate URL
                        Map<String, String> query = new HashMap<>();
                        query.put(Utility.URI_BUSID, busID);
                        query.put(Utility.URI_BUS_KEY, each_object);
                        Uri url = Utility.buildUri(Utility.URI_BUS, query);

                        // download the data and put it into the database
                        Utility.updateDatabase(mContext, each_object, url);

                        toDownload.remove(each_object);

                        if (Utility.VERBOSITY >= 2) {
                            Log.v(LOG_TAG + ".DownloadThread",
                                    "Begin to sleep 3 seconds");
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                        break;
                    }
                }
            }
            interrupt();
        }

        public void setPriority(String t) {
            this.priority = t;
            if (Utility.VERBOSITY >= 2) {
                Log.v(LOG_TAG + ".DownloadThread",
                        "Priority change to " + t);
            }
        }

        public void setSecondary(String t) {
            this.secondary = t;
            if (Utility.VERBOSITY >= 2) {
                Log.v(LOG_TAG + ".DownloadThread",
                        "Secondary change to " + t);
            }
        }
    }


    /**
     * This class is responsible for creating scrollable tabs in the
     * activity. During initiation, this class requires FragmentManager
     * (to handle creation and distribution of fragments), busID (to
     * know which business is viewed) and ser (to know which components
     * of the business will be viewed)
     */
    public static class BusActivityPagerAdapter extends FragmentStatePagerAdapter {

        protected int NUM_ITEMS;

        protected ArrayList<String> ALL_TABS;
        protected DownloadThread dl = null;

        String busId;

        public BusActivityPagerAdapter(FragmentManager fm, String busId,
                                       String[] ser, DownloadThread download) {
            super(fm);
            this.busId = busId;
            String[] services = ser;
            this.dl = download;
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
            Log.v(LOG_TAG, "ALL_TABS: " + ALL_TABS.toString());
        }

        @Override
        public Fragment getItem(int position) {
            String tabTitle = ALL_TABS.get(position);
            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "Fragment (" + String.valueOf(position) + ", " + tabTitle + ")");
            }

            switch (tabTitle) {

                case Utility.TAB_BASIC_INFO: {

                    BusInfoFragment infoFrag = new BusInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utility.URI_BUSID, busId);
                    infoFrag.setArguments(bundle);

                    return infoFrag;
                }

                case Utility.TAB_PRODUCTS: {

                    BusInfoFragment infoFrag = new BusInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utility.URI_BUSID, busId);
                    infoFrag.setArguments(bundle);

                    return infoFrag;
                }

                case Utility.TAB_DELIVERY: {

                    BusDelFragment delFrag = new BusDelFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utility.URI_BUSID, busId);
                    delFrag.setArguments(bundle);


                    return delFrag;
                }

                case Utility.TAB_SCHEDULE: {

                    BusInfoFragment infoFrag = new BusInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utility.URI_BUSID, busId);
                    infoFrag.setArguments(bundle);

                    return infoFrag;
                }

                case Utility.TAB_LOYALTY: {

                    BusLoyaltyFragment loyalFrag = new BusLoyaltyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utility.URI_BUSID, busId);
                    loyalFrag.setArguments(bundle);

                    return loyalFrag;
                }

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

        public ArrayList<String> getAllTabs() {
            return ALL_TABS;
        }

        public int getNumItems() {
            return NUM_ITEMS;
        }
    }
}
