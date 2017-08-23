package com.google.android.apps.miyagi.development.ui.practice.rightwrong.items;

import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.ui.components.widget.SelectRightView;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 13.12.2016.
 */

public class SelectRightAdapter extends FlexibleAdapter<SelectRightItem> {

    private List<Integer> mCorrectOptions;
    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;
    private SelectRightAdapter.OnItemStateChange mOnItemStateChangeListener;

    /**
     * Instantiates a new SelectRightAdapter.
     *
     * @param questions list of questions to display.
     */
    public SelectRightAdapter(@Nullable List<SelectRightItem> questions) {
        super(questions);
        mOnItemStateChangeListener = state -> {
            Lh.v("SelectRightAdapter OnItemStateChange");
            updateBtnSubmit();
        };
        for (SelectRightItem item : questions) {
            item.setOnItemStateChangeListener(mOnItemStateChangeListener);
        }
    }

    public interface OnItemStateChange {
        void onItemStateChange(SelectRightView.State state);
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener btnSubmitUpdateListener) {
        mBtnSubmitUpdateListener = btnSubmitUpdateListener;
    }

    public void setCorrectOptions(List<Integer> correctOptions) {
        mCorrectOptions = correctOptions;
    }

    /**
     * Sets currently selected options.
     * @param currentOptions - list of selected options ids.
     */
    public void setCurrentOptions(List<Integer> currentOptions) {
        for (int i = 0; i < getItemCount(); i++) {
            SelectRightItem item = getItem(i);
            if (currentOptions.contains(i)) {
                item.setState(SelectRightView.State.RIGHT);
            } else {
                item.setState(SelectRightView.State.LEFT);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sets current states.
     * @param currentStates - list of selected options ids.
     */
    public void setCurrentStates(List<SelectRightView.State> currentStates) {
        for (int i = 0; i < getItemCount(); i++) {
            SelectRightItem item = getItem(i);
            SelectRightView.State state = currentStates.get(i);
            item.setState(state);
        }
        notifyDataSetChanged();
    }

    private void updateBtnSubmit() {
        int size = getItemCount();
        int answerCount = 0;
        for (int i = 0; i < size; ++i) {
            SelectRightItem item = getItem(i);
            if (item.getState() != SelectRightView.State.IDLE) {
                ++answerCount;
            }
        }
        mBtnSubmitUpdateListener.enable(size == answerCount);
    }

    /**
     * Verifies user answers.
     */
    public PracticeResult verifyAnswers() {
        int size = getItemCount();
        int correctCount = 0;

        List<Integer> userAnswerIds = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            SelectRightItem item = getItem(i);
            if (item.getState() == SelectRightView.State.RIGHT) {
                userAnswerIds.add(item.getItemId());
            }
        }

        for (int userAnswerId : userAnswerIds) {
            if (mCorrectOptions.contains(userAnswerId)) {
                ++correctCount;
            }
        }

        if (userAnswerIds.size() == correctCount && mCorrectOptions.size() == correctCount) {
            return PracticeResult.SUCCESSFUL;
        } else {
            if (correctCount > 0) {
                return PracticeResult.ALMOST;
            } else {
                return PracticeResult.FAIL;
            }
        }
    }
}
