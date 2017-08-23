package com.google.android.apps.miyagi.development.data.models.statistics;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

public enum StatisticsPageType {
    PLAN(0, 0xFFFF3F80, "plan"), CERTIFICATION(1, 0xFFFFC400, "certification");

    private int mPosition;
    private int mColor;
    private String mType;

    StatisticsPageType(int position, int color, String type) {
        mPosition = position;
        mColor = color;
        mType = type;
    }

    /**
     * Gets type from postion.
     *
     * @param position the position of item.
     * @return the type based on element position.
     */
    public static StatisticsPageType getTypeFromPosition(int position) {
        if (position == PLAN.getPosition()) {
            return PLAN;
        } else {
            return CERTIFICATION;
        }
    }

    public int getPosition() {
        return mPosition;
    }

    public int getColor() {
        return mColor;
    }

    public String getType() {
        return mType;
    }
}
