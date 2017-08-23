package com.google.android.apps.miyagi.development.data.models.menu;

/**
 * Created by lukaszweglinski on 04.11.2016.
 */

public class NavigationMenuHeaderItem extends BaseNavigationMenuItem {

    private String mDisplayName;
    private String mAvatarUrl;

    /**
     * Constructor for testing purposes.
     *
     * @param displayName - name to display on header title.
     * @param avatarUrl   - url to avatar image.
     */

    public NavigationMenuHeaderItem(String displayName, String avatarUrl) {
        mDisplayName = displayName;
        mAvatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    @Override
    public MenuType getType() {
        return MenuType.HEADER;
    }
}
