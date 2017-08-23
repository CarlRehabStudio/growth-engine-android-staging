package com.google.android.apps.miyagi.development.ui.onboarding.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukasz on 12.02.2017.
 */

public class OnboardingViewPager extends ViewPager {

    // we some the listner
    private List<OnPageChangeListener> mInternalPageListener;

    public OnboardingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInternalPageListener = new ArrayList<>();
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
        this.mInternalPageListener.add(listener);
    }

    /**
     * Invoke onPageSelected for 0 position item.
     *
     * @param item - fragment position.
     */
    @Override
    public void setCurrentItem(int item) {
        // when you pass set current item to 0,
        // the mInternalPageListener won't be called so we call it on our own
        boolean invokeMeLater = false;

        if (super.getCurrentItem() == 0 && item == 0) {
            invokeMeLater = true;
        }

        super.setCurrentItem(item);
        if (invokeMeLater && mInternalPageListener != null) {
            for (OnPageChangeListener listener : mInternalPageListener) {
                listener.onPageSelected(0);
            }
        }
    }
}