package com.google.android.apps.miyagi.development.data.models.lesson.practice;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.clock.ClockPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.strikethrough.StrikeThroughPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext.SwitchesTextPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud.TagCloudPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter.TwitterPracticeDetails;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class Practice {

    public interface Type {
        String IMAGE_SLIDER = "image_slider";
        String BOOLEAN_SELECTOR = "boolean_selector";
        String REORDER = "reorder";
        String SWIPE_SELECTOR = "swipe_selector";
        String TAG_CLOUD = "tag_cloud";
        String SELECT_LARGE = "select_large";
        String SELECT_RIGHT = "select_right";
        String STRIKE_THROUGH = "strike_through";
        String SWITCHES_TEXT = "switches_text";
        String TEXT_DRAWER = "text_drawer";
        String TWITTER_DRAGANDDROP = "twitter_draganddrop";
    }

    public static final String ARG_DATA = "PRACTICE_DATA";

    @SerializedName("activity_type")
    protected String mActivityType;

    @SerializedName("instruction_start_button_text")
    protected String mInstructionStartButtonText;

    @SerializedName("instruction_next_button_text")
    protected String mInstructionNextButtonText;

    @SerializedName("instruction_submit_button_text")
    protected String mInstructionSubmitButtonText;

    @SerializedName("instruction_image_url")
    protected ImagesBaseModel mInstructionImageUrl;

    @SerializedName("instruction_image_alt")
    protected String mInstructionImageAlt;

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("instruction_header_text")
    protected String mInstructionHeaderText;

    @SerializedName("instruction_title_text")
    protected String mInstructionTitleText;

    @SerializedName("instruction_description_text")
    protected String mInstructionDescriptionText;

    @SerializedName("instruction_question_text")
    protected String mInstructionQuestionText;

    @SerializedName("instruction_dialog_title")
    protected String mInstructionDialogTitle;

    @SerializedName("instruction_dialog_text")
    protected String mInstructionDialogText;

    @SerializedName("instruction_dialog_ok")
    protected String mInstructionDialogOk;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("submit_snackbar_text")
    protected String mSubmitSnackbarText;

    //............................................................................. Practice Details
    @SerializedName("select_right")
    protected SelectRightPracticeDetails mSelectRightPracticeDetails;

    @SerializedName("swipe_selector")
    protected SwipePracticeDetails mSwipePracticeDetails;

    @SerializedName("switches_text")
    protected SwitchesTextPracticeDetails mSwitchesTextPracticeDetails;

    @SerializedName("text_drawer")
    protected FortuneWheelPracticeDetails mFortuneWheelPracticeDetails;

    @SerializedName("boolean_selector")
    protected BooleanSelectorPracticeDetails mBooleanPracticeDetails;

    @SerializedName("strike_through")
    protected StrikeThroughPracticeDetails mStrikeThroughPracticeDetails;

    @SerializedName("twitter_draganddrop")
    protected TwitterPracticeDetails mTwitterPracticeDetails;

    @SerializedName("reorder")
    protected ReorderPracticeDetails mReorderPracticeDetails;

    @SerializedName("select_large")
    protected SelectLargePracticeDetails mSelectLargePracticeDetails;

    @SerializedName("tag_cloud")
    protected TagCloudPracticeDetails mTagCloudPracticeDetails;

    @SerializedName("image_slider")
    protected ClockPracticeDetails mClockPracticeDetails;

    public String getActivityType() {
        return mActivityType;
    }

    public String getInstructionNextButtonText() {
        return mInstructionNextButtonText;
    }

    public String getInstructionSubmitButtonText() {
        return mInstructionSubmitButtonText;
    }

    public String getSubmitSnackbarText() {
        return mSubmitSnackbarText;
    }

    public ImagesBaseModel getInstructionImageUrl() {
        return mInstructionImageUrl;
    }

    public String getInstructionImageAlt() {
        return mInstructionImageAlt;
    }

    public String getInstructionHeaderText() {
        return mInstructionHeaderText;
    }

    public String getInstructionTitleText() {
        return mInstructionTitleText;
    }

    public String getInstructionDescriptionText() {
        return mInstructionDescriptionText;
    }

    public String getInstructionQuestionText() {
        return mInstructionQuestionText;
    }

    public String getInstructionStartButtonText() {
        return mInstructionStartButtonText;
    }

    public String getInstructionDialogTitle() {
        return mInstructionDialogTitle;
    }

    public String getInstructionDialogText() {
        return mInstructionDialogText;
    }

    public String getInstructionDialogOk() {
        return mInstructionDialogOk;
    }

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    //............................................................................. Practice Details
    public SwipePracticeDetails getSwipePracticeDetails() {
        return mSwipePracticeDetails;
    }

    public FortuneWheelPracticeDetails getFortuneWheelPracticeDetails() {
        return mFortuneWheelPracticeDetails;
    }

    public ClockPracticeDetails getClockPracticeDetails() {
        return mClockPracticeDetails;
    }

    public TwitterPracticeDetails getTwitterPracticeDetails() {
        return mTwitterPracticeDetails;
    }

    public TagCloudPracticeDetails getTagCloudPracticeDetails() {
        return mTagCloudPracticeDetails;
    }

    public SwitchesTextPracticeDetails getSwitchesTextPracticeDetails() {
        return mSwitchesTextPracticeDetails;
    }

    public SelectLargePracticeDetails getSelectLargePracticeDetails() {
        return mSelectLargePracticeDetails;
    }

    public SelectRightPracticeDetails getSelectRightPracticeDetails() {
        return mSelectRightPracticeDetails;
    }

    public BooleanSelectorPracticeDetails getBooleanPracticeDetails() {
        return mBooleanPracticeDetails;
    }

    public StrikeThroughPracticeDetails getStrikeThroughPracticeDetails() {
        return mStrikeThroughPracticeDetails;
    }

    public ReorderPracticeDetails getReorderPracticeDetails() {
        return mReorderPracticeDetails;
    }
}
