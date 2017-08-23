package com.google.android.apps.miyagi.development.data.net.responses.assessment;

import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.ExamPassedAction;
import com.google.android.apps.miyagi.development.data.models.assessment.Question;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class AssessmentResponseData {

    @SerializedName("assessment_xsrf_token")
    protected String mXsrfToken;

    @SerializedName("copy")
    protected Copy mCopy;

    @SerializedName("questions")
    protected List<Question> mQuestions;

    @SerializedName("action")
    protected ExamPassedAction mExamPassedAction;
    
    @SerializedName("attempts_left")
    protected int mAttemptsLeft;

    public int getAttemptsLeft() {
        return mAttemptsLeft;
    }

    public String getXsrfToken() {
        return mXsrfToken;
    }

    public Copy getCopy() {
        return mCopy;
    }

    public List<Question> getQuestions() {
        return mQuestions;
    }

    public ExamPassedAction getExamPassedAction() {
        return mExamPassedAction;
    }

}
