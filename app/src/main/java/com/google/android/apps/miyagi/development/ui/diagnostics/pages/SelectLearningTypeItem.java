package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import com.google.android.apps.miyagi.development.helpers.UndefinedSelectedState;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.LearningType;

/**
 * Created by lukaszweglinski on 16.12.2016.
 */

public class SelectLearningTypeItem {

    private final LearningType mLearningType;
    private final String mTitleText;
    private final String mText;

    private UndefinedSelectedState mSelectState = UndefinedSelectedState.UNDEFINED;

    /**
     * Constructs list item for screen: select learning type.
     */
    public SelectLearningTypeItem(LearningType learningType, String titleText, String text) {
        mLearningType = learningType;
        mTitleText = titleText;
        mText = text;
    }

    public void setState(boolean isSelected) {
        mSelectState = isSelected ? UndefinedSelectedState.SELECTED : UndefinedSelectedState.UNSELECTED;
    }

    public UndefinedSelectedState getSelectState() {
        return mSelectState;
    }

    public LearningType getLearningType() {
        return mLearningType;
    }

    public String getTitleText() {
        return mTitleText;
    }

    public String getText() {
        return mText;
    }

}