package com.google.android.apps.miyagi.development.ui.practice.switches;

import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext.SwitchesTextPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.SlideView;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 14.11.2016.
 */

class SwitchesAdapter extends FlexibleAdapter<SwitchItem> {

    private List<String> mCorrectOptions;
    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;

    private OnItemStateChange mOnItemStateChangeListener = state -> {
        Lh.v("SwitchesAdapter OnItemStateChange");
        updateBtnSubmit();
    };

    /**
     * Instantiates a new SwitchesAdapter.
     */
    private SwitchesAdapter(@Nullable List<SwitchItem> answers) {
        super(answers);
        for (SwitchItem item : answers) {
            item.setOnItemStateChangeListener(mOnItemStateChangeListener);
        }
    }

    /**
     * Creates new instance of SwitchesAdapter.
     */
    public static SwitchesAdapter create(
            List<SwitchesTextPracticeOption> options, List<String> correctOptions, int bgColor) {
        List<SwitchItem> items = new ArrayList<>();
        for (SwitchesTextPracticeOption option : options) {
            items.add(new SwitchItem(option, bgColor));
        }
        SwitchesAdapter adapter = new SwitchesAdapter(items);
        adapter.setCorrectOptions(correctOptions);
        return adapter;
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener btnSubmitUpdateListener) {
        mBtnSubmitUpdateListener = btnSubmitUpdateListener;
    }

    private void setCorrectOptions(List<String> correctOptions) {
        mCorrectOptions = correctOptions;
    }

    /**
     * Sets currently selected options.
     *
     * @param currentOptions - list of selected options ids.
     */
    void setCurrentOptions(List<String> currentOptions) {
        for (int i = 0; i < getItemCount(); i++) {
            SwitchItem item = getItem(i);
            if (currentOptions.contains(item.getItemId())) {
                item.setState(SlideView.State.RIGHT_ACTIVE);
            } else {
                item.setState(SlideView.State.LEFT_ACTIVE);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sets current states.
     * @param currentStates - list of selected options ids.
     */
    public void setCurrentStates(List<SlideView.State> currentStates) {
        for (int i = 0; i < getItemCount(); i++) {
            SwitchItem item = getItem(i);
            SlideView.State state = currentStates.get(i);
            item.setState(state);
        }
        notifyDataSetChanged();
    }

    private void updateBtnSubmit() {
        int size = getItemCount();
        int answerCount = 0;
        for (int i = 0; i < size; ++i) {
            SwitchItem item = getItem(i);
            if (item.getState() != SlideView.State.IDLE) {
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

        List<String> userAnswerIds = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            SwitchItem item = getItem(i);
            if (item.getState() == SlideView.State.RIGHT_ACTIVE) {
                userAnswerIds.add(item.getItemId());
            }
        }

        for (String userAnswerId : userAnswerIds) {
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

    interface OnItemStateChange {
        void onItemStateChange(SlideView.State state);
    }
}
