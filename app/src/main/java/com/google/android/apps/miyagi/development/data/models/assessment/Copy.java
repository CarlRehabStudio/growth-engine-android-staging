package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcin on 29.12.2016.
 */

@Parcel
public class Copy {

    @SerializedName("topic_color")
    protected String mTopicColorString;

    @SerializedName("passed_topic")
    protected CopyItem mPassedTopic;

    @SerializedName("failed")
    protected CopyItem mFailed;

    @SerializedName("failed_no_attempts")
    protected CopyItem mFailedNoAttempts;

    @SerializedName("passed_cert")
    protected CopyItem mPassedCert;

    @SerializedName("instructions")
    protected CopyInstructions mInstructions;

    @SerializedName("social")
    protected CopySocial mCopySocial;

    public int getTopicColor() {
        return ColorHelper.parseColor(mTopicColorString);
    }

    public CopyItem getPassedTopic() {
        return mPassedTopic;
    }

    public CopyItem getFailed() {
        return mFailed;
    }

    public CopyItem getFailedNoAttempts() {
        return mFailedNoAttempts;
    }

    public CopyInstructions getInstructions() {
        return mInstructions;
    }

    public CopyItem getPassedCert() {
        return mPassedCert;
    }

    public CopySocial getCopySocial() {
        return mCopySocial;
    }
}
