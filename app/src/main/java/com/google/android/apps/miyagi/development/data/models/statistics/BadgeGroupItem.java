package com.google.android.apps.miyagi.development.data.models.statistics;

/**
 * Created by Paweł on 2017-03-22.
 */

public class BadgeGroupItem implements IBadgeItem {

    private final String mSectionTitle;
    private final int mSectionBackgroundColor;

    public BadgeGroupItem(String sectionHeaderTitle, int sectionBackgroundColor) {
        mSectionTitle = sectionHeaderTitle;
        mSectionBackgroundColor = sectionBackgroundColor;
    }

    public String getSectionTitle() {
        return mSectionTitle;
    }

    public int getSectionBackgroundColor() {
        return mSectionBackgroundColor;
    }

    @Override
    public int getType() {
        return TYPE_SECTION;
    }

}
