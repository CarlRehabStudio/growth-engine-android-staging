package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by jerzyw on 24.10.2016.
 */

public class ExpandableLinearLayout extends LinearLayout {
    public static final int DEFAULT_ANIMATION_DURATION_MS = 300;

    private int mExpandedSize;

    private boolean mIsCollapsed;

    private StateChangeListener mOnStateChangeListener = StateChangeListener.NULL;
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

    public ExpandableLinearLayout(Context context) {
        super(context);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isCollapsed() {
        return mIsCollapsed;
    }

    /**
     * Expand or collapse layout depends on current state.
     *
     * @param durationMs - animation duration in milliseconds.
     */
    public void toggle(long durationMs) {
        if (isCollapsed()) {
            expand(durationMs);
        } else {
            collapse(durationMs);
        }
    }

    /**
     * Expand or collapse layout depends on current state. Uses @see DEFAULT_ANIMATION_DURATION_MS animation duration.
     */
    public void toggle() {
        toggle(DEFAULT_ANIMATION_DURATION_MS);
    }

    /**
     * Collapse layout. Uses @see DEFAULT_ANIMATION_DURATION_MS animation duration.
     */
    public void collapse() {
        collapse(DEFAULT_ANIMATION_DURATION_MS);
    }

    /**
     * Collapse layout.
     *
     * @param durationMs - animation duration in milliseconds.
     */
    public void collapse(long durationMs) {
        mIsCollapsed = true;
        //add to message queue (will be called after first onMeasure call).
        CollapseAction action = new CollapseAction(durationMs);
        postOnAnimation(action);
    }

    /**
     * Expand layout. Uses @see DEFAULT_ANIMATION_DURATION_MS animation duration.
     */
    public void expand() {
        expand(DEFAULT_ANIMATION_DURATION_MS);
    }

    /**
     * Expand layout.
     *
     * @param durationMs - animation duration in milliseconds.
     */
    public void expand(long durationMs) {
        mIsCollapsed = false;
        //add to message queue (will be called after first onMeasure call).
        ExpandAction action = new ExpandAction(durationMs);
        postOnAnimation(action);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isVertical()) {
            int heightMeasureSpecForChildren = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            super.onMeasure(widthMeasureSpec, heightMeasureSpecForChildren);
            mExpandedSize = getMeasuredHeight();
        } else {
            int widthMeasureSpecForChildren = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            super.onMeasure(widthMeasureSpecForChildren, heightMeasureSpec);
            mExpandedSize = getMeasuredWidth();
        }

        if(mIsCollapsed) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean isVertical() {
        return getOrientation() == LinearLayout.VERTICAL;
    }

    /**
     * Sets collapse / expande state change listener.
     *
     * @param listener - collapse / expand state change listener.
     */
    public void setOnStateChangeListener(StateChangeListener listener) {
        if (listener != null) {
            mOnStateChangeListener = listener;
        } else {
            mOnStateChangeListener = StateChangeListener.NULL;
        }
    }

    public interface StateChangeListener {
        StateChangeListener NULL = new StateChangeListener() {

            @Override
            public void onStateChanged() {
            }
        };

        void onStateChanged();
    }

    //..............................................................................................

    private class CollapseAction implements Runnable {

        private long mDurationMs;

        public CollapseAction(long durationMs) {
            mDurationMs = durationMs;
        }

        @Override
        public void run() {
            //another animation can be in progress - cancel in.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.removeAllListeners();
                mCurrentAnimator.cancel();
            }
            mCurrentAnimator = ValueAnimator.ofInt(getHeight(), 0);
            mCurrentAnimator.setDuration(mDurationMs);
            mCurrentAnimator.addUpdateListener(animator -> {
                if (isVertical()) {
                    getLayoutParams().height = (int) animator.getAnimatedValue();
                } else {
                    getLayoutParams().width = (int) animator.getAnimatedValue();
                }
                requestLayout();
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
            mOnStateChangeListener.onStateChanged();
        }
    }

    private class ExpandAction implements Runnable {

        private long mDurationMs;

        public ExpandAction(long durationMs) {
            mDurationMs = durationMs;
        }

        @Override
        public void run() {
            int currentHeight = getHeight();
            //another animation can be in progress - cancel in.
            if (mCurrentAnimator != null) {
                //cancel also calls onAnimationEnd
                mCurrentAnimator.removeAllListeners();
                mCurrentAnimator.cancel();
            }
            mCurrentAnimator = ValueAnimator.ofInt(currentHeight, mExpandedSize);
            mCurrentAnimator.setDuration(mDurationMs);
            mCurrentAnimator.addUpdateListener(animator -> {
                int animatedValue = (int) animator.getAnimatedValue();
                if (isVertical()) {
                    getLayoutParams().height = animatedValue;
                } else {
                    getLayoutParams().width = animatedValue;
                }
                requestLayout();
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
            mOnStateChangeListener.onStateChanged();
        }
    }

}
