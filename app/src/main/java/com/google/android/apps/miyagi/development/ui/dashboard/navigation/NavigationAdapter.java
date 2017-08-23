package com.google.android.apps.miyagi.development.ui.dashboard.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.OnMenuItemSelectedListener;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.BaseNavigationMenuItemHolder;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.NavigationMenuFooterHolder;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.NavigationMenuHeaderHolder;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.NavigationMenuNormalHolder;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.NavigationMenuSeparatorHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public class NavigationAdapter extends RecyclerView.Adapter<BaseNavigationMenuItemHolder> {

    private final int mMainBackgroundColor;
    private final LayoutInflater mInflater;
    private final List<BaseNavigationMenuItem> mData = new ArrayList<>();

    @Nullable
    private OnMenuItemSelectedListener<? super BaseNavigationMenuItem> mOnItemSelectedExternalListener;

    private final OnMenuItemSelectedListener<? super BaseNavigationMenuItem> mOnItemSelectedInternalListener = new OnMenuItemSelectedListener<BaseNavigationMenuItem>() {
        @Override
        public void onItemSelected(int position, BaseNavigationMenuItem item) {
            if (mOnItemSelectedExternalListener != null) {
                mOnItemSelectedExternalListener.onItemSelected(position, item);
            }
        }
    };

    NavigationAdapter(Context context, int mainBackgroundColor) {
        mInflater = LayoutInflater.from(context);
        mMainBackgroundColor = mainBackgroundColor;
    }

    @Override
    public BaseNavigationMenuItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseNavigationMenuItem.MenuType.HEADER.getValue()) {
            return new NavigationMenuHeaderHolder(mInflater.inflate(R.layout.navigation_drawer_menu_item_header, parent, false), mOnItemSelectedInternalListener,mMainBackgroundColor);
        } else if (viewType == BaseNavigationMenuItem.MenuType.FOOTER.getValue()) {
            return new NavigationMenuFooterHolder(mInflater.inflate(R.layout.navigation_drawer_menu_item_footer, parent, false), mOnItemSelectedInternalListener);
        } else if (viewType == BaseNavigationMenuItem.MenuType.SEPARATOR.getValue()) {
            return new NavigationMenuSeparatorHolder(mInflater.inflate(R.layout.navigation_drawer_menu_item_separator, parent, false));
        } else {
            return new NavigationMenuNormalHolder(mInflater.inflate(R.layout.navigation_drawer_menu_item_normal, parent, false), mOnItemSelectedInternalListener);
        }
    }

    @Override
    public void onBindViewHolder(BaseNavigationMenuItemHolder holder, int position) {
        holder.populateWithData(getItem(position));
    }

    private BaseNavigationMenuItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseNavigationMenuItem item = mData.get(position);
        return item.getType().getValue();
    }

    /**
     * Sets data to adapter.
     *
     * @param data - array of data. Can be null.
     */
    public void setData(List<BaseNavigationMenuItem> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnMenuItemSelectedListener<? super BaseNavigationMenuItem> onItemSelectedListener) {
        mOnItemSelectedExternalListener = onItemSelectedListener;
    }
}
