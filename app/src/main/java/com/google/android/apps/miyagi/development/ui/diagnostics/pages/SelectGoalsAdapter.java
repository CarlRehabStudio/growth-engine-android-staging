package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.ui.BaseAdapter;
import com.google.android.apps.miyagi.development.ui.BaseViewHolder;
import com.google.android.apps.miyagi.development.ui.components.widget.ColorizeAnimator;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;

/**
 * Created by Paweł on 2017-03-06.
 */

class SelectGoalsAdapter extends BaseAdapter<SelectGoalsItem> {

    SelectGoalsAdapter(Context context, List<SelectGoalsItem> items, OnItemSelectedListener<SelectGoalsItem> listener) {
        super(context, items, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diagnostics_select_goals_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position));
    }

    public class ViewHolder extends BaseViewHolder<SelectGoalsItem> {

        private final float mDefaultBackgroundAlpha = 0.4f;
        @BindView(R.id.diagnostics_step_three_text_item) TextView mLabel;
        @BindView(R.id.diagnostics_step_three_icon_background_item) LinearLayout mBackgroundIcon;
        @BindView(R.id.diagnostics_step_three_icon_item) ImageView mIcon;
        @BindView(R.id.card_view) CardView mCardView;
        private SelectGoalsItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mCardView.setOnClickListener(view -> mOnItemClickListener.onItemSelected(mItem));
        }

        @Override
        protected BaseAdapter<SelectGoalsItem> getAdapter() {
            return SelectGoalsAdapter.this;
        }

        @Override
        protected void bind(SelectGoalsItem item) {
            super.bind(item);
            this.mItem = item;

            bindTexts(item);
            bindState(item);
        }

        private void bindTexts(SelectGoalsItem item) {
            mLabel.setText(item.getStepThreeOptions().getText());
        }

        private void bindState(SelectGoalsItem item) {
            mLabel.setTextColor(ContextCompat.getColor(itemView.getContext(), item.isSelected() ? R.color.white : R.color.black_87));
            mIcon.setImageResource(item.isSelected() ? R.drawable.ic_check : R.drawable.ic_plus);
            mBackgroundIcon.setBackgroundColor(ColorHelper.getColorWithAlpha(item.getStepThreeOptions().getColor(), mDefaultBackgroundAlpha));

            if (item.isIdle()) {
                if (item.isSelected()) {
                    mCardView.setCardBackgroundColor(item.getStepThreeOptions().getColor());
                } else {
                    mCardView.setCardBackgroundColor(Color.WHITE);
                }
                item.setIdle(false);
            } else {
                ColorizeAnimator.animateBetweenColors(mCardView, item.isSelected() ? item.getStepThreeOptions().getColor() : Color.WHITE);
            }
        }

        @Override
        protected void onAnimated() {
            ViewCompat.setAlpha(itemView, 0.0f);
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 0.0f, 1.0f);
            ObjectAnimator slideInFromBottomAnimator = ObjectAnimator.ofFloat(itemView, "translationY", 350f, 0.0f);

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
