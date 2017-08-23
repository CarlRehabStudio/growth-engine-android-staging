package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcin on 29.12.2016.
 */

@Parcel
public class CopyInstructions {

    @SerializedName("image_background_color")
    protected String mImageBackgroundColorString;

    @SerializedName("instructions_text")
    protected String mInstructionsText;

    @SerializedName("dialog_confirm")
    protected String mDialogOk;

    @SerializedName("image_url")
    protected ImagesBaseModel mImageUrl;

    @SerializedName("toolbar_text")
    protected String mToolbarText;

    @SerializedName("start_cta")
    protected String mStartCta;

    @SerializedName("dialog_title")
    protected String mDialogTitle;

    @SerializedName("next_question_cta")
    protected String mNextQuestionCta;

    @SerializedName("attempts_left")
    protected int mAttemptsLeft;

    @SerializedName("instructions_title")
    protected String mInstructionsTitle;

    @SerializedName("topic_title")
    protected String mTopicTitle;

    @SerializedName("submit_cta")
    protected String mSubmitCta;

    public int getImageBackgroundColor() {
        return ColorHelper.parseColor(mImageBackgroundColorString);
    }

    public String getInstructionsText() {
        return mInstructionsText;
    }

    public String getDialogOk() {
        return mDialogOk;
    }

    public ImagesBaseModel getImageUrl() {
        return mImageUrl;
    }

    public String getToolbarText() {
        return mToolbarText;
    }

    public String getStartCta() {
        return mStartCta;
    }

    public String getDialogTitle() {
        return mDialogTitle;
    }

    public String getNextQuestionCta() {
        return mNextQuestionCta;
    }

    public int getAttemptsLeft() {
        return mAttemptsLeft;
    }

    public String getInstructionsTitle() {
        return mInstructionsTitle;
    }

    public String getTopicTitle() {
        return mTopicTitle;
    }

    public String getSubmitCta() {
        return mSubmitCta;
    }
}
