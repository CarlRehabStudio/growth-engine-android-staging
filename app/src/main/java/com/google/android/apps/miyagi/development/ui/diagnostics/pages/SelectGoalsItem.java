package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import com.google.android.apps.miyagi.development.data.models.diagnostics.StepThreeOptions;

/**
 * Created by lukaszweglinski on 16.12.2016.
 */

public class SelectGoalsItem {

    private final StepThreeOptions mStepThreeOptions;
    private boolean mIsSelected;
    private boolean mIsIdle;

    public SelectGoalsItem(StepThreeOptions stepThreeOptions) {
        mStepThreeOptions = stepThreeOptions;
    }

    public StepThreeOptions getStepThreeOptions() {
        return mStepThreeOptions;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isIdle() {
        return mIsIdle;
    }

    public void setIdle(boolean idle) {
        mIsIdle = idle;
    }
}