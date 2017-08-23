package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

/**
 * Created by lukaszweglinski on 09.11.2016.
 */

public class StrikeThroughLayout extends FrameLayout {

    private static final int DEFAULT_LINE_WIDTH_DP = 4;
    private static final int DEFAULT_STRIKE_HEIGHT_DP = 30;
    private static final int DEFAULT_LINE_COLOR = 0xFFE53935;
    private static final int DEFAULT_DURATION = 200;

    private boolean mChecked = false;
    private Paint mLinePaint;

    private float mScaleVal = 0f;
    private int mStrikeHeight;

    private ValueAnimator mCurrentAnimator;
    private Animator.AnimatorListener mCurrentAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mCurrentAnimator = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public StrikeThroughLayout(Context context) {
        this(context, null);
    }

    public StrikeThroughLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrikeThroughLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StrikeThroughLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray att = getContext().obtainStyledAttributes(attrs, R.styleable.StrikeThroughLayout);

        int lineWidth;
        int lineColor;
        try {
            lineWidth = att.getDimensionPixelSize(R.styleable.StrikeThroughLayout_lineWidth, ViewUtils.dp2px(getContext(), DEFAULT_LINE_WIDTH_DP));
            lineColor = att.getColor(R.styleable.StrikeThroughLayout_lineColor, DEFAULT_LINE_COLOR);
            mStrikeHeight = att.getDimensionPixelSize(R.styleable.StrikeThroughLayout_strikeHeight, ViewUtils.dp2px(getContext(), DEFAULT_STRIKE_HEIGHT_DP)) / 2;
        } finally {
            att.recycle();
        }

        setWillNotDraw(false);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(lineColor);
        reset();
    }

    private void startUncheckedAnimation() {
        UncheckedAction action = new UncheckedAction();
        postOnAnimation(action);
    }

    private void startCheckedAnimation() {
        CheckedAction action = new CheckedAction();
        postOnAnimation(action);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStroke(canvas);
    }

    private void drawStroke(Canvas canvas) {
        float stopX = getWidth() * mScaleVal;

        float center = getHeight() / 2;
        float startY = center + mStrikeHeight;
        float stopY = startY - ((2 * mStrikeHeight * mScaleVal));

        canvas.drawLine(0, startY, stopX, stopY, mLinePaint);
    }

    public boolean isChecked() {
        return mChecked;
    }

    /**
     * @param checked - set checked value.
     */
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            reset();
            invalidate();
        }
    }

    /**
     * checked with animation.
     *
     * @param checked checked.
     * @param animate change with animation.
     */
    public void setChecked(boolean checked, boolean animate) {
        if (animate) {
            if (checked != mChecked) {
                if (checked) {
                    startCheckedAnimation();
                } else {
                    startUncheckedAnimation();
                }
            }
            mChecked = checked;
        } else {
            this.setChecked(checked);
        }
    }

    public void toggle() {
        setChecked(!isChecked(), true);
    }

    private void reset() {
        mScaleVal = isChecked() ? 1.0f : 0f;
    }

    private class CheckedAction implements Runnable {
        public CheckedAction() {
        }

        @Override
        public void run() {
            if (mCurrentAnimator != null) {
                //cancel also calls onAnimationEnd
                mCurrentAnimator.removeAllListeners();
                mCurrentAnimator.cancel();
            }

            mCurrentAnimator = ValueAnimator.ofFloat(0f, 1.0f);
            mCurrentAnimator.setInterpolator(new LinearInterpolator());
            mCurrentAnimator.setDuration(DEFAULT_DURATION);
            mCurrentAnimator.addUpdateListener(animation -> {
                mScaleVal = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }

    private class UncheckedAction implements Runnable {
        public UncheckedAction() {
        }

        @Override
        public void run() {
            if (mCurrentAnimator != null) {
                //cancel also calls onAnimationEnd
                mCurrentAnimator.removeAllListeners();
                mCurrentAnimator.cancel();
            }

            mCurrentAnimator = ValueAnimator.ofFloat(1.0f, 0f);
            mCurrentAnimator.setInterpolator(new LinearInterpolator());
            mCurrentAnimator.setDuration(DEFAULT_DURATION);
            mCurrentAnimator.addUpdateListener(animation -> {
                mScaleVal = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }
}
