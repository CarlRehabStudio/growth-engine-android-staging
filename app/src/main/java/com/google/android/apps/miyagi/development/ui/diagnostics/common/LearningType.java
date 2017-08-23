package com.google.android.apps.miyagi.development.ui.diagnostics.common;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by lukaszweglinski on 16.12.2016.
 */

public enum LearningType {

    CERTIFICATION(R.drawable.ilu_diagnostic_1_1, 0xFF57BEF9, "certification"),
    PLAN(R.drawable.ilu_diagnostic_1_2, 0xFF8AC24A, "plan");

    protected final int mDrawable;
    protected final int mColor;
    protected final String mType;

    LearningType(int drawable, int color, String type) {
        mDrawable = drawable;
        mColor = color;
        mType = type;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public int getColor() {
        return mColor;
    }

    public String getType() {
        return mType;
    }
}
