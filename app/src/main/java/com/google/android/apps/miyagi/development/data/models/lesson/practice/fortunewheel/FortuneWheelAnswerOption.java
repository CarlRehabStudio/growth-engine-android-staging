package com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 16.11.2016.
 */
@Parcel
public class FortuneWheelAnswerOption {

    @SerializedName("id")
    protected String mId;

    @SerializedName("answer")
    protected String mAnswer;

    @SerializedName("option_color")
    protected String mOptionColorString;

    @SerializedName("label")
    protected String mLabel;

    public String getAnswer() {
        return mAnswer;
    }

    public String getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getOptionColor() {
        return ColorHelper.parseColor(mOptionColorString);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FortuneWheelAnswerOption that = (FortuneWheelAnswerOption) other;

        if (mId != null ? !mId.equals(that.mId) : that.mId != null) {
            return false;
        }

        if (mAnswer != null ? !mAnswer.equals(that.mAnswer) : that.mAnswer != null) {
            return false;
        }

        if (mOptionColorString != null ? !mOptionColorString.equals(that.mOptionColorString)
                : that.mOptionColorString != null) {
            return false;
        }

        return mLabel != null ? mLabel.equals(that.mLabel) : that.mLabel == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mAnswer != null ? mAnswer.hashCode() : 0);
        result = 31 * result + (mOptionColorString != null ? mOptionColorString.hashCode() : 0);
        result = 31 * result + (mLabel != null ? mLabel.hashCode() : 0);
        return result;
    }
}
