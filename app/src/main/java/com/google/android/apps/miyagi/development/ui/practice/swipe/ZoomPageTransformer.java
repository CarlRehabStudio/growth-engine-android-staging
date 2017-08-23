package com.google.android.apps.miyagi.development.ui.practice.swipe;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

public class ZoomPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
    private static final float MAX_ELEVATION = 8f;

    public ZoomPageTransformer() {
    }

    /**
     * Applies a transformation to the given page.
     *
     * @param view     - apply a transformation to this page.
     * @param position - position of page relative to the current front-and-center position of the pager. 0 is front and center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        float scaleFactor = 1f;
        float alpha = 1f;

        if (-1 <= position && position <= 1) {
            scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position / 4));

            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA);
            view.setAlpha(alpha);

            // distance between pages
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            float pageRatio = (float) pageHeight / (float) pageWidth;
            float offset = pageRatio * (horzMargin - vertMargin / 2);
            if (position < 0) {
                view.setTranslationX(-offset);
            } else {
                view.setTranslationX(offset);
            }
        }

        CardView cardView = (CardView) view.findViewById(R.id.page_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardView.setElevation(ViewUtils.dp2px(view.getContext(), MAX_ELEVATION) * (1f - alpha));
        }

    }
}