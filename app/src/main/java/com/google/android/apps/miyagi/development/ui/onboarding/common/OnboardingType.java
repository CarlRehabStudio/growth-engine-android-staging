package com.google.android.apps.miyagi.development.ui.onboarding.common;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

@Parcel
public enum OnboardingType {
    PUSH, DASHBOARD, LESSON;

    public static final String ARG_KEY = OnboardingType.class.getCanonicalName();
}
