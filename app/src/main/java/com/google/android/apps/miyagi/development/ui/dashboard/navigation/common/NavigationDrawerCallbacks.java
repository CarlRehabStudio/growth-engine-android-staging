package com.google.android.apps.miyagi.development.ui.dashboard.navigation.common;

import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public interface NavigationDrawerCallbacks {
    NavigationDrawerCallbacks NULL = new NavigationDrawerCallbacks() {
        @Override
        public void onNavigationDrawerItemSelected(int position, BaseNavigationMenuItem item) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change menu item to other.");
        }
    };

    void onNavigationDrawerItemSelected(int position, BaseNavigationMenuItem item);
}