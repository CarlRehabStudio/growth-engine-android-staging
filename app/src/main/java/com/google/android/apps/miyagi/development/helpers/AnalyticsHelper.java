package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.os.Bundle;

import com.google.android.apps.miyagi.development.R;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by emilzawadzki on 07.02.2017.
 */

public class AnalyticsHelper {

    private FirebaseAnalytics mFirebaseAnalytics;
    private Context mContext;

    public AnalyticsHelper(Context context) {
        mContext = context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    /**
     * Track screen in google analytics.
     */
    public void trackScreen(final String screenName) {
        Bundle params = new Bundle();
        params.putString(mContext.getResources().getString(R.string.parameter_screen), screenName);
        mFirebaseAnalytics.logEvent(mContext.getResources().getString(R.string.event_category_screen), params);
    }

    /**
     * Track custom event in google analytics.
     */
    public void trackEvent(final String screenName, final String category, final String action, final String label, final String value) {
        Bundle params = new Bundle();
        if (screenName != null) {
            params.putString(mContext.getResources().getString(R.string.parameter_screen), screenName);
        }
        params.putString(mContext.getResources().getString(R.string.parameter_category), category);
        params.putString(mContext.getResources().getString(R.string.parameter_action), action);
        params.putString(mContext.getResources().getString(R.string.parameter_label), label);
        if (value != null) {
            params.putString(mContext.getResources().getString(R.string.parameter_value), value);
        }
        mFirebaseAnalytics.logEvent(category, params);
    }

    public void trackEvent(final String screenName, final String category, final String action, final String label) {
        trackEvent(screenName, category, action, label, null);
    }

    public void trackEvent(final String screenName, final String category, final String action, final int label) {
        trackEvent(screenName, category, action, Integer.toString(label), null);
    }

}
