package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.OnMenuItemSelectedListener;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public abstract class BaseNavigationMenuItemHolder extends RecyclerView.ViewHolder {

    protected OnMenuItemSelectedListener<? super BaseNavigationMenuItem> mOnItemSelectedListener;

    public BaseNavigationMenuItemHolder(View itemView) {
        super(itemView);
    }

    public abstract void populateWithData(BaseNavigationMenuItem item);
}
