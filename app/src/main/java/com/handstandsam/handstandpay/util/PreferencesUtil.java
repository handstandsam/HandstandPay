package com.handstandsam.handstandpay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.handstandsam.handstandpay.contstants.Constants;
import com.handstandsam.handstandpay.model.MagStripeData;

/**
 * Created by handstandtech on 7/26/15.
 */
public class PreferencesUtil {
    public static final String PREF_KEY_SWIPE_DATA = "SWIPE_DATA";

    public static String getRawSwipeData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String swipeData = prefs.getString(PREF_KEY_SWIPE_DATA, Constants.DEFAULT_SWIPE_DATA);
        return swipeData;
    }

    /*
     *  Save new magnetic stripe data to the shared preference storage of our app.
     */
    public static void setSwipeData(Context context, String newSwipeData) {
        newSwipeData = newSwipeData.trim();
        SharedPreferences.Editor prefEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefEditor.putString(PREF_KEY_SWIPE_DATA, newSwipeData);
        prefEditor.commit();
    }

    public static MagStripeData getMagStripeData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String swipeData = prefs.getString(PREF_KEY_SWIPE_DATA, Constants.DEFAULT_SWIPE_DATA);
        MagStripeData msd = MagStripeParser.parseTrackData(swipeData);
        return msd;
    }
}
