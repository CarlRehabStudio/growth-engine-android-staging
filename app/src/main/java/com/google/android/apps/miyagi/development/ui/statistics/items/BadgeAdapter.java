package com.google.android.apps.miyagi.development.ui.statistics.items;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.statistics.Badge;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.components.widget.BadgeView;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

/**
 * Created by lukaszweglinski on 28.11.2016.
 */

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<Badge> mItems;
    private RequestManager mRequestManager;
    @Nullable
    private OnItemSelectedListener<Badge> mOnItemSelectedExternalListener;
    private OnItemSelectedListener<Badge> mOnItemSelectedInternalListener;

    /**
     * Instantiates a new Badge adapter.
     *
     * @param context context.
     * @param items   badge items.
     */
    public BadgeAdapter(Context context, List<Badge> items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mRequestManager = Glide.with(context);
    }

    public void updateItemsList(List<Badge> items) {
        mItems = items;
    }

    @Override
    public BadgeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_statistics_badge, parent, false);
        return new ViewHolder(view, mOnItemSelectedInternalListener);
    }

    @Override
    public void onBindViewHolder(BadgeAdapter.ViewHolder holder, int position) {
        holder.populateWithData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Sets listener of selected item.
     */
    public void setOnItemSelectedListener(OnItemSelectedListener<Badge> onItemSelectedListener) {
        mOnItemSelectedExternalListener = onItemSelectedListener;
        mOnItemSelectedInternalListener = item -> {
            if (mOnItemSelectedExternalListener != null) {
                mOnItemSelectedExternalListener.onItemSelected(item);
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final OnItemSelectedListener<Badge> mOnItemSelectedListener;
        private BadgeView mBadgeImage;
        private TextView mBadgeText;
        private Badge mBadge;

        ViewHolder(View itemView, OnItemSelectedListener<Badge> onItemSelectedListener) {
            super(itemView);
            mOnItemSelectedListener = onItemSelectedListener;
            if (mOnItemSelectedListener != null) {
                itemView.setOnClickListener(view -> mOnItemSelectedListener.onItemSelected(mBadge));
            }
            mBadgeImage = (BadgeView) itemView.findViewById(R.id.item_statistics_badge_image);
            mBadgeText = (TextView) itemView.findViewById(R.id.item_statistics_badge_text);
        }

        void populateWithData(Badge badge) {
            mBadge = badge;
            mBadgeText.setText(mBadge.getBadgeTitle());
            mRequestManager
                    .load(ImageUrlHelper.getUrlFor(mBadgeImage.getContext(), mBadge.getIcon()))
                    .into(mBadgeImage);

            mBadgeImage.setBackgroundColor(mBadge.getBadgeBackgroundColor());
            mBadgeImage.setAlpha(badge.isBadgeComplete() ? 1.0f : 0.2f);
        }
    }
}
