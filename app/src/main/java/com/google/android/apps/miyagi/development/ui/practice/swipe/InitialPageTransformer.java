package com.google.android.apps.miyagi.development.ui.practice.swipe;

import android.support.v4.view.ViewPager;
import android.view.View;

public class InitialPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.9473f;

    public InitialPageTransformer() {
    }

    /**
     * Apply a transformation to the given page.
     *
     * @param view     - apply a transformation to this page.
     * @param position - position of page relative to the current front-and-center position of the pager. 0 is front and center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (-1 <= position && position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            if (position == 0.f) {
                scaleFactor = MIN_SCALE;
            }

            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // distance between pages
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            float pageRatio = (float) pageHeight / (float) pageWidth;
            float offset = pageRatio * (horzMargin - vertMargin / 2);
            if (position <= 0) {
                view.setTranslationX(-offset);
            } else {
                view.setTranslationX(offset);
            }
        }
    }
}
