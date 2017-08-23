package com.google.android.apps.miyagi.development.data.models.menu;

/**
 * Created by lukaszweglinski on 04.11.2016.
 */

public abstract class BaseNavigationMenuItem {

    protected MenuType mAction;

    public MenuType getType() {
        return mAction;
    }

    public enum MenuType {
        NONE(0), HEADER(1), DASHBOARD(2), OFFLINE_DASHBOARD(3), WEB(4), PROFILE(5), SIGNOUT(6), FOOTER(7), SEPARATOR(8);

        int mValue;

        MenuType(int i) {
            mValue = i;
        }

        public int getValue() {
            return mValue;
        }
    }
}
