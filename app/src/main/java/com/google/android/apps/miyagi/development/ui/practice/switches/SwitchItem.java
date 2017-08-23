package com.google.android.apps.miyagi.development.ui.practice.switches;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext.SwitchesTextPracticeOption;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.SlideView;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.Lh;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Switch Item functionality class
 */
class SwitchItem extends AbstractFlexibleItem<SwitchItem.SwitchesViewHolder> {

    private SwitchesTextPracticeOption mOption;
    private int mBackgroundColor;
    private SlideView.State mSlideViewState = SlideView.State.IDLE;
    private SwitchesAdapter.OnItemStateChange mOnItemStateChangeListener;

    private SlideView.OnStateChange mOnStateChangeListener = state -> {
        mSlideViewState = state;
        mOnItemStateChangeListener.onItemStateChange(state);
        Lh.v("New slide view state in SwitchItem");
    };

    /**
     * Instantiates a new SwitchItem.
     */
    SwitchItem(SwitchesTextPracticeOption option, int backgroundColor) {
        mOption = option;
        mBackgroundColor = backgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }


    public SwitchesViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new SwitchesViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SwitchesViewHolder holder, int position, List payloads) {
        holder.setData(mOption, mBackgroundColor, mSlideViewState);
        holder.setOnStateChangeListener(mOnStateChangeListener);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_switches_text_item;
    }

    @Override
    public SwitchesViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    public void setOnItemStateChangeListener(SwitchesAdapter.OnItemStateChange onItemStateChangeListener) {
        mOnItemStateChangeListener = onItemStateChangeListener;
    }

    public SlideView.State getState() {
        return mSlideViewState;
    }

    public void setState(SlideView.State state) {
        mSlideViewState = state;
    }

    public String getItemId() {
        return mOption.getId();
    }

    static class SwitchesViewHolder extends FlexibleViewHolder {

        private final SlideView mSlideView;

        SwitchesViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mSlideView = (SlideView) view.findViewById(R.id.slide_view);
        }

        void setData(SwitchesTextPracticeOption option, int background, SlideView.State state) {
            mSlideView.setText(HtmlHelper.fromHtml(option.getText()));
            mSlideView.setState(state);
        }

        public void setOnStateChangeListener(SlideView.OnStateChange onStateChangeListener) {
            mSlideView.setOnStateChangeListener(onStateChangeListener);
        }
    }
}
