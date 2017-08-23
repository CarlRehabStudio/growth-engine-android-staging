package com.google.android.apps.miyagi.development.ui.practice.fortunewheel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelQuestionPage;
import com.google.android.apps.miyagi.development.ui.practice.fortunewheel.widget.OnFortuneWheelSelected;

import java.util.List;

/**
 * Created by jerzyw on 28.11.2016.
 */

public class FortuneWheelPracticePagerAdapter extends FragmentStatePagerAdapter {

    private FortuneWheelPracticeDetails mPracticeDetailData;
    private String mWheelInstructionText;
    private String mPracticeQuestionText;
    private OnFortuneWheelSelected mExternalSelectionListener;
    private boolean mIsSuccessful;

    public FortuneWheelPracticePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Sets data to adapter.
     *
     * @param practiceDetailData   - data for this practice
     * @param practiceQuestionText - question displayed on all pages.
     * @param wheelInstructionText - instruction displayed on all pages.
     */
    public void setData(FortuneWheelPracticeDetails practiceDetailData,
                        String practiceQuestionText,
                        String wheelInstructionText,
                        boolean isSuccessful) {

        mPracticeDetailData = practiceDetailData;
        mPracticeQuestionText = practiceQuestionText;
        mWheelInstructionText = wheelInstructionText;
        mIsSuccessful = isSuccessful;

        notifyDataSetChanged();
    }

    /**
     * Returns data item on given position.
     */
    public FortuneWheelQuestionPage getPageDataItem(int position) {
        List<FortuneWheelQuestionPage> pages = mPracticeDetailData.getPages();
        return pages.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        FortuneWheelQuestionPage pageData = getPageDataItem(position);
        FortuneWheelPageFragment fragment = FortuneWheelPageFragment.newInstance(
                position,
                pageData,
                mPracticeDetailData.getColors(),
                mPracticeQuestionText,
                mWheelInstructionText,
                mIsSuccessful
        );
        return fragment;
    }

    private void onItemSelected(FortuneWheelAnswerOption answer, int pageIndex) {
        if (mExternalSelectionListener != null) {
            mExternalSelectionListener.onItemSelected(answer, pageIndex);
        }
    }

    public void setOnSelectionChangeListener(OnFortuneWheelSelected listener) {
        mExternalSelectionListener = listener;
    }

    @Override
    public int getCount() {
        return (mPracticeDetailData == null) ? 0 : mPracticeDetailData.getPages().size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof FortuneWheelPageFragment) {
            ((FortuneWheelPageFragment) item).setOnSelectionChangeListener(this::onItemSelected);
        }
        return item;
    }
}
