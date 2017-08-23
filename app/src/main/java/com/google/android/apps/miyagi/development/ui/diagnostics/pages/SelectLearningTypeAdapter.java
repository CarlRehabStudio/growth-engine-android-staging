package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.UndefinedSelectedState;
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

class SelectLearningTypeAdapter extends BaseAdapter<SelectLearningTypeItem> {

    SelectLearningTypeAdapter(Context context, List<SelectLearningTypeItem> items, OnItemSelectedListener<SelectLearningTypeItem> listener) {
        super(context, items, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diagnostics_select_learning_type_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position));
    }

    public class ViewHolder extends BaseViewHolder<SelectLearningTypeItem> {

        @BindView(R.id.diagnostics_select_learning_type_item_title) TextView mTitle;
        @BindView(R.id.diagnostics_select_learning_type_item_description) TextView mDescription;
        @BindView(R.id.diagnostics_select_learning_type_item_image) ImageView mImage;
        @BindView(R.id.card_view) CardView mCardView;

        private SelectLearningTypeItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mCardView.setOnClickListener(view -> {
                mOnItemClickListener.onItemSelected(mItem);
            });
        }

        @Override
        protected BaseAdapter<SelectLearningTypeItem> getAdapter() {
            return SelectLearningTypeAdapter.this;
        }

        @Override
        protected void bind(SelectLearningTypeItem item) {
            super.bind(item);
            this.mItem = item;

            bindTexts(item);
            bindState(item);

        }

        private void bindTexts(SelectLearningTypeItem item) {
            mTitle.setText(item.getTitleText());
            mDescription.setText(item.getText());
            mImage.setImageResource(item.getLearningType().getDrawable());
            mImage.setBackgroundColor(item.getLearningType().getColor());
        }

        private void bindState(SelectLearningTypeItem item) {
            if (item.getSelectState() == UndefinedSelectedState.SELECTED) {
                ColorizeAnimator.animateAlpha(mCardView, 1.0f);
            } else if (item.getSelectState() == UndefinedSelectedState.UNSELECTED) {
                ColorizeAnimator.animateAlpha(mCardView, 0.4f);
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
