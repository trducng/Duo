package com.ducnguyen.duo;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ducnguyen.duo.bus.loyalty.BusLoyaltyAdapter;
import com.github.florent37.materialleanback.MaterialLeanBack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This CustomViews class holds a customized views that will be
 * used somewhere in the application. The current views include:
 *      - BusInfoActivity:
 *          + BusInfoFragment
 *          + BusLoyaltyAdapter
 */
public class CustomViews {

    public static class BusMainInfo extends LinearLayout {

        MaterialLeanBack imgView;

        private final String LOG_TAG = BusMainInfo.class.getSimpleName();

        LayoutInflater inflater;
        Cursor data;
        Context mContext;
        int ANDROID_VERSION;

        static final int COL_LOC = 4;
        static final int COL_CONTACT = 5;
        static final int COL_IMG = 6;
        static final int COL_HOURS = 7;
        static final int COL_NEWS = 8;
        static final int COL_LOY = 9;

        private static final int[] COLS_ID = {
                COL_LOC,
                COL_CONTACT,
                COL_IMG,
                COL_HOURS,
                COL_NEWS,
                COL_LOY
        };

        public BusMainInfo(Context context) {
            this(context, null);
        }

        public BusMainInfo(Context context, Cursor cursor) {
            super(context);
            init(context, cursor);
        }

        public BusMainInfo(Context context, AttributeSet attrs, Cursor cursor) {
            this(context, attrs, 0, cursor);
        }

        public BusMainInfo(Context context, AttributeSet attrs, int defStyle, Cursor cursor) {
            super(context, attrs, defStyle);
            init(context, cursor);
        }


        /**
         * This method swap the old cursor with the new cursor and close the old cursor
         * @param cursor    the new cursor
         */
        public void changeCursor(Cursor cursor) {
            if (cursor != data) {
                Cursor old = data;
                data = cursor;
                this.removeAllViews();
                if (data != null) createViews();
                if (old != null) old.close();
            }
        }

        /**
         * These methods create and attache views based on the current this.data
         */
        private void createViews() {
            if (!data.moveToFirst()) return;
            for (int id: COLS_ID) {
                if (data.isNull(id)) continue;
                switch (id) {
                    case COL_LOC: {
                        attachLoc(data.getString(id));
                        break;
                    }
                    case COL_CONTACT: {
                        attachCont(data.getString(id));
                        break;
                    }
                    case COL_IMG: {
                        attachIMG(data.getString(id));
                        break;
                    }
                    case COL_HOURS: {
                        attachHour(data.getString(id));
                        break;
                    }
                    case COL_NEWS: {
                        attachNews(data.getString(id));
                        break;
                    }
                    case COL_LOY: {
//                        attachLoy(data.getString(id));
                        break;
                    }
                }
            }
        }


        private void attachLoc(String string) {

            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "attachLoc: " + string);
            }

            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_bus_info_cell_layout,
                    null, false);
            TextView header = (TextView) view.findViewById(R.id.textview_bus_info_cell_header);
            header.setText("Location:");

            TextView locView = (TextView) inflater.inflate(R.layout.item_bus_info_textview_plain,
                                                            null, false);
            locView.setText(Html.fromHtml(string));
            view.addView(locView);
            this.addView(view);
        }

        private void attachCont(String string) {

            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "attachCont: " + string);
            }


            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_bus_info_cell_layout,
                    null, false);
            TextView header = (TextView) view.findViewById(R.id.textview_bus_info_cell_header);
            header.setText("Contact:");

            TextView conView = (TextView) inflater.inflate(R.layout.item_bus_info_textview_plain,
                                                           null, false);
            conView.setText(Html.fromHtml(string));
            view.addView(conView);
            this.addView(view);
        }

        private void attachIMG(String string) {

            imgView = (MaterialLeanBack) inflater.inflate(R.layout.item_bus_info_image,
                                                          null, false);

            imgView.setCustomizer(new MaterialLeanBack.Customizer() {
                @Override
                public void customizeTitle(TextView textView) {
                    textView.setTypeface(null, Typeface.BOLD);
                }
            });

            imgView.setAdapter(new MaterialLeanBack.Adapter<BusInfoImageViewHolder>() {
                @Override
                public int getLineCount() {
                    return 3;
                }

                @Override
                public int getCellsCount(int line) {
                    return 10;
                }

                @Override
                public BusInfoImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int line) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cellt_test, viewGroup, false);
                    return new BusInfoImageViewHolder(view);
                }

                @Override
                public void onBindViewHolder(BusInfoImageViewHolder viewHolder, int i) {

                    String url = "http://www.lorempixel.com/40" + viewHolder.row + "/40" + viewHolder.cell + "/";
                    Picasso.with(viewHolder.imageView.getContext())
                            .load(url)
                            .resize(1000, 1000)
                            .centerCrop()
                            .into(viewHolder.imageView);
                }

                @Override
                public String getTitleForRow(int row) {
                    return "";
                }

                //region customView
                @Override
                public RecyclerView.ViewHolder getCustomViewForRow(ViewGroup viewgroup, int row) {
                    if (row == 3) {
                        View view = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.header, viewgroup, false);
                        return new RecyclerView.ViewHolder(view) {
                        };
                    } else
                        return null;
                }

                @Override
                public boolean isCustomView(int row) {
                    return row == 3;
                }

                @Override
                public void onBindCustomView(RecyclerView.ViewHolder viewHolder, int row) {
                    super.onBindCustomView(viewHolder, row);
                }

                //endregion

            });

            this.addView(imgView);
        }

        private void attachHour(String string) {

            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "attachHour: " + string);
            }
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_bus_info_cell_layout,
                                                            null, false);
            TextView header = (TextView) view.findViewById(R.id.textview_bus_info_cell_header);
            header.setText("Operation hours:");
            TextView hourView = (TextView) inflater.inflate(R.layout.item_bus_info_textview_plain,
                                                            null, false);
            hourView.setText(Html.fromHtml(string));
            view.addView(hourView);
            this.addView(view);
        }

        private void attachNews(String string) {

            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "attachNews: " + string);
            }

            LinearLayout view = (LinearLayout) inflater.inflate(
                            R.layout.item_bus_info_cell_layout,
                            null, false);
            TextView header = (TextView) view.findViewById(
                            R.id.textview_bus_info_cell_header);
            header.setText("News:");

//            TextView newsView = (TextView) inflater.inflate(
//                    R.layout.item_bus_info_textview_plain,
//                    null, false);
//            newsView.setText(string);
//            view.addView(newsView);

            try {
                JSONArray news = new JSONArray(string);
                for (int i=0; i<news.length(); i++) {
                    LinearLayout newsView = (LinearLayout)
                            inflater.inflate(R.layout.item_bus_info_each_news, null, false);
                    JSONObject eachNews = news.getJSONObject(i);

                    if (!eachNews.getString(Utility.BUS_NEWS_IMG).equals("null")) {
                        ImageView newsImg = (ImageView) newsView.findViewById(
                                R.id.imageview_bus_info_news);
                        Picasso.with(mContext)
                                .load(eachNews.getString(Utility.BUS_NEWS_IMG))
                                .into(newsImg);
                    } else {
                        ImageView newsImg = (ImageView) newsView.findViewById(
                                R.id.imageview_bus_info_news);
                        newsView.removeView(newsImg);
                    }

                    if (!eachNews.getString(Utility.BUS_NEWS_TITLE).equals("null")) {
                        TextView newsHeader = (TextView) newsView.findViewById(
                                R.id.textview_bus_info_news_header);
                        newsHeader.setText(
                                eachNews.getString(Utility.BUS_NEWS_TITLE));
                    } else {
                        TextView newsHeader = (TextView) newsView.findViewById(
                                R.id.textview_bus_info_news_header);
                        newsView.removeView(newsHeader);
                    }


                    if (!eachNews.getString(Utility.BUS_NEWS_TEXT).equals("null")) {
                        TextView newsText = (TextView) newsView.findViewById(
                                R.id.textview_bus_info_news_info);
                        newsText.setText(eachNews.getString(Utility.BUS_NEWS_TEXT));
                    }

                    if (i == news.length()-1) {
                        ImageView divider = (ImageView) newsView.findViewById(
                                R.id.imageview_bus_info_news_separator);
                        divider.setImageAlpha(0);
                    }

                    view.addView(newsView);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG + ".attachNews",
                      "JSONException: " + e.getMessage());
            }

            this.addView(view);
        }

        private void attachLoy(String string) {

            if (Utility.VERBOSITY >= 1) {
                Log.v(LOG_TAG, "attachLoy: " + string);
            }
            TextView loyView = new TextView(mContext);
            loyView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            loyView.setText("This is loyalty");
//            loyView.setBackgroundColor(getResources().getColor(R.color.search_scrollview, null));
            this.addView(loyView);
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        /**
         * Perform initialization. Specifically, it passes in the data, the context,
         * sets up the layout and inflater
         * @param context   the context passed into constructor
         * @param cursor    the data passed into constructor
         */
        private void init(Context context, Cursor cursor) {

            data = cursor;
            mContext = context;

            inflater = LayoutInflater.from(mContext);

            ANDROID_VERSION = Build.VERSION.SDK_INT;

            // set layout
            this.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            this.setOrientation(LinearLayout.VERTICAL);
            int padVal = dpToPx(16);
            this.setPadding(padVal, padVal, padVal, padVal);

            if (ANDROID_VERSION >= Build.VERSION_CODES.LOLLIPOP) {
                this.setDividerDrawable(getResources().getDrawable(
                        R.drawable.linear_layout_divider, null));
            } else {
                this.setDividerDrawable(getResources().getDrawable(
                        R.drawable.linear_layout_divider));
            }
            this.setShowDividers(SHOW_DIVIDER_MIDDLE);

            if (data != null) createViews();
        }

        /**
         * Helper function to convert the desired dps
         * to pixels
         * @param dp    desired dps
         * @return      return int
         */
        private int dpToPx(float dp) {
            return Math.round(dp * getResources()
                    .getDisplayMetrics()
                    .density);

        }
    }

    public static class BusInfoImageViewHolder extends MaterialLeanBack.ViewHolder {

        protected ImageView imageView;

        public BusInfoImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

	/**
	 * This edit text handles extra text in business loyalty tab. This text edit has
	 * only adapted to text handling in business loyalty and hence should only be
	 * used here and not anywhere else.
	 */
    public static class BusLoyEditText extends EditText {

        private final String LOG_TAG = BusLoyEditText.class.getSimpleName();
        private BusLoyaltyAdapter mAdapter;
		private SmartRadioButton mRadioButton;
        private String mName;

        public BusLoyEditText(Context context){
            this(context, null, null);
        }

        public BusLoyEditText(Context context, BusLoyaltyAdapter adapter,
                              String name) {
            super(context);
            init(adapter, name);
        }

        public BusLoyEditText(Context context, SmartRadioButton radioButton) {
	        super(context);
	        init(radioButton);
        }

		private String getUserText() {
            return "[\"" + this.getText().toString() + "\"]";
        }

        private void init(BusLoyaltyAdapter adapter, String name) {

            // Set up fields
            mAdapter = adapter;
            mName = name;
            setAttributes();

            this.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    // Automatically submit the result when user finishes
                    // editing the text (when the textbox loses focused)
                    // TODO: this shit might need validation
                    if (!hasFocus) {
                        try {
                            mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_TEXT,
                                    mName,
                                    new JSONArray(getUserText()));
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSONException at BusLoyEditText: "
                                    + e.getMessage());
                        }

                    }

                }
            });
        }

		private void init(SmartRadioButton radioButton) {
			mRadioButton = radioButton;
			setAttributes();

			this.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					// we need that mRadioButton is checked, otherwise,
					// we can just have this edit text to lose focus
					// simply because user hits the radio button again,
					// which close this edit text, make it loses focus
					if (!hasFocus && mRadioButton.isChecked()) {
						mRadioButton.handleExtraButtons(BusLoyEditText.this,
                                                        getText().toString());
					}
				}
			});
		}

        private void setAttributes() {
            this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            this.setBackgroundResource(R.drawable.background_bus_loyal_options_fill);
        }

	    /**
	     * To make sure EditText will lose focus when the user closes soft-keyboard
	     * by pressing back button
	     * @param keyCode       the keycode
	     * @param event         the event of the click
	     * @return
	     */
	    @Override
	    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
			    this.clearFocus();
		    }

		    return super.onKeyPreIme(keyCode, event);
	    }
    }


    public interface BusLoyTicGroup {

        void modifyAnswer(SmartRadioButton button, String value);

        void modifyAnswer(SmartRadioButton button, JSONObject value);
    }

    /**
     * The purpose of BusLoyRadioGroup is to wrap a group of radio buttons, which is the
     * class SmartRadioButton. The reasons that we want BusLoyRadioGroup to wrap around
     * a bunch of SmartRadioButton is because:
     *      1. There will always be only zero or one checked radio button. That's why
     *         we need a wrapper over all radio buttons of the same purpose to
     *         guarantee this condition is held (automatically toggle off an active
     *         radio button when another radio button is checked, automatically toggle
     *         off a radio button if that button is hit again)
     *      2. To simplify the structure of BusLoyaltyAdapter. Since there are total of
     *         7 variants of radio buttons, without this BusLoyRadioGroup wrapper,
     *         BusLoyaltyAdapter will need a lot of extra methods to deal specifically
     *         just with radio buttons, which can make the code of BusLoyaltyAdapter
     *         very complicated and hard to track
     *      3. To guarantee that all radio buttons have the same layout appearances
     *         (share the same margin and padding)
     *
     * The overall concept of this interaction between BusLoyaltyAdapter, BusLoyRadioGroup
     * and SmartRadioButton is as follow:
     *  _________________________________________
     * |Adapter - Item n                         |
     * |     ____________________________________|
     * |    |Header                              | Text     /\          /\
     * |    |____________________________________| View     \/          |
     * |    |BusLoyRadioGroup                    |          /\          |
     * |    |                                    |          |           |
     * |    |    ________________________________|          |           |
     * |    |   |SmartRadioButton1 |             |  Linear  |   Linear  |
     * |    |   |Button            |Extra info   |  Layout  |   Layout  |
     * |    |   |__________________|_____________|          |           |
     * |    |   |SmartRadioButton2 |             |          |           |
     * |    |   |Button            |Extra info   |          |           |
     * |____|___|__________________|_____________|         \/          \/
     * |Adapter - Item n+1                       |
     * |                                         |
     *
     * Ask: what if I change BusLoyRadioGroup from LinearLayout to ListView?
     * Answer: should not do, I would probably have two nesting ListView,
     *         creating unexpected behaviors
     * Then the radio button would be an expandable ListView. It will minimize
     * the UI short-comings of having too much dialogs, but it will introduce
     * more syncing difficulties
     */
    public static class BusLoyRadioGroup extends LinearLayout
                implements BusLoyTicGroup {

        private final String LOG_TAG = BusLoyRadioGroup.class.getSimpleName();
        private BusLoyaltyAdapter mAdapter;
        private String mName;
        private JSONArray mOptions;
        private JSONArray mCurrentChoice = new JSONArray();
        private SmartRadioButton mActive;
        private Context mContext;

        public BusLoyRadioGroup(Context context) {
            this(context, null, null, null);
        }

	    /**
	     * Public constructor that creates an instance of this class
	     * @param context   the context where this group is instantiated
	     * @param adapter   the adapter that holds this view
	     * @param name      the question name (will serve as key in adapter's JSON)
	     * @param options   list of choices that will be populated as radio buttons
	     */
        public BusLoyRadioGroup(Context context, BusLoyaltyAdapter adapter,
                                String name, String options) {
            super(context);
            init(context, adapter, name, options);
            layOptions();
        }

        private void init(Context context, BusLoyaltyAdapter adapter, String name,
                          String options) {
            mAdapter = adapter;
            mName = name;
            mContext = context;
            setDefaultAttributes();
            try {
                mOptions = new JSONArray(options);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException in initialization "
                        + LOG_TAG + ": " + e.getMessage());
            }
        }

        private void setDefaultAttributes() {
            this.setOrientation(HORIZONTAL);
            this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

        private void layOptions() {

            for (int i=0; i<mOptions.length(); i++) {
                try {

                    if (mOptions.get(i) instanceof JSONObject) {

	                    // if any of the radio button contains extra fields, then you will
	                    // want this whole group to have VERTICAL orientation. If all
	                    // radio buttons are just a simple text, then it should be HORIZONTAL
						this.setOrientation(VERTICAL);
                        SmartRadioButton smartButton =
                                new SmartRadioButton(mContext,
                                        (JSONObject) mOptions.get(i));
                        smartButton.setGroup(this);
                        this.addView(smartButton);
                    } else if (mOptions.get(i) instanceof String) {
                        SmartRadioButton smartButton =
                                new SmartRadioButton(mContext,
                                        (String) mOptions.get(i));
                        smartButton.setGroup(this);
                        this.addView(smartButton);
                    }


                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException in layout options: "
                            + e.getMessage());
                }
            }

        }

        @Override
        public void modifyAnswer(SmartRadioButton button, String value) {

            if (mCurrentChoice.isNull(0)) {
	            // if currently there isn't any active choice
                mActive = button;
                mCurrentChoice.put(value);
	            mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
			                          mName,
			                          mCurrentChoice);
            } else {

                if (value == null) {
                    // this is the case the user deselects the button
                    mActive = null;
                    mCurrentChoice = new JSONArray();
                    mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
                                          mName,
                                          null);
                } else {
                    // this is the case the user selects the button,
                    // you want to deselect other button
                    mActive.toggle(false);
                    mActive = button;
                    try {
                        mCurrentChoice.put(0, value);
                        mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
                                mName,
                                mCurrentChoice);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSONException when " +
                                "modify Answers: " + e.getMessage());
                    }
                }
            }
        }

        @Override
        public void modifyAnswer(SmartRadioButton button, JSONObject value) {

            if (value == null) {
                // this is the case when the user immediately deselects
                // currently active choice, we our tracking of buttons
                // and reset adapter JSON
                mActive = null;
                mCurrentChoice = new JSONArray();
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
                                      mName,
                                      null);
                return;
            }

            if (mActive == null) {
                // if there isn't any active choice
                mActive = button;
                mCurrentChoice.put(value);
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
                                      mName,
                                      mCurrentChoice);
            } else {
                if (mActive != button) {
                    mActive.toggle(false);
                    mActive = button;
                }
                try {
                    mCurrentChoice.put(0, value);
                    mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_RADIO,
                            mName,
                            mCurrentChoice);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException when modify Adapter JSON: "
                            + e.getMessage());
                }
            }

        }

    }

	/**
	 * Smart radio button will create its own layout depending on whether it
	 * only contains a single option, or it contains a hierarchy of options
	 */
    public static class SmartRadioButton extends LinearLayout {

        private final String LOG_TAG = SmartRadioButton.class.getSimpleName();
        private String mOption;
        private JSONObject mExtraOption = null;
        private BusLoyTicGroup mGroup;
        private boolean mChecked = false;
        private Context mContext;
        private TextView button;
		private LinearLayout mExtraQuestion;

        // these three variables keep track of the active extra answers
        // expect for an instance of SmartRadioButton will have at most
        // 1 of these variables instantiated
        private BusLoyEditText activeTextField = null;
        private SimpleButton activeRadioButton = null;
        private Set<SimpleButton> activeCheckButtons =
                new HashSet<>();
        private Set<String> activeCheckButtonsValues = new HashSet<>();

        // these two variables stores user answer to make check box group
        // have faster time remove choices that the user deselects (not
        // having to do iterations to find the choice to remove)
        private String mAnswerString;
        private JSONObject mAnswerJSON;


        public SmartRadioButton(Context context) {
            super(context);
        }

        public SmartRadioButton(Context context, String option) {
            super(context);
            init(context, option);
        }

        public SmartRadioButton(Context context, JSONObject options) {
            super(context);
            init(context, options);
        }

	    /**
	     * Common initialization for both
	     * @param context   the context where smart radio button is initiated
	     */
        private void init(Context context) {
            mContext = context;
            setDefaultAttributes();
        }

        private void init(Context context, JSONObject options) {
            mExtraOption = options;
            init(context);
            this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            button = createButton(mExtraOption);
	        LinearLayout extraAnswer = createExtraButtons(mExtraOption);
//            PredicateLayout extraAnswer = createRelativeButton(mExtraOption);

            this.addView(button);
            this.addView(extraAnswer);

        }

        private void init(Context context, String option) {
            mOption = option;
            init(context);

            button = createButton(mOption);
            this.addView(button);
        }

        private void setDefaultAttributes() {
            this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
	        this.setOrientation(VERTICAL);
            this.setPadding(50, 10, 10, 15);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle(boolean check) {

            if (check) {
                mChecked = true;

	            // set the button from transparent to fill
	            button.setBackgroundResource(
                        R.drawable.background_bus_loyal_options_fill);

	            // show extra questions
	            if (mExtraOption != null) {
		            mExtraQuestion.setVisibility(VISIBLE);
	            }

            } else {
                mChecked = false;

	            // set the button from transparent to fill
                button.setBackgroundResource(
                        R.drawable.background_bus_loyal_options_transparent);

	            // hide extra questions
	            if (mExtraOption != null) {
		            mExtraQuestion.setVisibility(GONE);
                    clearActiveAnswer();
	            }
            }

        }

        @TargetApi(23)
        public TextView createButton(String name) {

            TextView buttonName = new TextView(mContext);
            buttonName.setText(name);
            buttonName.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            buttonName.setBackgroundResource(
                    R.drawable.background_bus_loyal_options_transparent);
            buttonName.setTextColor(getResources().getColor(R.color.white, null));
            buttonName.setPadding(10, 10, 10, 10);


            buttonName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked()) {
                        toggle(false);
	                    mGroup.modifyAnswer(SmartRadioButton.this, (String) null);
                    } else {
                        toggle(true);
	                    mGroup.modifyAnswer(SmartRadioButton.this, mOption);
                    }
                }
            });

            return buttonName;
        }


        @TargetApi(23)
        public TextView createButton(JSONObject options) {

            TextView buttonName = new TextView(mContext);

            try {
                buttonName.setText(options.getString("name"));
                buttonName.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                buttonName.setBackgroundResource(
                        R.drawable.background_bus_loyal_options_transparent);
                buttonName.setTextColor(getResources().getColor(R.color.white, null));
                buttonName.setPadding(10, 10, 10, 10);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException in create button: " + e.getMessage());
            }

	        buttonName.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
			        if (isChecked()) {
				        toggle(false);
                        getGroup().modifyAnswer(SmartRadioButton.this,
                                (JSONObject) null);
			        } else {
                        handleExtraButtons();
                        toggle(true);
                    }
		        }
	        });

            return buttonName;
        }

        @TargetApi(23)
		public LinearLayout createExtraButtons(JSONObject options) {

			String type;

			mExtraQuestion = new LinearLayout(mContext);

			// set attributes for extraOptions
			//  TODO: if this layout has fixed attributes, put them into xml
			mExtraQuestion.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
			));
			mExtraQuestion.setOrientation(VERTICAL);
            mExtraQuestion.setPadding(20, 10, 0, 20);
            mExtraQuestion.setVisibility(GONE);


			// deal with it!
			if (!options.isNull("desc")) {
				try {
					TextView extraHeader = new TextView(mContext);
					extraHeader.setText(options.getString("desc"));
                    extraHeader.setTextColor(getResources().getColor(R.color.white, null));
					mExtraQuestion.addView(extraHeader);
				} catch (JSONException e) {
					Log.e(LOG_TAG, "JSONException in create header"
							+ " for button extra: " + e.getMessage());
				}
			}

			try {
				type = options.getString("type");
			} catch (JSONException e) {
				Log.e(LOG_TAG, "JSONException in extracting type: "
						+ e.getMessage());
				return null;
			}

			switch (type) {

				case Utility.BUS_LOYAL_EXTRA_TEXT: {
					mExtraQuestion.addView(new BusLoyEditText(mContext, this));
					break;
				}

				case Utility.BUS_LOYAL_EXTRA_RADIO: {
					try {
						JSONArray optionList = options.getJSONArray("options");
						for (int i=0; i<optionList.length(); i++) {
                            mExtraQuestion.addView(new SimpleButton(
                                    mContext, SmartRadioButton.this,
                                    optionList.getString(i),
                                    Utility.BUS_LOYAL_EXTRA_RADIO));
						}
					} catch (JSONException e) {
						Log.e(LOG_TAG, "JSONException in extracting radio" +
                                "options: " + e.getMessage());
					}
					break;
				}

				case Utility.BUS_LOYAL_EXTRA_CHECK: {
                    try {
                        JSONArray optionList = options.getJSONArray("options");
                        for (int i=0; i<optionList.length(); i++) {
                            mExtraQuestion.addView(new SimpleButton(
                                    mContext, SmartRadioButton.this,
                                    optionList.getString(i),
                                    Utility.BUS_LOYAL_EXTRA_CHECK));
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSONException in extracting check box" +
                                "options: " + e.getMessage());
                    }
					break;
				}
			}


			return mExtraQuestion;
		}

		/**
         * Handle informing the parent group (BusLoyRadioButton)
         * that this button is chosen, has child views, but none
         * of the child views are selected.
         * This will call the RadioGroup to
         */
        public void handleExtraButtons() {
            JSONObject answer = new JSONObject();
            try {
                answer.put("name", mExtraOption.get("name"));
                answer.put("extra", null);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException in radio button "
                        + e.getMessage());
            }
            getGroup().modifyAnswer(this, answer);
        }

		/**
		 * Handle text written in EditText. This method will be called by the
		 * child text field
         * @param textField the text field instance (need when clear)
		 * @param string    the string that user types
		 */
		public void handleExtraButtons(BusLoyEditText textField, String string) {

			JSONObject answer = new JSONObject();
            activeTextField = textField;

			try {
				answer.put("name", mExtraOption.get("name"));
				answer.put("extra", string);

			} catch (JSONException e) {
				Log.e(LOG_TAG, "JSONException in radio button handling "
						+ "text field: " + e.getMessage());
			}
			getGroup().modifyAnswer(this, answer);
		}

		/**
         * Handle text chosen in SimpleRadioButton. This method will be called
         * by child's radio button
         * @param button    the button that call this method
         * @param string    the string that user chooses
         */
        public void handleExtraButtons(String type,
                                       SimpleButton button, String string) {

            switch(type) {
                case Utility.BUS_LOYAL_EXTRA_RADIO: {

                    JSONObject answer = new JSONObject();
                    if (string == null) {
                        // string == null means the user deselects an extra button
                        activeRadioButton = null;
                    } else {
                        if (activeRadioButton != null) {
                            activeRadioButton.toggle(false);
                        }
                        activeRadioButton = button;
                    }

                    try {
                        answer.put("name", mExtraOption.get("name"));
                        answer.put("extra", string);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSONException in radio button handling "
                                + "radio button: " + e.getMessage());
                    }
                    getGroup().modifyAnswer(this, answer);
                    break;
                }

                case Utility.BUS_LOYAL_EXTRA_CHECK: {

                    JSONObject answer = new JSONObject();
                    if (string == null) {
                        // string == null means the user deselects an
                        // extra button, we would want to remove that
                        // extra button from activeCheckButtons
                        activeCheckButtons.remove(button);
                        activeCheckButtonsValues.remove(button.getValue());
                    } else {
                        // user selects a new button, we would want to
                        // add that button to activeCheckButtons
                        activeCheckButtons.add(button);
                        activeCheckButtonsValues.add(button.getValue());
                    }

                    // update JSONObject answer
                    try {
                        answer.put("name", mExtraOption.get("name"));
                        answer.put("extra", new JSONArray(activeCheckButtonsValues));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSONException in radio button handling "
                                + "check boxes: " + e.getMessage());
                    }
                    getGroup().modifyAnswer(this, answer);
                    break;
                }
            }
        }


        public void clearActiveAnswer() {

            if (activeRadioButton != null) {
                activeRadioButton.toggle(false);
                activeRadioButton = null;
            } else if (activeTextField != null) {
                activeTextField.setText("");
                activeTextField = null;
            } else if (!activeCheckButtons.isEmpty()) {
                activeCheckButtonsValues = new HashSet<>();
                Iterator<SimpleButton> iterator =
                        activeCheckButtons.iterator();
                while (iterator.hasNext()) {
                    iterator.next().toggle(false);
                }
                activeCheckButtons = new HashSet<>();
            }
        }


		/**
         * These methods get user's answer
         */
        public String getAnswerString() {
            return mAnswerString;
        }

        public JSONObject getAnswerJSON() {
            return mAnswerJSON;
        }

		/**
		 * These methods set user's answer
         */
        public void setAnswerString(String answer) {
            mAnswerString = answer;
        }

        public void setAnswerJSON(JSONObject answer) {
            mAnswerJSON = answer;
        }

        /**
         * Get the radio group that wraps this radio button
		 * @return  the registered radio group for this radio button
		 */
		public BusLoyTicGroup getGroup() {
			return mGroup;
		}


        /**
         * This method should be called after this object is instantiated
         * @param group     the group that handles this button
         */
        public void setGroup(BusLoyTicGroup group) {
            mGroup = group;
        }
    }


    public static class BusLoyCheckBoxGroup extends LinearLayout
                implements BusLoyTicGroup {

        private final String LOG_TAG = BusLoyCheckBoxGroup.class.getSimpleName();
        private Context mContext;
        private BusLoyaltyAdapter mAdapter;
        private String mName;
        private JSONArray mOptions = null;
        private Set<Object> mCurrentChoice = new HashSet<>();
        private Set<SmartRadioButton> mActiveChoices = new HashSet<>();


	    /**
         * Public constructor for checkbox group
         * @param context   the context where this check box group appears
         * @param adapter   the adapter that contains the information for
         *                  this check box group (used to pass user answers)
         * @param name      the question for this checkbox group
         * @param options   the list of answers for this group. This options
         *                  string must have the following format ["string1",
         *                  {object2},...] so that it can be parsed as a
         *                  json array
	     */
        public BusLoyCheckBoxGroup(Context context,  BusLoyaltyAdapter adapter,
                                   String name, String options) {
            super(context);
            init(context, adapter, name, options);
            layOptions();
        }

        private void init(Context context, BusLoyaltyAdapter adapter,
                          String name, String options) {
            mContext = context;
            mAdapter = adapter;
            mName = name;
            try {
                mOptions = new JSONArray(options);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException in initializing "
                        + LOG_TAG + ": " + e.getMessage());
            }

            setDefaultAttributes();
        }

        private void setDefaultAttributes() {
            setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            setOrientation(HORIZONTAL);
        }

        private void layOptions() {

            if (mOptions == null) {
                return;
            }

            for (int i=0; i<mOptions.length(); i++) {
                try {

                    if (mOptions.get(i) instanceof JSONObject) {

                        setOrientation(VERTICAL);
                        SmartRadioButton smartButton =
                                new SmartRadioButton(mContext,
                                        (JSONObject) mOptions.get(i));
                        smartButton.setGroup(this);
                        this.addView(smartButton);

                    } else if (mOptions.get(i) instanceof String) {

                        SmartRadioButton smartButton =
                                new SmartRadioButton(mContext,
                                        (String) mOptions.get(i));
                        smartButton.setGroup(this);
                        this.addView(smartButton);

                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException in laying out " +
                            "options: " + e.getMessage());
                }

            }

        }

        @Override
        public void modifyAnswer(SmartRadioButton button, String value) {

            if (value == null) {
                // means that the button is deselected to become inactive,
                // we would remove this button from mActiveChoices
                mCurrentChoice.remove(button.getAnswerString());
                mActiveChoices.remove(button);
            } else {
                // means that this is the new answer to be recorded, we
                // would add this button's answer to mCurrentChoice
                mCurrentChoice.add(value);
                mActiveChoices.add(button);
                button.setAnswerString(value);
            }

            if (mCurrentChoice.isEmpty()) {
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_CHECK,
                                      mName, null);
            } else {
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_CHECK,
                                      mName, new JSONArray(mCurrentChoice));
            }

        }

        @Override
        public void modifyAnswer(SmartRadioButton button, JSONObject value) {

            if (value == null) {
                // means that the button is deselected to become inactive,
                // we would remove this button from mActiveChoices and
                // the corresponding answer from mCurrentChoice
                mCurrentChoice.remove(button.getAnswerJSON());
                mActiveChoices.remove(button);
                button.setAnswerJSON(null);
            } else {

                if (mActiveChoices.contains(button)) {
                    // means that this button is already recorded and the
                    // user wants to update the answer, we would modify
                    // mCurrentChoice
                    mCurrentChoice.remove(button.getAnswerJSON());
                    mCurrentChoice.add(value);
                    button.setAnswerJSON(value);
                } else {
                    // means that this is the new answer to be recorded, we
                    // would add this button to mActiveChoices and the
                    // corresponding answer to mCurrentChoices
                    mCurrentChoice.add(value);
                    button.setAnswerJSON(value);
                    mActiveChoices.add(button);
                }
            }

            if (mCurrentChoice.isEmpty()) {
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_CHECK,
                        mName, null);
            } else {
                mAdapter.modifyAnswer(Utility.BUS_LOYAL_EXTRA_CHECK,
                        mName, new JSONArray(mCurrentChoice));
            }

        }
    }

	public static class SimpleButton extends TextView {

		private String mOption;
		private boolean mCheck = false;
        private SmartRadioButton mSmartRadioButton;
        private String mType;

		public SimpleButton(Context context) {
			this(context, null, null, null);
		}

		public SimpleButton(Context context, SmartRadioButton button,
                            String option, String type) {
			super(context);
			init(button, option, type);
		}

		private void init(SmartRadioButton button, String option,
                          String type) {
			mOption = option;
            mSmartRadioButton = button;
            mType = type;
            setAttributes();

            this.setText(mOption);

			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isChecked()) {
						toggle(false);
                        mSmartRadioButton.handleExtraButtons(
                                mType,
                                SimpleButton.this,
                                null);
					} else {
						toggle(true);
                        mSmartRadioButton.handleExtraButtons(
                                mType,
                                SimpleButton.this,
                                mOption);
					}
				}
			});
		}

		public boolean isChecked() {
			return mCheck;
		}

		@TargetApi(23)
		private void setAttributes() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            setLayoutParams(params);
            setPadding(10, 10, 10, 10);
			setBackgroundResource(
					R.drawable.background_bus_loyal_options_transparent);
			setTextColor(getResources().getColor(R.color.white, null));
		}

		private void toggle(boolean check) {
			mCheck = check;
			if (check) {
				setBackgroundResource(
						R.drawable.background_bus_loyal_options_fill);
			} else {
				setBackgroundResource(
						R.drawable.background_bus_loyal_options_transparent);
			}
		}

        public String getValue() {
            return mOption;
        }
	}
}
