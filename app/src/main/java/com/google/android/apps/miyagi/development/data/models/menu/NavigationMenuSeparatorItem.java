package com.google.android.apps.miyagi.development.data.models.menu;

/**
 * Created by lukaszweglinski on 04.11.2016.
 */

public class NavigationMenuSeparatorItem extends BaseNavigationMenuItem {
    @Override
    public MenuType getType() {
        return MenuType.SEPARATOR;
    }
}
