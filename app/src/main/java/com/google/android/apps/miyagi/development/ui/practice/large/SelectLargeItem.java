package com.google.android.apps.miyagi.development.ui.practice.large;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeOption;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.UndefinedSelectedState;
import com.google.android.apps.miyagi.development.ui.components.widget.ScalableImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Select Large item class
 */

public class SelectLargeItem extends AbstractFlexibleItem<SelectLargeItem.SelectLargeViewHolder> {

    private final SelectLargePracticeOption mPracticeOption;
    private UndefinedSelectedState mSelectLargeState = UndefinedSelectedState.UNDEFINED;

    SelectLargeItem(SelectLargePracticeOption practiceOption) {
        mPracticeOption = practiceOption;
        setSelectable(true);
    }

    public SelectLargeViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new SelectLargeViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SelectLargeViewHolder holder, int position, List payloads) {
        holder.setData(mPracticeOption, position);
        holder.setSelected(mSelectLargeState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_select_large_item;
    }

    @Override
    public SelectLargeViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    void setState(boolean isSelected) {
        mSelectLargeState = isSelected ? UndefinedSelectedState.SELECTED : UndefinedSelectedState.UNSELECTED;
    }

    public String getItemId() {
        return mPracticeOption.getId();
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    static class SelectLargeViewHolder extends FlexibleViewHolder {

        final RequestManager mRequestManager;
        final ScalableImageView mImageView;
        final TextView mText;

        SelectLargeViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            mText = (TextView) view.findViewById(R.id.select_large_item_label);
            mImageView = (ScalableImageView) view.findViewById(R.id.select_large_item_image);
            mRequestManager = Glide.with(itemView.getContext());
        }

        void setData(SelectLargePracticeOption practiceOption, int position) {
            if (!TextUtils.isEmpty(practiceOption.getText())) {
                mText.setText(practiceOption.getText());
                mText.setVisibility(View.VISIBLE);
            } else {
                mText.setText(null);
                mText.setVisibility(View.GONE);
            }

            mRequestManager
                    .load(ImageUrlHelper.getUrlFor(GoogleApplication.getInstance().getBaseContext(), practiceOption.getImages()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .dontAnimate()
                    .into(mImageView);
        }

        void setSelected(UndefinedSelectedState selectState) {
            if (selectState != UndefinedSelectedState.UNDEFINED) {
                boolean isSelected = selectState == UndefinedSelectedState.SELECTED;
                mImageView.setChecked(isSelected, true);

                ValueAnimator textColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                        mText.getCurrentTextColor(),
                        ContextCompat.getColor(itemView.getContext(), isSelected ? R.color.black_87 : R.color.black_54));
                textColorAnimation.addUpdateListener(animation ->
                        mText.setTextColor((int) animation.getAnimatedValue())
                );

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(300);
                animatorSet.play(textColorAnimation);
                animatorSet.start();
            }
        }
    }
}
