package com.google.android.apps.miyagi.development.ui.components.widget.cards;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lukaszweglinski on 05.12.2016.
 */

class CardContainer {

    private final CardCallback mCallback;
    private final float mCardSpacing;
    private final int mXOffset;
    private final int mRotate;
    private final int mAdapterPosition;
    private View mView;
    private int mPositionWithinViewGroup = -1;
    private boolean mIsAnimationRunning = false;
    private AtomicBoolean mLock = new AtomicBoolean(false);

    /**
     * Instantiates a new Card container.
     *
     * @param view            View showing in stack.
     * @param callback        Animation callback.
     * @param cardSpacing     Cards spacing minus this card offset.
     * @param xOffset         X offset of card in translate animation.
     * @param rotate          Rotate degrees of card in animation.
     * @param adapterPosition position in adapter.
     */
    CardContainer(View view, CardCallback callback, float cardSpacing, int xOffset, int rotate, int adapterPosition) {
        mView = view;
        mCallback = callback;
        mCardSpacing = cardSpacing;
        mXOffset = xOffset;
        mRotate = rotate;
        mAdapterPosition = adapterPosition;
    }

    /**
     * Gets card view.
     *
     * @return the card view
     */
    View getCard() {
        return mView;
    }

    /**
     * Run animation swipe card from top to bottom.
     */
    void swipeCardTop() {
        if (mLock.compareAndSet(false, true)) {
            View view = getCard();

            ValueAnimator translateAnimationYDown = ObjectAnimator.ofFloat(view, "translationY", -(view.getHeight()), mCardSpacing);
            ValueAnimator translateAnimationXDown = ObjectAnimator.ofFloat(view, "translationX", mXOffset, -mCardSpacing);
            ValueAnimator rotateAnimationDown = ObjectAnimator.ofFloat(view, "rotation", mRotate, 0);

            AnimatorSet animatorSetDown = new AnimatorSet();
            long swipeDuration = CardsSelector.CARD_ANIMATION_DURATION;
            animatorSetDown.setDuration(swipeDuration);
            animatorSetDown.playTogether(translateAnimationYDown, translateAnimationXDown, rotateAnimationDown);
            animatorSetDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mCallback.onCardAnimationEnd();
                    mLock.set(false);
                    mIsAnimationRunning = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            ValueAnimator translateAnimationYUp = ObjectAnimator.ofFloat(view, "translationY", 0, -(view.getHeight()));
            ValueAnimator translateAnimationXUp = ObjectAnimator.ofFloat(view, "translationX", 0, mXOffset);
            ValueAnimator rotateAnimationUp = ObjectAnimator.ofFloat(view, "rotation", 0, mRotate);

            AnimatorSet animatorSetUp = new AnimatorSet();
            animatorSetUp.playTogether(translateAnimationYUp, translateAnimationXUp, rotateAnimationUp);
            animatorSetUp.setDuration(swipeDuration);

            animatorSetUp.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mIsAnimationRunning = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animatorSetDown.start();
                    mCallback.onCardAnimationUp();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSetUp.start();
        }
    }

    /**
     * Gets position within view group.
     *
     * @return the position within view group
     */
    int getPositionWithinViewGroup() {
        return mPositionWithinViewGroup;
    }

    /**
     * Sets position of view in stack.
     *
     * @param positionWithinViewGroup the position in stack.
     */
    void setPositionWithinViewGroup(int positionWithinViewGroup) {
        mPositionWithinViewGroup = positionWithinViewGroup;
    }

    /**
     * Return is animation swipe running.
     *
     * @return isAnimationRunning.
     */
    boolean isAnimationRunning() {
        return mIsAnimationRunning;
    }

    AtomicBoolean getLock() {
        return mLock;
    }

    /**
     * Gets adapter position.
     *
     * @return the adapter position.
     */
    public int getAdapterPosition() {
        return mAdapterPosition;
    }
}
