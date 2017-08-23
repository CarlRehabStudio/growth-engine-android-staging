package com.google.android.apps.miyagi.development.ui.practice.swipe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeOption;

import java.util.List;


public class SwipeSelectorPagerAdapter extends FragmentStatePagerAdapter {

    private List<SwipePracticeOption> mOptions;
    private int mPhoneColor;

    /**
     * Constructs page adapter for Swipe Selector exercise.
     */
    public SwipeSelectorPagerAdapter(FragmentManager fm, SwipePracticeDetails practiceData) {
        super(fm);
        mOptions = practiceData.getOptions();
        mPhoneColor = practiceData.getColors().getPhoneColor();
    }

    @Override
    public Fragment getItem(int position) {
        return SwipeSelectorPageFragment.create(position, mOptions.get(position), mPhoneColor);
    }

    @Override
    public int getCount() {
        return 2; // Swipe Selector has only 2 pages.
    }

    public String getOptionId(int position) {
        return mOptions.get(position).getId();
    }

    /**
     * Returns given option position.
     */
    public int getOptionIndex(String optionId) {
        for (int i = 0; i < mOptions.size(); i++) {
            if (mOptions.get(i).getId().equals(optionId)) {
                return i;
            }
        }
        return 0;
    }
}
