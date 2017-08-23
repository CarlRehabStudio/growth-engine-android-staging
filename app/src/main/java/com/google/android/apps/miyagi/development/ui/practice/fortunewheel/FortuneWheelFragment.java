package com.google.android.apps.miyagi.development.ui.practice.fortunewheel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelQuestionPage;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jerzyw on 28.11.2016.
 */

public class FortuneWheelFragment extends AbstractPracticeFragment<Map<Integer, FortuneWheelAnswerOption>> {

    @BindView(R.id.practice_fortune_wheel_pager) NonSwipeableViewPager mViewPager;

    private FortuneWheelPracticePagerAdapter mPagerAdapter;
    private Map<Integer, FortuneWheelAnswerOption> mUserAnswers;

    private Unbinder mUnbinder;

    /**
     * Create new instance of FortuneWheelFragment.
     */
    public static FortuneWheelFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        FortuneWheelFragment fragment = new FortuneWheelFragment();

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
        initBottomNav();
        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_fragment_fortune_wheel_pager;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getFortuneWheelPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        mPagerAdapter = new FortuneWheelPracticePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setOnSelectionChangeListener(this::onUserAnswer);

        FortuneWheelPracticeDetails details = mPracticeData.getFortuneWheelPracticeDetails();
        mPagerAdapter.setData(
                details,
                details.getQuestion(),
                mPracticeData.getInstructionDialogText(),
                mIsSuccessful
        );
        updateNextButtonLabelByStep(mViewPager.getCurrentItem(), mPagerAdapter.getCount() - 1);
        enablePrevButtonLabelByStep(mViewPager.getCurrentItem());
        mUserAnswers = new HashMap<>();

        mNavCallback.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public void onNextClick() {
        onNextButtonClicked();
    }

    @Override
    public void onPrevClick() {
        onPrevButtonClicked();
    }

    private void initBottomNav() {
        enableNavBtnSubmitByStep();
        mNavCallback.setNavInstructionVisibility(View.GONE);
    }

    private void enableNavBtnSubmitByStep() {
        int currPageIndex = mViewPager.getCurrentItem();
        int lastPageIndex = mPagerAdapter.getCount() - 1;
        if (currPageIndex == lastPageIndex) {
            enableNavBtnSubmitIfAllQuestionAnswered();
        } else {
            enableNavBtnSubmit();
        }
    }

    private void enableNavBtnSubmitIfAllQuestionAnswered() {
        if (userAnsweredAllQuestions()) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    }

    private boolean userAnsweredAllQuestions() {
        boolean userAnsweredAllQuestions = true;
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            if (mUserAnswers.get(i) == null) {
                userAnsweredAllQuestions = false;
                break;
            }
        }
        return userAnsweredAllQuestions;
    }

    private void onUserAnswer(FortuneWheelAnswerOption answer, int pageIndex) {
        mUserAnswers.put(pageIndex, answer);
        enableNavBtnSubmitByStep();
    }

    private void onNextButtonClicked() {
        int currPageIndex = mViewPager.getCurrentItem();
        int lastPageIndex = mPagerAdapter.getCount() - 1;
        if (currPageIndex >= lastPageIndex) {
            goToResultScreen();
        } else {
            ++currPageIndex;
            mViewPager.setCurrentItem(currPageIndex, true);
            updateNextButtonLabelByStep(currPageIndex, lastPageIndex);
            enablePrevButtonLabelByStep(currPageIndex);
            if (currPageIndex == lastPageIndex) {
                enableNavBtnSubmitIfAllQuestionAnswered();
            }
        }
    }

    private void onPrevButtonClicked() {
        int currPageIndex = mViewPager.getCurrentItem();
        int lastPageIndex = mPagerAdapter.getCount() - 1;
        if (currPageIndex == 0) {
            mNavCallback.goBack();
        } else {
            --currPageIndex;
            mViewPager.setCurrentItem(currPageIndex, true);
            updateNextButtonLabelByStep(currPageIndex, lastPageIndex);
            enablePrevButtonLabelByStep(currPageIndex);
            enableNavBtnSubmit();
        }
    }

    private void goToResultScreen() {
        PracticeResult result = verifyAnswers();
        switch (result) {
            case FAIL:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
                break;
            case SUCCESSFUL:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
                break;
            case ALMOST:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_ALMOST);
                break;
            default:
                Lh.e(this, "Unknown PracticeResult: " + result);
        }
    }

    private PracticeResult verifyAnswers() {
        int count = mPagerAdapter.getCount();
        FortuneWheelAnswerOption answer;
        FortuneWheelQuestionPage pageData;
        String correctOptionId;
        int incorrectAnswersNum = 0;
        for (int i = 0; i < count; ++i) {
            pageData = mPagerAdapter.getPageDataItem(i);
            correctOptionId = pageData.getCorrectOption();
            answer = mUserAnswers.get(i);
            if (!correctOptionId.equals(answer.getId())) {
                ++incorrectAnswersNum;
            }
        }

        if (incorrectAnswersNum == 0) {
            return PracticeResult.SUCCESSFUL;
        } else if (incorrectAnswersNum == count) {
            return PracticeResult.FAIL;
        } else {
            return PracticeResult.ALMOST;
        }
    }

    private void updateNextButtonLabelByStep(int currentPageIndex, int lastPageIndex) {
        if (currentPageIndex == lastPageIndex) {
            mNavCallback.setNextButtonText(mPracticeData.getInstructionSubmitButtonText());
        } else {
            mNavCallback.setNextButtonText(mPracticeData.getFortuneWheelPracticeDetails().getPracticeNextStep());
        }
    }

    private void enablePrevButtonLabelByStep(int currentPageIndex) {
        if (currentPageIndex == 0) {
            enableNavBtnPrevIfNotSuccessfulAndNotTablet();
        } else {
            enableNavBtnPrev();
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
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mUserAnswers));
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void recreateAnswers() {
    }

    @Override
    protected void recreateAnswers(Map<Integer, FortuneWheelAnswerOption> currentStates) {
        mUserAnswers = currentStates;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            updateNextButtonLabelByStep(mViewPager.getCurrentItem(), mPagerAdapter.getCount() - 1);
            enablePrevButtonLabelByStep(mViewPager.getCurrentItem());
            enableNavBtnSubmitByStep();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
