package com.ducnguyen.duo.bus.loyalty;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ducnguyen.duo.CustomViews;
import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.bus.BusActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ducprogram on 4/12/16.
 */
public class BusLoyaltyAdapter extends CursorAdapter {

    private final String LOG_TAG = BusLoyaltyAdapter.class.getSimpleName();
    private Context mContext;

	/**
	 * This json object records user's answers in business loyalty page with
     * the format {"question": [answer]}. Answer is a json array to accommodate
     * that there can be many answers for checkboxes. To simplify, answer for
     * text box and radio button are also recorded as a single-element json
     * array.
     * Answer json array can contain string (simple answer) or json object
     * (nested answers - answer for answer). Nested answers appear in checkbox
	 * and radio box questions, when user selects an option and has to provide
	 * extra information regarding that option. Nested answers will have the
	 * following format {"name": "chosen option", "extra": [extra answer]}.
	 * Here, [extra answer] is a json array of string elements to account for
	 * possible multiple extra answers from checkbox-type questions. [extra
	 * answer] will not store json, otherwise user faces the risk of endless
	 * questionnaires.
     */
    private JSONObject extraAnswers = new JSONObject();


	/**
	 * This function will be called when user answers extra loyalty questions.
	 * This function's purpose is to change JSON extraAnswers as the user answers
	 * questions. So that when the user clicks REGISTER, this adapter will send
	 * the JSON extraAnswers to the server to complete registration.
     * @param type      the type of questions that the user answer. The current
	 *                  types are text answer, radio button answer (single choice)
	 *                  check box answer (multiple choice), which are corresponded
	 *                  by BUS_LOYAL_EXTRA_TEXT, BUS_LOYAL_EXTRA_RADIO,
	 *                  BUS_LOYAL_EXTRA_CHECK
     * @param key       the key of the corresponding question in the JSON extraAnswer
     * @param values    user's answer. This answer will be mapped with key
     */
    public void modifyAnswer(String type, String key, JSONArray values) {

        switch(type) {

            case Utility.BUS_LOYAL_EXTRA_TEXT: {
                try {
                    extraAnswers.put(key, values);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem updating answer "
                            + "text in BusLoyaltyFragment: "
                            + e.getMessage());
                }
                break;
            }

            case Utility.BUS_LOYAL_EXTRA_RADIO: {

                try {
                    if (values == null) {
                        extraAnswers.remove(key);
                    } else {
                        extraAnswers.put(key, values);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem updating answer "
                            + "radio in BusLoyaltyFragment: "
                            + e.getMessage());
                }
                break;
            }

            case Utility.BUS_LOYAL_EXTRA_CHECK: {

                try {
                    if (values == null) {
                        extraAnswers.remove(key);
                    } else {
                        extraAnswers.put(key, values);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem updating answer "
                            + "checkboxes in BusLoyaltyFragment: "
                            + e.getMessage());
                }
                break;
            }
        }
	    Log.v(LOG_TAG, extraAnswers.toString());
    }

    private static final int VIEW_GREET = 0;
    private static final int VIEW_MESS = 1;
    private static final int VIEW_PERSONAL = 2;
    private static final int VIEW_SEPARATE = 3;
    private static final int VIEW_GENERAL = 4;
    private static final int VIEW_INITIAL_REGISTER_BUTTON = 5;
    private static final int VIEW_OTHER_INFORMATION = 6;
    private static final int VIEW_FINAL_REGISTER_BUTTON = 7;
    private static final int VIEW_TYPE_COUNT = 8;

    private static final int COL_BUSID = 0;
    private static final int COL_GREETING = 1;
    private static final int COL_MESSAGE = 2;
    private static final int COL_IMG = 3;
    private static final int COL_ITEM = 4;
    private static final int COL_ITEM_DESC = 5;
    private static final int COL_PTS = 6;
    private static final int COL_TYPE = 7;

    public BusLoyaltyAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = -1;

        switch(cursor.getInt(COL_TYPE)) {

            case VIEW_GREET: {

                layoutId = R.layout.item_bus_loy_textview_plain;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TextView textView = (TextView) view.findViewById(R.id.textView_bus_loyal_plain);
                view.setTag(textView);
                return view;
            }

            case VIEW_MESS: {

                layoutId = R.layout.item_bus_info_textview_plain;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TextView textView = (TextView) view.findViewById(R.id.textview_bus_info_plain);
                view.setTag(textView);
                return view;
            }

            case VIEW_PERSONAL: {

                layoutId = R.layout.item_bus_loy_deal;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                DealViewHolder dealViewHolder = new DealViewHolder(view);
                view.setTag(dealViewHolder);
                return view;
            }

            case VIEW_SEPARATE: {

                layoutId = R.layout.item_bus_loy_separate;
                return LayoutInflater.from(context).inflate(layoutId, parent, false);
            }

            case VIEW_GENERAL: {

                layoutId = R.layout.item_bus_loy_deal;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                DealViewHolder dealViewHolder = new DealViewHolder(view);
                view.setTag(dealViewHolder);
                return view;
            }

            case VIEW_INITIAL_REGISTER_BUTTON: {

                layoutId = R.layout.item_bus_loy_button;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                Button button = (Button) view.findViewById(R.id.button_bus_loy);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String busID = ((BusActivity) mContext).getBusId();
                        Map<String, String> query = new HashMap<>();
                        query.put(Utility.URI_BUSID, busID);
                        query.put(Utility.URI_BUS_KEY, Utility.URI_LOYALTY_MORE);
                        Uri url = Utility.buildUri(Utility.URI_BUS, query);
                        if (Utility.VERBOSITY >= 2) {
                            Log.v("TEST onCLICK", "IT IS CLICKED!");
                        }
                        new Utility.UpdateDatabase(mContext, url, Utility.URI_LOYALTY_MORE)
                                .execute();
                        extraAnswers = new JSONObject();
                    }
                });
                view.setTag(button);
                return view;
            }

            case VIEW_OTHER_INFORMATION: {

                layoutId = R.layout.item_bus_loy_extra;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TextView textView = (TextView) view.findViewById(
                                    R.id.textView_bus_loyal_extra_header);
                view.setTag(textView);
                return view;
            }

            case VIEW_FINAL_REGISTER_BUTTON: {

                layoutId = R.layout.item_bus_loy_button;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                Button button = (Button) view.findViewById(R.id.button_bus_loy);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: confirm, register and change the state of button
                        // to REGISTERED and color green here
                        Log.v(LOG_TAG, "THIS SHIT IS CLICKED");
                    }
                });
                view.setTag(button);
                return view;
            }

            default:
                throw new UnsupportedOperationException(LOG_TAG + " has to handle "
                        + "unknown loyalty type: " + cursor.getInt(COL_TYPE));
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        switch(cursor.getInt(COL_TYPE)) {

            case VIEW_GREET: {

                TextView textView = (TextView) view.getTag();
                textView.setText(cursor.getString(COL_GREETING));
                break;
            }

            case VIEW_MESS: {

                TextView textView = (TextView) view.getTag();
                textView.setText(cursor.getString(COL_MESSAGE));
                break;
            }

            case VIEW_PERSONAL: {

                DealViewHolder dealViewHolder = (DealViewHolder) view.getTag();

                Picasso.with(mContext)
                        .load(cursor.getString(COL_IMG))
                        .into(dealViewHolder.itemImage);
                dealViewHolder.itemText.setText(cursor.getString(COL_ITEM));
                dealViewHolder.descText.setText(cursor.getString(COL_ITEM_DESC));
                dealViewHolder.ptsText.setText(cursor.getString(COL_PTS) + " pts");

                break;
            }

            case VIEW_SEPARATE: {
                break;
            }

            case VIEW_GENERAL: {

                try {
                    DealViewHolder dealViewHolder = (DealViewHolder) view.getTag();
                    Picasso.with(mContext)
                            .load(cursor.getString(COL_IMG))
                            .into(dealViewHolder.itemImage);
                    dealViewHolder.itemText.setText(cursor.getString(COL_ITEM));
                    dealViewHolder.descText.setText(cursor.getString(COL_ITEM_DESC));
                    dealViewHolder.ptsText.setText(cursor.getString(COL_PTS) + " pts");
                } catch (Error e) {
                    Log.v(LOG_TAG, "cursor that has error: " + cursor.getString(COL_ITEM));
                    throw new Error("Error with VIEW_GENERAL");
                }

                break;
            }

            case VIEW_INITIAL_REGISTER_BUTTON: {
                break;
            }

            case VIEW_OTHER_INFORMATION: {

                TextView header = (TextView) view.getTag();
                header.setText(cursor.getString(COL_ITEM));

                switch(cursor.getString(COL_ITEM_DESC)) {

                    case Utility.BUS_LOYAL_EXTRA_TEXT: {
                        CustomViews.BusLoyEditText editText =
                                new CustomViews.BusLoyEditText(
                                        mContext, this,
                                        cursor.getString(COL_ITEM)
                                );

	                    // this code is used to remove redundant entry result
	                    // from adding view dynamically: when Android recycles
	                    // this view, this view already has header text and the
	                    // text box. Since we add the text box dynamically,
	                    // the above text box is then added on top of the
	                    // existing text box, creating an unnecessary extra
	                    // text box. To resolve, we manually delete the extra
	                    // one if the total number of child views of this view
	                    // is 3 (header text, text box, extra text box). This
	                    // problem and solution apply to the two other cases
                        ((LinearLayout) view).addView(editText);
                        if (((LinearLayout) view).getChildCount() == 3) {
                            ((LinearLayout) view).removeViewAt(2);
                        }
                        break;
                    }

                    case Utility.BUS_LOYAL_EXTRA_RADIO: {

                        CustomViews.BusLoyRadioGroup radioGroup =
                                new CustomViews.BusLoyRadioGroup(
                                        mContext, this,
                                        cursor.getString(COL_ITEM),
                                        cursor.getString(COL_MESSAGE)
                                );

                        ((LinearLayout) view).addView(radioGroup);
                        if (((LinearLayout) view).getChildCount() == 3) {
                            ((LinearLayout) view).removeViewAt(2);
                        }

                        break;
                    }

                    case Utility.BUS_LOYAL_EXTRA_CHECK: {

	                    CustomViews.BusLoyCheckBoxGroup checkGroup =
			                    new CustomViews.BusLoyCheckBoxGroup(
				                        mContext, this,
					                    cursor.getString(COL_ITEM),
					                    cursor.getString(COL_MESSAGE));

	                    ((LinearLayout) view).addView(checkGroup);
	                    if (((LinearLayout) view).getChildCount() == 3) {
		                    ((LinearLayout) view).removeViewAt(2);
	                    }
                        break;
                    }

                    default:
                        throw new UnsupportedOperationException("Unsupported "
                                + "question type: " + cursor.getString(COL_ITEM_DESC));
                }

                break;
            }

            case VIEW_FINAL_REGISTER_BUTTON: {
                break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        Cursor data = getCursor();

        if (!data.moveToPosition(position)) {
            throw new UnsupportedOperationException("getItemViewType: cursor does not have" +
                                                    " value at position: " + position);
        }

        int viewType = data.getInt(COL_TYPE);

        switch(viewType) {

            case VIEW_GREET: {
                return VIEW_GREET;
            }

            case VIEW_MESS: {
                return VIEW_MESS;
            }

            case VIEW_PERSONAL: {
                return VIEW_PERSONAL;
            }

            case VIEW_SEPARATE: {
                return VIEW_SEPARATE;
            }

            case VIEW_GENERAL: {
                return VIEW_GENERAL;
            }

            case VIEW_INITIAL_REGISTER_BUTTON: {
                return VIEW_INITIAL_REGISTER_BUTTON;
            }

            case VIEW_OTHER_INFORMATION: {
                return VIEW_OTHER_INFORMATION;
            }

            case VIEW_FINAL_REGISTER_BUTTON: {
                return VIEW_FINAL_REGISTER_BUTTON;
            }

            default:
                throw new UnsupportedOperationException(
                        "getItemViewType: no such type " + position);
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public static class DealViewHolder {

        ImageView itemImage;
        TextView itemText;
        TextView descText;
        TextView ptsText;

        public DealViewHolder(View view) {
            itemImage = (ImageView) view.findViewById(R.id.imageview_bus_loyal_deal);
            itemText = (TextView) view.findViewById(R.id.textview_bus_loyal_name);
            descText = (TextView) view.findViewById(R.id.textview_bus_loyal_description);
            ptsText = (TextView) view.findViewById(R.id.textview_bus_loyal_pts);
        }
    }

}
