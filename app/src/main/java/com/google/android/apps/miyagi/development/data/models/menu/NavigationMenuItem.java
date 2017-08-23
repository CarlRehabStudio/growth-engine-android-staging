package com.google.android.apps.miyagi.development.data.models.menu;

import android.support.annotation.Nullable;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

@Parcel
public class NavigationMenuItem extends BaseNavigationMenuItem {

    protected String mDisplayName;
    protected String mContentUrl;
    protected String mSectionIconUrl;
    protected String mSectionIconAlt;

    public NavigationMenuItem() {
    }

    /**
     * @param displayName    - name to display on list.
     * @param contentUrl     - web service url related with this item.
     * @param sectionIconUrl - url to icon image.
     * @param sectionIconAlt - description to image.
     */
    public NavigationMenuItem(String displayName, @Nullable String contentUrl, String sectionIconUrl, String sectionIconAlt, MenuType action) {
        mDisplayName = displayName;
        mContentUrl = contentUrl;
        mSectionIconUrl = sectionIconUrl;
        mSectionIconAlt = sectionIconAlt;
        mAction = action;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getContentUrl() {
        return mContentUrl;
    }

    public String getSectionIconUrl() {
        return mSectionIconUrl;
    }

    public String getSectionIconAlt() {
        return mSectionIconAlt;
    }

    public MenuType getAction() {
        return mAction;
    }
}