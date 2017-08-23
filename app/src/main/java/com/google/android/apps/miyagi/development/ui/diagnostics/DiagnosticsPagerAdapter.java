package com.google.android.apps.miyagi.development.ui.diagnostics;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepOneOptions;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.DiagnosticsCallback;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.LearningType;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectGoalsFragment;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectLearningTypeFragment;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectPersonaFragment;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.List;

/**
 * Created by marcin on 07.02.2017.
 */

class DiagnosticsPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEMS_NUMBER = 3;
    private DiagnosticsResponseData mResponseData;
    private String mPersona;
    private List<Integer> mGoals;
    private DiagnosticsCallback mDiagnosticsCallback;
    private OnItemSelectedListener<StepOneOptions> mPersonaSelectedListener = item -> {
        mPersona = item.getId();
        mDiagnosticsCallback.selectPersona(mPersona);
        mDiagnosticsCallback.enableNavBtnNext(true);
    };
    private OnItemSelectedListener<LearningType> mLearningTypeSelectedListener = learningType -> {
        mDiagnosticsCallback.selectLearningType(learningType);
        mDiagnosticsCallback.enableNavBtnNext(true);
    };
    private OnItemSelectedListener<List<Integer>> mGoalSelectedListener = goals -> {
        mGoals = goals;
        if (mGoals != null) {
            mDiagnosticsCallback.enableNavBtnNext(mGoals.size() > 0);
            mDiagnosticsCallback.selectGoals(mGoals);
        }
    };

    DiagnosticsPagerAdapter(FragmentManager fm, DiagnosticsResponseData responseData) {
        super(fm);
        mResponseData = responseData;
    }

    @Override
    public int getCount() {
        return ITEMS_NUMBER;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                ((SelectPersonaFragment) fragment).setOnSelectionChangeListener(mPersonaSelectedListener);
                break;
            case 1:
                ((SelectLearningTypeFragment) fragment).setOnSelectionChangeListener(mLearningTypeSelectedListener);
                break;
            case 2:
                SelectGoalsFragment fragmentStepThree = (SelectGoalsFragment) fragment;
                if (fragmentStepThree != null) {
                    if (mPersona != null) {
                        fragmentStepThree.setPersona(mPersona);
                    }
                    fragmentStepThree.setOnSelectionChangeListener(mGoalSelectedListener);
                }
                break;
            default:
                break;
        }

        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SelectPersonaFragment fragmentOne = SelectPersonaFragment.newInstance(mResponseData);
                fragmentOne.setOnSelectionChangeListener(mPersonaSelectedListener);
                return fragmentOne;
            case 1:
                SelectLearningTypeFragment fragmentTwo = SelectLearningTypeFragment.newInstance(mResponseData);
                fragmentTwo.setOnSelectionChangeListener(mLearningTypeSelectedListener);
                return fragmentTwo;
            case 2:
                SelectGoalsFragment fragmentThree = SelectGoalsFragment.newInstance(mResponseData);
                fragmentThree.setOnSelectionChangeListener(mGoalSelectedListener);
                return fragmentThree;
            default:
                return null;
        }
    }

    void setDiagnosticsCallback(DiagnosticsCallback callback) {
        mDiagnosticsCallback = callback;
    }

    /**
     * Gets item screen name.
     *
     * @param currentItem - current item position.
     * @return - screen name from position.
     */
    int getItemScreenName(int currentItem) {
        switch (currentItem) {
            case 0:
                return R.string.screen_create_plan_persona;
            case 1:
                return R.string.screen_create_plan;
            case 2:
                return R.string.screen_create_plan_goals;
            default:
                return -1;
        }
    }
}
