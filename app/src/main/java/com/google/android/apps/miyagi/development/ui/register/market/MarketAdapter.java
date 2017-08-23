package com.google.android.apps.miyagi.development.ui.register.market;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.BaseAdapter;
import com.google.android.apps.miyagi.development.ui.BaseViewHolder;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

/**
 * Created by Paweł on 2017-03-06.
 */

public class MarketAdapter extends BaseAdapter<Market> {

    private final RequestManager mRequestManager;

    public MarketAdapter(Context context, List<Market> items, OnItemSelectedListener<Market> listener) {
        super(context, items, listener);
        mRequestManager = Glide.with(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.register_market_selection_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position));
    }

    public class ViewHolder extends BaseViewHolder<Market> {

        @BindView(R.id.register_choose_language_item_label) TextView mMarketTitle;
        @BindView(R.id.register_choose_language_item_image) ImageView mMarketImage;
        @BindView(R.id.divider_top) View mDividerTop;
        @BindView(R.id.divider_bottom) View mDividerBottom;

        private Market mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                mOnItemClickListener.onItemSelected(mItem);
            });
        }

        @Override
        protected BaseAdapter<Market> getAdapter() {
            return MarketAdapter.this;
        }

        @Override
        protected void bind(Market item) {
            super.bind(item);
            this.mItem = item;

            bindTexts(item);
            bindDividers();

        }

        private void bindTexts(Market item) {
            mMarketTitle.setText(item.getDisplayName());
            // problem with content, smaller flags are not square images, use XXXHDPI images
            mRequestManager.load(item.getFlag().getXxxhdpi())
                    .placeholder(R.drawable.ic_no_flag)
                    .fitCenter()
                    .into(mMarketImage);
        }

        private void bindDividers() {
            if (getAdapterPosition() == getItemCount() - 1) {
                mDividerBottom.setVisibility(View.VISIBLE);
            } else {
                mDividerBottom.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onAnimated() {
            ViewCompat.setAlpha(itemView, 0.0f);
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 0.0f, 1.0f);
            ObjectAnimator slideInFromBottomAnimator = ObjectAnimator.ofFloat(itemView, "translationY", 300f, 0.0f);

            int animationDuration = itemView.getContext().getResources().getInteger(R.integer.animation_duration);
            alphaAnimator.setDuration(animationDuration);
            slideInFromBottomAnimator.setDuration(animationDuration);

            alphaAnimator.setInterpolator(new AccelerateInterpolator(2f));
            slideInFromBottomAnimator.setInterpolator(new DecelerateInterpolator());

            animators.add(alphaAnimator);
            animators.add(slideInFromBottomAnimator);
        }

    }

}
