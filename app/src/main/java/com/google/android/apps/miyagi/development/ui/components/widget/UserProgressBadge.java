package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;

/**
 * Created by Lukasz on 22.01.2017.
 */

public class UserProgressBadge extends FrameLayout {

    private static final long ANIMATION_DURATION = 1500;

    private ImageView mIcon;
    private TextView mProgressLabel;

    private int mProgress;
    private int mColor;

    private ValueAnimator mCurrentProgressAnimation;
    private Animator.AnimatorListener mCurrentAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mCurrentProgressAnimation = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public UserProgressBadge(@NonNull Context context) {
        super(context);
        initView();
    }

    public UserProgressBadge(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public UserProgressBadge(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.dashboard_action_layout_user_progress, null);
        addView(view);
        mIcon = (ImageView) findViewById(R.id.user_progress_icon);
        mProgressLabel = (TextView) findViewById(R.id.label_progress);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mProgressLabel.setText(String.valueOf(mProgress));
    }

    /**
     * Sets progress animation with given progress value.
     */
    public void setProgressAnimation(int progress) {
        mProgress = progress;

        if (mCurrentProgressAnimation != null) {
            mCurrentProgressAnimation.removeAllListeners();
            mCurrentProgressAnimation.cancel();
        }

        mCurrentProgressAnimation = ObjectAnimator.ofInt(0, progress);
        mCurrentProgressAnimation.setInterpolator(new FastOutSlowInInterpolator());
        mCurrentProgressAnimation.setDuration(ANIMATION_DURATION);
        mCurrentProgressAnimation.addUpdateListener(animation -> {
            mProgress = (int) animation.getAnimatedValue();
            mProgressLabel.setText(String.valueOf(mProgress));
        });
        mCurrentProgressAnimation.addListener(mCurrentAnimatorListener);
        mCurrentProgressAnimation.start();
    }

    public void setColor(int color) {
        mColor = color;
        mIcon.setColorFilter(mColor);
    }
}
