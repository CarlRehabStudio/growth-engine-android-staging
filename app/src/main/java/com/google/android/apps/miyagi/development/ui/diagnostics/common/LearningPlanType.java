package com.google.android.apps.miyagi.development.ui.diagnostics.common;

import android.support.annotation.DrawableRes;
import com.google.android.apps.miyagi.development.R;

/**
 * Created by lukaszweglinski on 16.12.2016.
 */

public enum LearningPlanType {

    GET_CERTIFIED(R.drawable.ilu_diagnostic_2_1, 0xFF84C052, "get_certified"),
    PRE_BUSSINES(R.drawable.ilu_diagnostic_2_1, 0xFF84C052, "pre_business"),
    IN_BUSSINES(R.drawable.ilu_diagnostic_2_2, 0xFF4687E9, "in_business"),
    NO_BUSSINES(R.drawable.ilu_diagnostic_2_3, 0xFFF6C443, "no_business");

    @DrawableRes
    private final int mDrawable;
    private final int mColor;
    private final String mType;

    LearningPlanType(int drawable, int color, String type) {
        mDrawable = drawable;
        mColor = color;
        mType = type;
    }

    /**
     * Return LearningPlanType enum from string id.
     *
     * @param type string value of type.
     * @return the learning plan type.
     */
    public static LearningPlanType fromId(String type) {
        if (GET_CERTIFIED.getType().equals(type)) {
            return LearningPlanType.GET_CERTIFIED;
        } else if (PRE_BUSSINES.getType().equals(type)) {
            return LearningPlanType.PRE_BUSSINES;
        } else if (IN_BUSSINES.getType().equals(type)) {
            return LearningPlanType.IN_BUSSINES;
        } else if (NO_BUSSINES.getType().equals(type)) {
            return LearningPlanType.NO_BUSSINES;
        }
        return null;
    }

    @DrawableRes
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
