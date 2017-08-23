package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuItem;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.OnMenuItemSelectedListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;


/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public final class NavigationMenuNormalHolder extends BaseNavigationMenuItemHolder {

    private static final int mSelectedColor = 0xFF4054B2;
    private static final int mUnSelectedColor = 0xFF757575;

    private final RequestManager mRequestManager;
    private final TextView mText;
    private final ImageView mIcon;
    private NavigationMenuItem mMenuItem;

    /**
     * @param itemView               - holder view.
     * @param onItemSelectedListener - menu item select listener.
     */
    public NavigationMenuNormalHolder(View itemView, OnMenuItemSelectedListener<? super BaseNavigationMenuItem> onItemSelectedListener) {
        super(itemView);
        mIcon = (ImageView) itemView.findViewById(R.id.nav_item_icon);
        mText = (TextView) itemView.findViewById(R.id.nav_item_text);
        mRequestManager = Glide.with(itemView.getContext());
        mOnItemSelectedListener = onItemSelectedListener;
        itemView.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(getAdapterPosition(), mMenuItem));
    }

    @Override
    public void populateWithData(BaseNavigationMenuItem item) {
        mMenuItem = (NavigationMenuItem) item;

        mText.setTextColor(Color.BLACK);
        mIcon.setColorFilter(mUnSelectedColor);

        if (mMenuItem == null) {
            mText.setText(null);
        } else {
            mText.setText(mMenuItem.getDisplayName());
            mRequestManager.load(mMenuItem.getSectionIconUrl()).into(mIcon);
            mIcon.setContentDescription(mMenuItem.getSectionIconAlt());
            if (mMenuItem.getType() == BaseNavigationMenuItem.MenuType.DASHBOARD) {
                mText.setTextColor(mSelectedColor);
                mIcon.setColorFilter(mSelectedColor);
            }
        }
    }
}
