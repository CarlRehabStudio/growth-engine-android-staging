package com.google.android.apps.miyagi.development.ui.assessment.exam;

import com.google.android.apps.miyagi.development.data.models.assessment.Question;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 30.12.2016.
 */

@Parcel
public class QuestionModel {

    protected String mInstanceId;
    protected String mIndexLabel;
    protected String mQuestion;
    protected boolean mCorrect;
    protected boolean mResultMode;
    protected List<String> mChoices;
    protected int mUserChoiceIndex = -1;
    protected boolean mUserAnswered;

    public QuestionModel() {

    }

    /**
     * Constructs new question model.
     */
    public QuestionModel(Question question, boolean resultMode) {
        mInstanceId = question.getInstanceId();
        mIndexLabel = question.getIndexLabel();
        mQuestion = question.getQuestion();
        mCorrect = question.isCorrect();
        mChoices = question.getChoices();
        mResultMode = resultMode;
    }

    public List<String> getChoices() {
        return mChoices;
    }

    public String getIndexLabel() {
        return mIndexLabel;
    }

    public String getInstanceId() {
        return mInstanceId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public boolean isCorrect() {
        return mCorrect;
    }

    public int getUserChoiceIndex() {
        return mUserChoiceIndex;
    }

    public boolean isResultMode() {
        return mResultMode;
    }

    public void setUserChoiceIndex(int index) {
        mUserChoiceIndex = index;
    }

    public boolean isUserAnswered() {
        return mUserAnswered;
    }

    public void setUserAnswered(boolean userAnswered) {
        mUserAnswered = userAnswered;
        mResultMode = false;
    }
}
