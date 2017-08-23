package com.google.android.apps.miyagi.development.ui.statistics.items;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.statistics.Page;
import com.google.android.apps.miyagi.development.data.models.statistics.PagesWrapper;
import com.google.android.apps.miyagi.development.data.models.statistics.StatisticsPageType;
import com.google.android.apps.miyagi.development.ui.components.widget.ArcProgressBar;

/**
 * Created by lukaszweglinski on 28.11.2016.
 */

public class StatisticsPager extends PagerAdapter {

    private final int ITEMS_COUNT;

    private final LayoutInflater mLayoutInflater;
    private PagesWrapper mPages;
    private boolean[] mAnimationStatesArray;
    private ArcProgressBar[] mArcProgressBarsArray;

    private static final long ANIMATION_DURATION = 1500;
    private static final long ANIMATION_DELAY = 500;

    private int mCurrentProgress;
    private int mCurrentBackgroundProgress;

    private ValueAnimator mCurrentProgressAnimation;
    private ValueAnimator mCurrentProgressBackgroundAnimation;
    private Animator.AnimatorListener mCurrentAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mCurrentProgressAnimation = null;
            mCurrentProgressBackgroundAnimation = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    /**
     * Construcs view pager to display user statistics.
     */
    public StatisticsPager(Context context, PagesWrapper pages) {
        mLayoutInflater = LayoutInflater.from(context);
        mPages = pages;
        ITEMS_COUNT = pages.size();
        mAnimationStatesArray = new boolean[ITEMS_COUNT];
        mArcProgressBarsArray = new ArcProgressBar[ITEMS_COUNT];
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.statistics_pager_item, container, false);
        StatisticsPageType type = StatisticsPageType.getTypeFromPosition(position);

        Page page = ITEMS_COUNT == 1 ? mPages.get(0) : mPages.getPageFromType(type);
        view.setBackgroundColor(page.getBackgroundColor());

        ArcProgressBar progressBar = (ArcProgressBar) view.findViewById(R.id.statistics_progress_bar);
        progressBar.setBottomText(page.getProgressTitle());

        progressBar.setForegroundStrokeColor(page.getSpeedometerColorFull());
        progressBar.setBackgroundStrokeColor(page.getSpeedometerColorEmpty());
        container.addView(view);

        view.setTag(Integer.toString(position));

        mArcProgressBarsArray[position] = progressBar;
        mAnimationStatesArray[position] = false;

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Animates progress bar with user statistics.
     *
     * @param pageNumber - number of page which data are displayed in progress bar.
     */
    public void animateProgressBar(int pageNumber) {
        boolean wasAnimated = mAnimationStatesArray[pageNumber];
        ArcProgressBar progressBar = mArcProgressBarsArray[pageNumber];
        if (wasAnimated) {
            progressBar.setArcAngle(progressBar.getFinalArcAngle());
            StatisticsPageType type = StatisticsPageType.getTypeFromPosition(pageNumber);
            Page page = ITEMS_COUNT == 1 ? mPages.get(0) : mPages.getPageFromType(type);
            progressBar.setProgress(page.getProgress());
        } else if (progressBar != null) {
            mAnimationStatesArray[pageNumber] = true;
            if (mCurrentProgressAnimation != null) {
                mCurrentProgressAnimation.removeAllListeners();
                mCurrentProgressAnimation.cancel();
            }
            if (mCurrentProgressBackgroundAnimation != null) {
                mCurrentProgressBackgroundAnimation.removeAllListeners();
                mCurrentProgressBackgroundAnimation.cancel();
            }

            mCurrentProgressAnimation = ObjectAnimator.ofInt(0, (int) progressBar.getFinalArcAngle());
            mCurrentProgressAnimation.setInterpolator(new FastOutSlowInInterpolator());
            mCurrentProgressAnimation.setDuration(ANIMATION_DURATION);
            mCurrentProgressAnimation.addUpdateListener(animation -> {
                mCurrentBackgroundProgress = (int) animation.getAnimatedValue();
                progressBar.setArcAngle(mCurrentBackgroundProgress);
                progressBar.invalidate();
            });
            mCurrentProgressAnimation.addListener(mCurrentAnimatorListener);
            mCurrentProgressAnimation.start();

            StatisticsPageType type = StatisticsPageType.getTypeFromPosition(pageNumber);
            Page page = ITEMS_COUNT == 1 ? mPages.get(0) : mPages.getPageFromType(type);
            int progress = page.getProgress();

            mCurrentProgressBackgroundAnimation = ObjectAnimator.ofInt(0, progress);
            mCurrentProgressBackgroundAnimation.setInterpolator(new FastOutSlowInInterpolator());
            mCurrentProgressBackgroundAnimation.setDuration(ANIMATION_DURATION);
            mCurrentProgressBackgroundAnimation.setStartDelay(ANIMATION_DELAY);
            mCurrentProgressBackgroundAnimation.addUpdateListener(animation -> {
                mCurrentProgress = (int) animation.getAnimatedValue();
                progressBar.setProgress(mCurrentProgress);
                progressBar.invalidate();
            });
            mCurrentProgressBackgroundAnimation.addListener(mCurrentAnimatorListener);
            mCurrentProgressBackgroundAnimation.start();
        }
    }
}
