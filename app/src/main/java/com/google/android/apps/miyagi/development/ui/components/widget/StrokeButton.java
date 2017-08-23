package com.google.android.apps.miyagi.development.ui.components.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

/**
 * Created by lukaszweglinski on 07.12.2016.
 */

public class StrokeButton extends AppCompatButton {

    private static final int DEFAULT_STROKE_WIDTH_DP = 1;
    private static final int DEFAULT_BUTTON_RADIUS_DP = 2;

    private int mStrokeWidth;
    private int mCornerRadius;

    private int mFillColor;
    private int mStrokeColor;

    private GradientDrawable mButtonDrawable;

    public StrokeButton(Context context) {
        this(context, null, 0);
    }

    public StrokeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StrokeButton, 0, 0);
        try {
            mStrokeWidth = (int) attributes.getDimension(R.styleable.StrokeButton_stroke_width, ViewUtils.dp2px(context, DEFAULT_STROKE_WIDTH_DP));
            mCornerRadius = (int) attributes.getDimension(R.styleable.StrokeButton_corner_radius, ViewUtils.dp2px(context, DEFAULT_BUTTON_RADIUS_DP));
        } finally {
            attributes.recycle();
        }

        mButtonDrawable = new GradientDrawable();
        mButtonDrawable.setCornerRadius(mCornerRadius);
        setBackground(mButtonDrawable);
    }

    /**
     * Sets stroke color.
     *
     * @param color - stroke color.
     */
    public void setStrokeColor(int color) {
        mStrokeColor = color;
        mButtonDrawable.setStroke(mStrokeWidth, mStrokeColor);
        setBackground(mButtonDrawable);
    }

    /**
     * Sets fill color.
     *
     * @param color - fill color.
     */
    public void setFillColor(int color) {
        mFillColor = color;
        mButtonDrawable.setColor(mFillColor);
        setBackground(mButtonDrawable);
    }
}
