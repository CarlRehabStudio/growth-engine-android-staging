package com.google.android.apps.miyagi.development.ui.practice.swipe;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.apps.miyagi.development.R;

public class SwipeSelectorViewPager extends ViewPager {

    private int mContentWidth = 0;
    private int mCurrentOptionIndex = -1;

    public SwipeSelectorViewPager(Context context) {
        this(context, null);
    }

    /**
     * Constructs Swipe-Selector View Pager.
     *
     * @param context - context.
     * @param attrs   - attributes set.
     */
    public SwipeSelectorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        setPageTransformers();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measurePageMargin(widthMeasureSpec, heightMeasureSpec);
    }

    private void measurePageMargin(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            View page = getChildAt(0);
            page.measure(widthMeasureSpec, heightMeasureSpec);
            int pageWidth = page.getMeasuredWidth();

            View content = page.findViewById(R.id.page_content);
            mContentWidth = content.getMeasuredWidth();
            if (mContentWidth > 0) {
                int offset = -(pageWidth - mContentWidth);
                if (getPageMargin() != offset) {
                    setPageMargin(offset);
                    requestLayout();
                }
            }
        }
    }

    private void setPageTransformers() {
        setVisibility(INVISIBLE);
        setPageTransformer(false, new InitialPageTransformer());
        post(new ScrollToCentreAction());
        post(new SetDefaultPageTransformerAction());
    }

    /**
     * Sets currently selected option.
     */
    public void setCurrentOption(int optionIndex) {
        mCurrentOptionIndex = optionIndex;
        setCurrentItem(mCurrentOptionIndex);
        setPageTransformer(false, new ZoomPageTransformer());
    }

    private class ScrollToCentreAction implements Runnable {

        @Override
        public void run() {
            if (mCurrentOptionIndex < 0) {
                scrollTo(mContentWidth / 2, 0);
            }
        }
    }

    private class SetDefaultPageTransformerAction implements Runnable {

        @Override
        public void run() {
            setVisibility(VISIBLE);
            setPageTransformer(false, new ZoomPageTransformer());
        }
    }

}
