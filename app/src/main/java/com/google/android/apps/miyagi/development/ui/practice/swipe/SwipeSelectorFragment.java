package com.google.android.apps.miyagi.development.ui.practice.swipe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeDetails;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import org.parceler.Parcels;

public class SwipeSelectorFragment extends AbstractPracticeFragment<Integer> {

    @BindView(R.id.swipe_label_header) TextView mLabelHeader;
    @BindView(R.id.swipe_image_phone) ImageView mImagePhone;
    @BindView(R.id.swipe_view_pager) SwipeSelectorViewPager mViewPager;

    private String mCorrectOptionId;
    private SwipeSelectorPagerAdapter mAdapter;
    private int mCurrentOption = -1;

    private Unbinder mUnbinder;

    public SwipeSelectorFragment() {
    }

    /**
     * Constructs new instance of SwipeSelectorFragment.
     */
    public static SwipeSelectorFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        SwipeSelectorFragment fragment = new SwipeSelectorFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        initUi();
        mViewPager.addOnPageChangeListener(new OnSwipeSelectorPageChangeListener());
        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_swipe_selector_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getSwipePracticeDetails().getColors().getLessonColor();
    }

    private void initUi() {
        SwipePracticeDetails practiceDetailsData = mPracticeData.getSwipePracticeDetails();
        initHeader(mLabelHeader, practiceDetailsData.getQuestion());
        mCorrectOptionId = practiceDetailsData.getCorrectOption();
        bindColors(practiceDetailsData);
        bindViewPager(practiceDetailsData);
    }

    private void bindViewPager(final SwipePracticeDetails data) {
        mAdapter = new SwipeSelectorPagerAdapter(getFragmentManager(), data);
        mViewPager.setAdapter(mAdapter);
    }

    private void bindColors(final SwipePracticeDetails data) {
        SwipePracticeColors colors = data.getColors();
        mImagePhone.setColorFilter(colors.getPhoneColor());
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
        if (verifyAnswers()) {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
        } else {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
        }
    }

    private boolean verifyAnswers() {
        return mCorrectOptionId.equals(mAdapter.getOptionId(mCurrentOption));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mCurrentOption));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mCurrentOption = mAdapter.getOptionIndex(mCorrectOptionId);
        mViewPager.setCurrentOption(mCurrentOption);
        enableNavBtnSubmit();
    }

    @Override
    protected void recreateAnswers(Integer currentStates) {
        if (currentStates >= 0) {
            mCurrentOption = currentStates;
            mViewPager.setCurrentOption(currentStates);
            enableNavBtnSubmit();
        }
    }

    private class OnSwipeSelectorPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentOption = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (ViewPager.SCROLL_STATE_IDLE == state) {
                if (mCurrentOption == -1) {
                    mCurrentOption = 0;
                }
                enableNavBtnSubmit();
            }
        }
    }
}
