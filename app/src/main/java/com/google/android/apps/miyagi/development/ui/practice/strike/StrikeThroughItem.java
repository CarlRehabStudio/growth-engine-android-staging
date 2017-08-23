package com.google.android.apps.miyagi.development.ui.practice.strike;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.strikethrough.StrikeThroughPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.StrikeThroughLayout;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Strike through Item functionality class
 */

public class StrikeThroughItem extends AbstractFlexibleItem<StrikeThroughItem.ViewHolder> {

    private StrikeThroughPracticeOption mOption;
    private boolean mChecked;

    public StrikeThroughItem(StrikeThroughPracticeOption option) {
        mOption = option;
        setSelectable(true);
    }

    public StrikeThroughItem.ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new StrikeThroughItem.ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, StrikeThroughItem.ViewHolder holder, int position, List payloads) {
        holder.mLabel.setText(mOption.getText());
        holder.mLayout.setChecked(mChecked, true);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_strike_through_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    public String getOptionId() {
        return mOption.getId();
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    public void toggle() {
        mChecked = !mChecked;
    }

    public void setChecked(boolean isChecked) {
        mChecked = isChecked;

    }

    static class ViewHolder extends FlexibleViewHolder {
        private final StrikeThroughLayout mLayout;
        private final TextView mLabel;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mLayout = (StrikeThroughLayout) view.findViewById(R.id.strike_through_item_layout);
            mLabel = (TextView) view.findViewById(R.id.strike_through_item_label);
        }
    }
}
