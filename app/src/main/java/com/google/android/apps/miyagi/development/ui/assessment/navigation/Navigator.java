package com.google.android.apps.miyagi.development.ui.assessment.navigation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.data.models.assessment.Question;
import com.google.android.apps.miyagi.development.ui.assessment.common.IAssessmentBottomNav;

import java.util.List;
import java.util.Map;

/**
 * Class used to navigate through the assessment flow (Instruction - Test - Result - Badge).
 */
public abstract class Navigator implements IAssessmentBottomNav.Callback {

    protected Navigation mNavigation;
    protected FragmentManager mFragmentManager;
    protected Navigation.Step mCurrentStep = Navigation.Step.INSTRUCTION;

    /**
     * Creates instance of Navigator object for Assessment.
     */
    public Navigator(FragmentManager fragmentManager, Navigation navigation) {
        mFragmentManager = fragmentManager;
        mNavigation = navigation;
    }

    protected abstract void launch(Fragment fragment, String tag);

    /**
     * Navigates back using back stack.
     */
    public abstract void navigateBack(Activity baseActivity);

    /**
     * Navigates to instruction screen.
     */
    public abstract void launchInstruction(CopyInstructions copy, int topicColor, int mainBackgroundColor);

    /**
     * Navigates to test screen.
     */
    public abstract void launchTest(CopyInstructions copy, int topicColor, List<Question> questions, boolean isResultMode, Map<String, Integer> answers, int mainBackgroundColor);

    /**
     * Navigates to result failed screen.
     */
    public abstract void launchResultFailed(Copy copy, boolean noAttemptsLeft, int topColor, int mainBackgroundColor);

    /**
     * Navigates to result success - badge screen.
     */
    public abstract void launchBadge(Copy copy, int mainBackgroundColor);

    public Navigation.Step getCurrentStep() {
        return mCurrentStep;
    }

    public void setCurrentStep(Navigation.Step currentStep) {
        mCurrentStep = currentStep;
    }
}
