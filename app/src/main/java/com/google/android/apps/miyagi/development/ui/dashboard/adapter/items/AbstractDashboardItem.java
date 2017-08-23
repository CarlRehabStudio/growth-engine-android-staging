package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

public abstract class AbstractDashboardItem<T extends FlexibleViewHolder>
        extends AbstractFlexibleItem<T> {

    static int DASHBOARD_ITEM_UNIQUE_ID = 0;
    int mId = ++DASHBOARD_ITEM_UNIQUE_ID;

    public AbstractDashboardItem() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractDashboardItem) {
            AbstractDashboardItem item = (AbstractDashboardItem) obj;
            return mId == item.mId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}