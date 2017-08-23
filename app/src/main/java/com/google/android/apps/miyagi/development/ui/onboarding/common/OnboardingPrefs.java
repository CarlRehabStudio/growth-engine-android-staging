package com.google.android.apps.miyagi.development.ui.onboarding.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lukaszweglinski on 21.12.2016.
 */

public class OnboardingPrefs {

    private static final String PREF_KEY = "Onboarding";
    private final Context mContext;

    public OnboardingPrefs(Context context) {
        mContext = context;
    }

    /**
     * Is onboarding required return true if should show.
     *
     * @param onboardingType OnboardingType  type to check.
     * @return true if required, false otherwise.
     */
    public boolean isOnboardingRequired(OnboardingType onboardingType) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean(onboardingType.name(), false);
    }

    /**
     * Sets onboarding visited.
     *
     * @param onboardingType the onboarding type.
     */
    public void setOnboardingVisited(OnboardingType onboardingType) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(onboardingType.name(), true);
        editor.apply();
    }

    /**
     * Clear onboarding data.
     */
    public void clearData() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
