package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuFooterItem;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.OnMenuItemSelectedListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public final class NavigationMenuFooterHolder extends BaseNavigationMenuItemHolder {

    private final RequestManager mRequestManager;
    private final TextView mFooterTitle;
    private final TextView mGoogleTitle;
    private final ImageView mFooterImage;

    private NavigationMenuFooterItem mMenuItem;

    /**
     * Constructs NavigationMenuFooterHolder object.
     */
    public NavigationMenuFooterHolder(View itemView, OnMenuItemSelectedListener<? super BaseNavigationMenuItem> onItemSelectedListener) {
        super(itemView);
        mFooterTitle = (TextView) itemView.findViewById(R.id.nav_footer_title);
        mGoogleTitle = (TextView) itemView.findViewById(R.id.nav_footer_google_text);
        mFooterImage = (ImageView) itemView.findViewById(R.id.nav_footer_image);
        mRequestManager = Glide.with(itemView.getContext());
        mOnItemSelectedListener = onItemSelectedListener;
        itemView.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(getAdapterPosition(), mMenuItem));
    }

    @Override
    public void populateWithData(BaseNavigationMenuItem item) {
        mMenuItem = (NavigationMenuFooterItem) item;
        if (mMenuItem != null) {
            mGoogleTitle.setText(mMenuItem.getLegal().getGoogleText());
            mFooterTitle.setText(mMenuItem.getLegal().getTitle());
            mFooterImage.setContentDescription(mMenuItem.getLegal().getGoogleImageAltText());
            mRequestManager.load(mMenuItem.getLegal().getGoogleImageUrl()).into(mFooterImage);
        }
    }
}
