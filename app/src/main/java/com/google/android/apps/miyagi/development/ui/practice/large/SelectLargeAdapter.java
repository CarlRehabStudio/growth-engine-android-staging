package com.google.android.apps.miyagi.development.ui.practice.large;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.List;

/**
 * Created by lukaszweglinski on 23.11.2016.
 */

class SelectLargeAdapter extends FlexibleAdapter<SelectLargeItem> implements FlexibleAdapter.OnItemClickListener {

    String mCorrectOption;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;

    SelectLargeAdapter(@Nullable List<SelectLargeItem> items, @Nullable String correctOption) {
        super(items);
        addListener(this);
        mCorrectOption = correctOption;
    }

    @Override
    public void toggleSelection(@IntRange(from = 0L) int position) {
        super.toggleSelection(position);
        for (int i = 0; i < getItemCount(); i++) {
            getItem(i).setState(isSelected(i));
            notifyItemChanged(i);
        }
        updateBtnSubmit();
    }

    private void updateBtnSubmit() {
        if (getSelectedPositions().size() == 0) {
            mBtnSubmitUpdateListener.enable(false);
        } else {
            mBtnSubmitUpdateListener.enable(true);
        }
    }

    @Override
    public boolean onItemClick(int position) {
        selectItem(position);
        return false;
    }

    /**
     * Selects item at specified position.
     * @param position select item position.
     */
    public void selectItem(int position) {
        clearSelection();
        toggleSelection(position);
    }

    public PracticeResult verifyAnswers() {
        List<Integer> selectedPositions = getSelectedPositions();
        if (selectedPositions.size() == 1) {
            int pos = selectedPositions.get(0);
            if (mCorrectOption.equals(getItem(pos).getItemId())) {
                return PracticeResult.SUCCESSFUL;
            }
        }
        return PracticeResult.FAIL;
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener listener) {
        mBtnSubmitUpdateListener = listener;
    }
}