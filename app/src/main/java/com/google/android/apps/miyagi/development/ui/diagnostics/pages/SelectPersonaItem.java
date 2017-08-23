package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import com.google.android.apps.miyagi.development.data.models.diagnostics.StepOneOptions;
import com.google.android.apps.miyagi.development.helpers.UndefinedSelectedState;

/**
 * Created by lukaszweglinski on 16.12.2016.
 */

class SelectPersonaItem {

    private final StepOneOptions mStepOneOptions;
    private UndefinedSelectedState mSelectState = UndefinedSelectedState.UNDEFINED;

    SelectPersonaItem(StepOneOptions stepOneOptions) {
        mStepOneOptions = stepOneOptions;
    }

    public void setState(boolean isSelected) {
        mSelectState = isSelected ? UndefinedSelectedState.SELECTED : UndefinedSelectedState.UNSELECTED;
    }

    UndefinedSelectedState getSelectState() {
        return mSelectState;
    }

    StepOneOptions getStepOneOptions() {
        return mStepOneOptions;
    }
}