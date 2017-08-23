package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;

/**
 * Created by lukaszweglinski on 23.11.2016.
 */

public class ScalableImageView extends AppCompatImageView {

    private long mDuration = 300;
    private float mUpScale = 1.0f;
    private float mDownScale = 0.75f;
    private float mUpAlpha = 1.0f;
    private float mDownAlpha = 0.5f;

    private boolean mChecked;

    private AnimatorSet mCurrentAnimator;
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

    public ScalableImageView(Context context) {
        super(context);
        init();
    }

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        updateScale();
    }

    public void toggle() {
        setChecked(!isChecked(), true);
    }

    public boolean isChecked() {
        return mChecked;
    }

    /**
     * @param checked - set checked value without animation.
     */
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            updateScale();
            updateAlpha();
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
            mChecked = checked;
            if (checked) {
                startCheckedAnimation();
            } else {
                startUncheckedAnimation();
            }
        } else {
            this.setChecked(checked);
        }
    }

    private void updateScale() {
        if (isChecked()) {
            setScaleX(mUpScale);
            setScaleY(mUpScale);
        } else {
            setScaleX(mDownScale);
            setScaleY(mDownScale);
        }
    }

    private void updateAlpha() {
        if (isChecked()) {
            setAlpha(mUpAlpha);
        } else {
            setAlpha(mDownAlpha);
        }
    }

    private void startUncheckedAnimation() {
        ScaleDownAction action = new ScaleDownAction();
        postOnAnimation(action);
    }

    private void startCheckedAnimation() {
        ScaleUpAction action = new ScaleUpAction();
        postOnAnimation(action);
    }

    public long getDuration() {
        return mDuration;
    }

    private void cancelAnimation() {
        if (mCurrentAnimator != null) {
            //cancel also calls onAnimationEnd
            mCurrentAnimator.removeAllListeners();
            mCurrentAnimator.cancel();
        }
    }

    private class ScaleUpAction implements Runnable {
        @Override
        public void run() {
            cancelAnimation();

            mCurrentAnimator = new AnimatorSet();

            mCurrentAnimator.playTogether(
                    ObjectAnimator.ofFloat(ScalableImageView.this, "scaleX", mUpScale),
                    ObjectAnimator.ofFloat(ScalableImageView.this, "scaleY", mUpScale),
                    ObjectAnimator.ofFloat(ScalableImageView.this, "alpha", mUpAlpha)
            );

            mCurrentAnimator.setInterpolator(new OvershootInterpolator());
            mCurrentAnimator.setDuration(getDuration());
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }

    private class ScaleDownAction implements Runnable {
        @Override
        public void run() {
            cancelAnimation();
            mCurrentAnimator = new AnimatorSet();

            mCurrentAnimator.playTogether(
                    ObjectAnimator.ofFloat(ScalableImageView.this, "scaleX", mDownScale),
                    ObjectAnimator.ofFloat(ScalableImageView.this, "scaleY", mDownScale),
                    ObjectAnimator.ofFloat(ScalableImageView.this, "alpha", mDownAlpha)
            );

            mCurrentAnimator.setInterpolator(new OvershootInterpolator());
            mCurrentAnimator.setDuration(getDuration());
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }
}
