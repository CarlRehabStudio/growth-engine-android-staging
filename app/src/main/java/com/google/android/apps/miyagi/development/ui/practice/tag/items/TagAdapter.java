package com.google.android.apps.miyagi.development.ui.practice.tag.items;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 14.11.2016.
 */

public class TagAdapter extends FlexibleAdapter<TagItem> {

    private List<String> mSelectedIds = new ArrayList<>();

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;

    public TagAdapter(@Nullable List<TagItem> items, @Nullable OnItemClickListener listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }

    @Override
    public void toggleSelection(@IntRange(from = 0L) int position) {
        super.toggleSelection(position);
        notifyItemChanged(position, TagItem.TOGGLE_ITEM);
        updateBtnSubmit();
    }

    private void updateBtnSubmit() {
        if (getSelectedPositions().size() == 0) {
            mBtnSubmitUpdateListener.enable(false);
        } else {
            mBtnSubmitUpdateListener.enable(true);
        }
    }

    /**
     * Gets ids of user selected options.
     */
    public List<String> getSelectedIds() {
        mSelectedIds.clear();
        List<Integer> selectedPositions = getSelectedPositions();
        for (Integer pos : selectedPositions) {
            TagItem item = getItem(pos);
            mSelectedIds.add(item.getOptionId());
        }
        return mSelectedIds;
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener listener) {
        mBtnSubmitUpdateListener = listener;
    }
}
