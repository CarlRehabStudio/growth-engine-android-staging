package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuHeaderItem;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.OnMenuItemSelectedListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public final class NavigationMenuHeaderHolder extends BaseNavigationMenuItemHolder {

    private final RequestManager mRequestManager;
    private final TextView mHeaderText;
    private final ImageView mHeaderImage;
    private final int mMainBackgroundColor;

    private NavigationMenuHeaderItem mMenuItem;

    /**
     * Creates navigation menu header holder.
     */
    public NavigationMenuHeaderHolder(View itemView, OnMenuItemSelectedListener<? super BaseNavigationMenuItem> onItemSelectedListener, int mainBackgroundColor) {
        super(itemView);
        mMainBackgroundColor = mainBackgroundColor;
        itemView.setBackgroundColor(mMainBackgroundColor);
        mHeaderImage = (ImageView) itemView.findViewById(R.id.nav_header_avatar);
        mHeaderText = (TextView) itemView.findViewById(R.id.nav_header_title);
        mRequestManager = Glide.with(itemView.getContext());
        mOnItemSelectedListener = onItemSelectedListener;
        itemView.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(getAdapterPosition(), mMenuItem));
    }

    @Override
    public void populateWithData(BaseNavigationMenuItem item) {
        mMenuItem = (NavigationMenuHeaderItem) item;
        if (mMenuItem == null) {
            mHeaderText.setText(null);
        } else {
            mHeaderText.setText(mMenuItem.getDisplayName());
            mRequestManager.load(mMenuItem.getAvatarUrl()).into(mHeaderImage);
        }
    }
}
