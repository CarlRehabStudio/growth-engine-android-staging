package com.google.android.apps.miyagi.development.data.models.lesson.practice;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class PracticeFeedback {

    public static final String ARG_KEY = "PRACTICE_FEEDBACK";

    @SerializedName("result_next_steps_title")
    protected String mResultNextStepsTitle;

    @SerializedName("result_header")
    protected String mResultHeader;

    @SerializedName("additional_links")
    protected List<AdditionalLink> mAdditionalLinks;

    @SerializedName("result_next_lesson")
    protected String mResultNextLesson;

    @SerializedName("result_try_again")
    protected String mResultTryAgain;

    @SerializedName("result_description_wrong")
    protected String mResultDescriptionWrong;

    @SerializedName("result_review_answer")
    protected String mResultReviewAnswer;

    @SerializedName("result_header_wrong")
    protected String mResultHeaderWrong;

    @SerializedName("result_watch_video")
    protected String mResultWatchVideo;

    @SerializedName("result_next_steps_description")
    protected String mResultNextStepsDescription;

    @SerializedName("additional_links_title")
    protected String mAdditionalLinksTitle;

    @SerializedName("result_description_almost")
    protected String mResultDescriptionAlmost;

    @SerializedName("result_header_right")
    protected String mResultHeaderRight;

    @SerializedName("result_header_almost")
    protected String mResultHeaderAlmost;

    @SerializedName("result_description_right")
    protected String mResultDescriptionRight;

    public List<AdditionalLink> getAdditionalLinks() {
        return mAdditionalLinks;
    }

    public String getAdditionalLinksTitle() {
        return mAdditionalLinksTitle;
    }

    public String getResultDescriptionAlmost() {
        return mResultDescriptionAlmost;
    }

    public String getResultDescriptionRight() {
        return mResultDescriptionRight;
    }

    public String getResultDescriptionWrong() {
        return mResultDescriptionWrong;
    }

    public String getResultHeader() {
        return mResultHeader;
    }

    public String getResultHeaderAlmost() {
        return mResultHeaderAlmost;
    }

    public String getResultHeaderRight() {
        return mResultHeaderRight;
    }

    public String getResultHeaderWrong() {
        return mResultHeaderWrong;
    }

    public String getResultNextLesson() {
        return mResultNextLesson;
    }

    public String getResultNextStepsDescription() {
        return mResultNextStepsDescription;
    }

    public String getResultNextStepsTitle() {
        return mResultNextStepsTitle;
    }

    public String getResultReviewAnswer() {
        return mResultReviewAnswer;
    }

    public String getResultTryAgain() {
        return mResultTryAgain;
    }

    public String getResultWatchVideo() {
        return mResultWatchVideo;
    }

}
