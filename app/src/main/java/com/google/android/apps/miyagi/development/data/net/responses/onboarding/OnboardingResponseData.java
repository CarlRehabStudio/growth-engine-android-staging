package com.google.android.apps.miyagi.development.data.net.responses.onboarding;

import com.google.android.apps.miyagi.development.data.models.onboarding.OnboardingStep;
import com.google.android.apps.miyagi.development.data.models.onboarding.PushStep;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

@Parcel
public class OnboardingResponseData {

    public static final String ONBOARDING_DATA_KEY = "ONBOARDING_DATA_KEY";

    @SerializedName("push_xsrf_token")
    protected String mPushXsrfToken;

    @SerializedName("skip_text")
    protected String mSkipText;

    @SerializedName("next_text")
    protected String mNextText;

    @SerializedName("push_step")
    protected PushStep mPushStep;

    @SerializedName("dashboard_steps")
    protected List<OnboardingStep> mDashboardStep;

    @SerializedName("lesson_steps")
    protected List<OnboardingStep> mLessonStep;

    public String getPushXsrfToken() {
        return mPushXsrfToken;
    }

    public String getSkipText() {
        return mSkipText;
    }

    public String getNextText() {
        return mNextText;
    }

    public PushStep getPushStep() {
        return mPushStep;
    }

    public List<OnboardingStep> getDashboardStep() {
        return mDashboardStep;
    }

    public List<OnboardingStep> getLessonStep() {
        return mLessonStep;
    }
}
