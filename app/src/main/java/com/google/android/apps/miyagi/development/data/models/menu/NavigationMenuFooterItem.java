package com.google.android.apps.miyagi.development.data.models.menu;

import com.google.android.apps.miyagi.development.data.models.commondata.menu.Legal;

/**
 * Created by lukaszweglinski on 04.11.2016.
 */

public class NavigationMenuFooterItem extends BaseNavigationMenuItem {

    private final Legal mLegal;

    /**
     * Constructor for testing purposes.
     *
     * @param legal - Legal menu item.
     */

    public NavigationMenuFooterItem(Legal legal ) {
        mLegal = legal;
    }

    @Override
    public MenuType getType() {
        return MenuType.FOOTER;
    }

    public Legal getLegal() {
        return mLegal;
    }
}
