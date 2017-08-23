package com.google.android.apps.miyagi.development.ui.assessment.exam;


/**
 * Created by Paweł on 2017-03-06.
 */

public class ExamItem {

    private String mTestTitle;

    private String mTestChar;

    private boolean mIsSelected;

    private boolean mIsAnswered;

    private boolean mIsAnsweredCorrectly;

    private ExamItem(Builder builder) {
        setTestTitle(builder.mTestTitle);
        setTestChar(builder.mTestChar);
        setSelected(builder.mIsSelected);
        setAnswered(builder.mIsAnswered);
        setAnsweredCorrectly(builder.mIsAnsweredCorrectly);
    }

    public String getTestTitle() {
        return mTestTitle;
    }

    public void setTestTitle(String testTitle) {
        this.mTestTitle = testTitle;
    }

    public String getTestChar() {
        return mTestChar;
    }

    public void setTestChar(String testChar) {
        this.mTestChar = testChar;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isAnswered() {
        return mIsAnswered;
    }

    public void setAnswered(boolean answered) {
        mIsAnswered = answered;
    }

    public boolean isAnsweredCorrectly() {
        return mIsAnsweredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        mIsAnsweredCorrectly = answeredCorrectly;
    }


    public static final class Builder {
        private String mTestTitle;
        private String mTestChar;
        private boolean mIsSelected;
        private boolean mIsAnswered;
        private boolean mIsAnsweredCorrectly;

        public Builder() {
        }

        public Builder withTestTitle(String val) {
            mTestTitle = val;
            return this;
        }

        public Builder withTestChar(String val) {
            mTestChar = val;
            return this;
        }

        public Builder withIsSelected(boolean val) {
            mIsSelected = val;
            return this;
        }

        public Builder withIsAnswered(boolean val) {
            mIsAnswered = val;
            return this;
        }

        public Builder withIsAnsweredCorrectly(boolean val) {
            mIsAnsweredCorrectly = val;
            return this;
        }

        public ExamItem build() {
            return new ExamItem(this);
        }
    }
}
