package com.google.android.apps.miyagi.development.ui.practice.tag.items;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud.TagCloudPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.ColorizeAnimator;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Tag item functionality class
 */

public class TagItem extends AbstractFlexibleItem<TagItem.TagViewHolder> {

    public static final String TOGGLE_ITEM = "TOGGLE_ITEM";

    private TagCloudPracticeOption mOption;
    private int mTextSelectedColor;
    private int mTextUnselectedColor;
    private int mSelectedColor;
    private int mUnselectedColor;

    /**
     * Constructs new Tag item for TagCloud activity.
     */
    public TagItem(TagCloudPracticeOption option, int selectedColor, int unselectedColor, int textSelectedColor, int textUnselectedColor) {
        mOption = option;
        mSelectedColor = selectedColor;
        mUnselectedColor = unselectedColor;
        mTextSelectedColor = textSelectedColor;
        mTextUnselectedColor = textUnselectedColor;
        setSelectable(true);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }


    public TagViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TagViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, TagViewHolder holder, int position, List payloads) {
        boolean isSelected = adapter.isSelected(position);
        if (payloads.size() == 0) {
            holder.populateData(mOption, isSelected, mTextSelectedColor, mTextUnselectedColor,
                    mSelectedColor, mUnselectedColor, false);
        } else if (payloads.contains(TOGGLE_ITEM)) {
            holder.populateData(mOption, isSelected, mTextSelectedColor, mTextUnselectedColor,
                    mSelectedColor, mUnselectedColor, true);
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_tag_cloud_item;
    }

    @Override
    public TagViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    String getOptionId() {
        return mOption.getId();
    }

    static class TagViewHolder extends FlexibleViewHolder {

        private final TextView mLabel;
        private final ImageView mIcon;
        private final CardView mCardView;
        private TagCloudPracticeOption mOption;

        /**
         * Constructs new Tag view holder object.
         */
        TagViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mCardView = (CardView) view.findViewById(R.id.tag_cloud_item_card);
            mLabel = (TextView) view.findViewById(R.id.tag_cloud_item_label);
            mIcon = (ImageView) view.findViewById(R.id.tag_cloud_item_icon);
        }

        void populateData(TagCloudPracticeOption item, boolean isSelected, int textSelectedColor,
                          int textUnselectedColor, int selectedColor, int unselectedColor, boolean toggled) {
            mOption = item;
            mLabel.setText(mOption.getText());
            mLabel.setTextColor(isSelected ? textSelectedColor : textUnselectedColor);
            mIcon.setImageResource(isSelected ? R.drawable.ic_check : R.drawable.ic_not_interested);

            if (toggled) {
                ColorizeAnimator.animateBetweenColors(mCardView, isSelected ? selectedColor : unselectedColor);
            } else {
                if (isSelected) {
                    mCardView.setCardBackgroundColor(selectedColor);
                } else {
                    mCardView.setCardBackgroundColor(unselectedColor);
                }
            }
        }
    }
}
