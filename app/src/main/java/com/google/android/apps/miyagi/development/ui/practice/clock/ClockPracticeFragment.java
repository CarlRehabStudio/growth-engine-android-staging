package com.google.android.apps.miyagi.development.ui.practice.clock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.clock.ClockAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.clock.ClockPracticeDetails;
import com.google.android.apps.miyagi.development.ui.practice.clock.widget.ClockWidget;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import org.parceler.Parcels;

/**
 * Created by jerzyw on 05.12.2016.
 */

public class ClockPracticeFragment extends AbstractPracticeFragment<Integer> {

    @BindView(R.id.practice_clock_question) TextView mQuestionTv;
    @BindView(R.id.practice_clock_main_widget) ClockWidget mClockWidget;

    private Unbinder mUnbinder;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener = on -> {
        if (on) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    };

    public ClockPracticeFragment() {

    }

    /**
     * Creates new ClockPracticeFragment instance with practice data.
     */
    public static ClockPracticeFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        ClockPracticeFragment fragment = new ClockPracticeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPracticeData = Parcels.unwrap(arguments.getParcelable(Practice.ARG_DATA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        initUi();
        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_fragment_clock;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getClockPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        ClockPracticeDetails details = mPracticeData.getClockPracticeDetails();
        initHeader(mQuestionTv, details.getQuestion());
        mClockWidget.setOptions(details.getOptions());
        mClockWidget.setBtnSubmitUpdateListener(mBtnSubmitUpdateListener);
    }

    @Override
    public void onNextClick() {
        onButtonSubmitClick();
    }

    @Override
    public void onPrevClick() {
        mNavCallback.goBack();
    }

    private void onButtonSubmitClick() {
        ClockAnswerOption answer = mClockWidget.getCurrentAnswer();
        if (verifyAnswers(answer)) {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
        } else {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
        }
    }

    private boolean verifyAnswers(ClockAnswerOption answer) {
        if (answer == null) {
            return false;
        } else {
            ClockPracticeDetails practiceDetail = mPracticeData.getClockPracticeDetails();
            return practiceDetail.getCorrectOptionId().equals(Integer.toString(answer.getId()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mClockWidget.getCurrentAnswer().getId()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mClockWidget.setCurrentOption(mPracticeData.getClockPracticeDetails().getCorrectOptionId());
    }

    @Override
    protected void recreateAnswers(Integer currentStates) {
        mClockWidget.setCurrentOption(Integer.toString(currentStates));
    }
}
