package com.google.android.apps.miyagi.development.ui.components.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by lukaszweglinski on 14.03.2017.
 */

public class AdjustableImageView extends android.support.v7.widget.AppCompatImageView {

    private static final float CONCEPT_SCREEN_WIDTH = 360f;
    private static final float CONCEPT_MEDIUM_IMAGE_HEIGHT = 202f;
    public static final float MEDIUM_RATIO = CONCEPT_SCREEN_WIDTH / CONCEPT_MEDIUM_IMAGE_HEIGHT;
    private static final float CONCEPT_DASHBOARD_IMAGE_HEIGHT = 288f;
    public static final float DASHBOARD_RATIO = CONCEPT_SCREEN_WIDTH / CONCEPT_DASHBOARD_IMAGE_HEIGHT;

    private float mAspectRatio;

    public AdjustableImageView(Context context) {
        this(context, null);
    }

    public AdjustableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdjustableImageView);
        try {
            int ratio = a.getInt(R.styleable.AdjustableImageView_ratio, 1);
            if (ratio == 1) {
                mAspectRatio = MEDIUM_RATIO;
            } else if (ratio == 2) {
                mAspectRatio = DASHBOARD_RATIO;
            } else {
                mAspectRatio = MEDIUM_RATIO;
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // Fixed Width & Adjustable Height
            int width = widthSize;
            int height = (int) (width / mAspectRatio);
            if (isInScrollingContainer())
                setMeasuredDimension(width, height);
            else
                setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }
}