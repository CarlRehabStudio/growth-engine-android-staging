package com.google.android.apps.miyagi.development.ui.assessment.navigation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.data.models.assessment.Question;
import com.google.android.apps.miyagi.development.ui.assessment.exam.ExamFragment;
import com.google.android.apps.miyagi.development.ui.assessment.instruction.InstructionFragment;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation.Step;
import com.google.android.apps.miyagi.development.ui.assessment.result.BadgeFragment;
import com.google.android.apps.miyagi.development.ui.assessment.result.ResultFragment;

import java.util.List;
import java.util.Map;

/**
 * Class used to navigate through the assessment flow (Instruction - Test - Result - Badge).
 */
public class NavigatorMobile extends Navigator {

    public NavigatorMobile(FragmentManager fragmentManager, Navigation navigation) {
        super(fragmentManager, navigation);
    }

    @Override
    public void launch(Fragment fragment, String tag) {
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .addToBackStack(fragment.toString())
                    .commit();
        }
    }

    private void launch(Fragment fragment, String tag, int containerViewId) {
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction()
                    .replace(containerViewId, fragment, tag)
                    .addToBackStack(fragment.toString())
                    .commit();
        }
    }

    @Override
    public void navigateBack(Activity baseActivity) {
        if (mFragmentManager.getBackStackEntryCount() <= 1) {
            baseActivity.finish();
        } else {
            mNavigation.setLabelVisibility(View.INVISIBLE);
            mNavigation.setPrevButtonVisibility(View.INVISIBLE);
            mNavigation.setNextEnable(true);
            mCurrentStep = Step.INSTRUCTION;
            mFragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public void launchInstruction(CopyInstructions copy, int topicColor, int mainBackgroundColor) {
        InstructionFragment instructionFragment = (InstructionFragment) mFragmentManager.findFragmentByTag(InstructionFragment.class.getCanonicalName());
        if (instructionFragment != null) {
            mFragmentManager.beginTransaction().remove(instructionFragment).commit();
        }

        instructionFragment = InstructionFragment.newInstance(copy);
        launch(instructionFragment, InstructionFragment.class.getCanonicalName());
        mCurrentStep = Step.INSTRUCTION;
    }

    @Override
    public void launchTest(CopyInstructions copy, int topicColor, List<Question> questions, boolean isResultMode, Map<String, Integer> answers, int mainBackgroundColor) {
        ExamFragment examFragment = (ExamFragment) mFragmentManager.findFragmentByTag(ExamFragment.class.getCanonicalName());
        if (examFragment != null) {
            mFragmentManager.beginTransaction().remove(examFragment).commit();
        }

        int count = mFragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count - 1; i++) {
            mFragmentManager.popBackStackImmediate();
        }
        examFragment = ExamFragment.newInstance(copy, topicColor, questions, isResultMode, answers, mainBackgroundColor);
        launch(examFragment, ExamFragment.class.getCanonicalName());
        mCurrentStep = Step.TEST;
    }

    @Override
    public void launchResultFailed(Copy copy, boolean noAttemptsLeft, int topColor, int mainBackgroundColor) {
        ResultFragment resultFragment = (ResultFragment) mFragmentManager.findFragmentByTag(ResultFragment.class.getCanonicalName());
        if (resultFragment != null) {
            mFragmentManager.beginTransaction().remove(resultFragment).commit();
        }

        resultFragment = ResultFragment.newInstance(copy, noAttemptsLeft, topColor, mainBackgroundColor);
        launch(resultFragment, ResultFragment.class.getCanonicalName(), R.id.container_overlay);
        mCurrentStep = Step.RESULT;
    }

    @Override
    public void launchBadge(Copy copy, int mainBackgroundColor) {
        BadgeFragment badgeFragment = (BadgeFragment) mFragmentManager.findFragmentByTag(BadgeFragment.class.getCanonicalName());
        if (badgeFragment != null) {
            mFragmentManager.beginTransaction().remove(badgeFragment).commit();
        }

        badgeFragment = BadgeFragment.newInstance(copy, mainBackgroundColor);
        launch(badgeFragment, BadgeFragment.class.getCanonicalName(), R.id.container_overlay);
        mCurrentStep = Step.BADGE;
    }

    @Override
    public void onNextClick() {
        if (mCurrentStep == Step.INSTRUCTION) {
            mNavigation.goTo(Step.TEST);
        } else if (mCurrentStep == Step.TEST) {
            ExamFragment examFragment = (ExamFragment) mFragmentManager.findFragmentByTag(ExamFragment.class.getCanonicalName());
            if (examFragment != null) {
                examFragment.onNextClick();
            }
        }
    }

    @Override
    public void onPrevClick() {
        if (mCurrentStep == Step.TEST) {
            ExamFragment examFragment = (ExamFragment) mFragmentManager.findFragmentByTag(ExamFragment.class.getCanonicalName());
            if (examFragment != null) {
                examFragment.onPrevClick();
            }
        }
    }
}
