package com.google.android.apps.miyagi.development.ui.practice.rightwrong.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeOption;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.components.widget.SelectRightView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Select correct item class. Functionality for Select right item
 */

public class SelectRightItem extends AbstractFlexibleItem<SelectRightItem.SelectRightViewHolder> {

    private int mBackgroundColor;
    private SelectRightPracticeOption mOption;
    private SelectRightView.State mState = SelectRightView.State.IDLE;
    private SelectRightAdapter.OnItemStateChange mOnItemStateChangeListener;
    private SelectRightView.OnStateChange mOnStateChangeListener;

    /**
     * Instantiates a new SwitchItem.
     *
     * @param option          option to answer.
     * @param backgroundColor background color for SelectRightView.
     */
    public SelectRightItem(SelectRightPracticeOption option, int backgroundColor) {
        mOption = option;
        mBackgroundColor = backgroundColor;
        mOnStateChangeListener = state -> {
            mState = state;
            mOnItemStateChangeListener.onItemStateChange(state);
        };
    }


    public SelectRightViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new SelectRightViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SelectRightViewHolder holder, int position, List payloads) {
        holder.setData(mOption, mBackgroundColor, mState);
        holder.setOnStateChangeListener(mOnStateChangeListener);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_select_right_item;
    }

    @Override
    public SelectRightViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    public SelectRightView.State getState() {
        return mState;
    }

    public void setState(SelectRightView.State state) {
        mState = state;
    }

    void setOnItemStateChangeListener(SelectRightAdapter.OnItemStateChange onItemStateChangeListener) {
        mOnItemStateChangeListener = onItemStateChangeListener;
    }

    public int getItemId() {
        return mOption.getId();
    }

    static class SelectRightViewHolder extends FlexibleViewHolder {
        private final SelectRightView mSelectRightView;
        private final TextView mHeader;
        private SelectRightPracticeOption mItem;

        SelectRightViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mSelectRightView = (SelectRightView) view.findViewById(R.id.select_right_item_view);
            mHeader = (TextView) view.findViewById(R.id.select_right_item_text);
        }

        void setData(SelectRightPracticeOption option, int backgroundColor, SelectRightView.State state) {
            mItem = option;
            mHeader.setText(mItem.getText());
            mSelectRightView.setBackgroundColor(backgroundColor);
            Glide.with(itemView.getContext())
                    .load(ImageUrlHelper.getUrlFor(GoogleApplication.getInstance().getBaseContext(), mItem.getImages()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .dontAnimate()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            mSelectRightView.setDrawable(resource);
                        }
                    });
            if (state != mSelectRightView.getState()) {
                mSelectRightView.setState(state);
            }
        }

        void setOnStateChangeListener(SelectRightView.OnStateChange onStateChangeListener) {
            mSelectRightView.setOnStateChangeListener(onStateChangeListener);
        }
    }
}
