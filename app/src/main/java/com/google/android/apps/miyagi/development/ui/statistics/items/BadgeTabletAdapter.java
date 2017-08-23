package com.google.android.apps.miyagi.development.ui.statistics.items;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.statistics.Badge;
import com.google.android.apps.miyagi.development.data.models.statistics.BadgeGroupItem;
import com.google.android.apps.miyagi.development.data.models.statistics.IBadgeItem;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.BaseAdapter;
import com.google.android.apps.miyagi.development.ui.BaseViewHolder;
import com.google.android.apps.miyagi.development.ui.components.widget.BadgeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

/**
 * Created by Paweł on 2017-03-22.
 */

public class BadgeTabletAdapter extends BaseAdapter<IBadgeItem> {

    private RequestManager mRequestManager;

    public BadgeTabletAdapter(Context context, List<IBadgeItem> items) {
        super(context, items, null);
        mRequestManager = Glide.with(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case IBadgeItem.TYPE_ITEM:
                View itemView = mInflater.inflate(R.layout.item_statistics_badge, parent, false);
                return new ItemViewHolder(itemView);
            case IBadgeItem.TYPE_SECTION:
            default:
                View sectionView = mInflater.inflate(R.layout.expandable_header_item, parent, false);
                return new SectionViewHolder(sectionView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case IBadgeItem.TYPE_ITEM:
                ((ItemViewHolder) holder).bind(getItem(position));
                break;
            case IBadgeItem.TYPE_SECTION:
            default:
                ((SectionViewHolder) holder).bind(getItem(position));
                break;
        }
    }

    public class SectionViewHolder extends BaseViewHolder<IBadgeItem> {

        @BindView(R.id.image_expand_collapse) ImageView mExpandIcon;
        @BindView(R.id.label_title) TextView mSectionTitle;
        @BindView(R.id.divider_top) View mDividerTop;

        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mExpandIcon.setVisibility(View.INVISIBLE);
            mDividerTop.setVisibility(View.GONE);
        }

        @Override
        protected BaseAdapter<IBadgeItem> getAdapter() {
            return BadgeTabletAdapter.this;
        }

        @Override
        protected void bind(IBadgeItem item) {
            super.bind(item);
            BadgeGroupItem section = (BadgeGroupItem) item;
            mSectionTitle.setText(section.getSectionTitle());
            itemView.setBackgroundColor(section.getSectionBackgroundColor());
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
        }
    }

    public class ItemViewHolder extends BaseViewHolder<IBadgeItem> {

        @BindView(R.id.item_statistics_badge_image) BadgeView mBadgeImage;
        @BindView(R.id.item_statistics_badge_text) TextView mBadgeText;

        private Badge mBadge;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected BaseAdapter<IBadgeItem> getAdapter() {
            return BadgeTabletAdapter.this;
        }

        @Override
        protected void bind(IBadgeItem item) {
            super.bind(item);
            mBadge = (Badge) item;
            mBadgeText.setText(mBadge.getBadgeTitle());
            mRequestManager
                    .load(ImageUrlHelper.getUrlFor(mBadgeImage.getContext(), mBadge.getIcon()))
                    .into(mBadgeImage);

            mBadgeImage.setBackgroundColor(mBadge.getBadgeBackgroundColor());
            mBadgeImage.setAlpha(mBadge.isBadgeComplete() ? 1.0f : 0.2f);
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
        }
    }
}
