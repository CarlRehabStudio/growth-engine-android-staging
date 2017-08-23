package com.google.android.apps.miyagi.development.ui.practice.strike;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcin on 12.12.2016.
 */

public class StrikeThroughAdapter extends FlexibleAdapter<StrikeThroughItem> implements FlexibleAdapter.OnItemClickListener {

    private List<String> mSelectedIds = new ArrayList<>();
    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;

    public StrikeThroughAdapter(@Nullable List<StrikeThroughItem> items, boolean stableIds) {
        super(items,  stableIds);
        addListener(this);
    }

    @Override
    public void toggleSelection(@IntRange(from = 0L) int position) {
        super.toggleSelection(position);
        getItem(position).toggle();
        notifyItemChanged(position);
        updateBtnSubmit();
    }

    /**
     * Returns ids of selected items.
     */
    public List<String> getSelectedIds() {
        mSelectedIds.clear();
        List<Integer> selectedPositions = getSelectedPositions();
        for (Integer pos : selectedPositions) {
            StrikeThroughItem item = getItem(pos);
            mSelectedIds.add(item.getOptionId());
        }
        return mSelectedIds;
    }

    private void updateBtnSubmit() {
        if (getSelectedPositions().size() == 0) {
            mBtnSubmitUpdateListener.enable(false);
        } else {
            mBtnSubmitUpdateListener.enable(true);
        }
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener listener) {
        mBtnSubmitUpdateListener = listener;
    }

    @Override
    public boolean onItemClick(int position) {
        toggleSelection(position);
        return false;
    }

    /**
     * Sets currently selected options.
     * @param currentOptions - list of selected options ids.
     */
    public void setCurrentOptions(List<String> currentOptions) {
        for (int i = 0; i < getItemCount(); i++) {
            StrikeThroughItem item = getItem(i);
            if (currentOptions.contains(item.getOptionId())) {
                toggleSelection(i);
            }
        }
    }
}
