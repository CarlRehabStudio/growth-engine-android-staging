package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.view.View;

/**
 * Created by lukaszweglinski on 16.11.2016.
 */
public class ColorizeAnimator {

    private static final long DEFAULT_ANIMATION_DURATION = 400;

    /**
     * Animate between View background color and colorTo.
     *
     * @param viewToAnimateItBackground the view to animate it background.
     * @param colorTo                   color to animate to.
     */
    public static void animateBetweenColors(final View viewToAnimateItBackground, final int colorTo) {
        if (viewToAnimateItBackground.getBackground() instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) viewToAnimateItBackground.getBackground();
            animateBetweenColors(viewToAnimateItBackground, colorDrawable.getColor(), colorTo, DEFAULT_ANIMATION_DURATION);
        }
    }

    /**
     * Animate between CardView  card background color and colorTo.
     *
     * @param viewToAnimateItBackground the view to animate it background.
     * @param colorTo                   color to animate to.
     */
    public static void animateBetweenColors(final CardView viewToAnimateItBackground, final int colorTo) {
        int colorFrom = viewToAnimateItBackground.getCardBackgroundColor().getDefaultColor();
        animateBetweenColors(viewToAnimateItBackground, colorFrom, colorTo, DEFAULT_ANIMATION_DURATION);
    }

    /**
     * Animate View background between colors.
     *
     * @param viewToAnimateItBackground the view to animate it background.
     * @param colorFrom                 color to animate from.
     * @param colorTo                   color to animate to.
     * @param durationInMs              duration of animation in ms.
     */
    public static void animateBetweenColors(final View viewToAnimateItBackground, final int colorFrom, final int colorTo, final long durationInMs) {
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> viewToAnimateItBackground.setBackgroundColor((Integer) animator.getAnimatedValue()));

        colorAnimation.setDuration(durationInMs);
        colorAnimation.start();
    }

    /**
     * Animate CardView background between colors.
     *
     * @param viewToAnimateItBackground the view to animate it background.
     * @param colorFrom                 color to animate from.
     * @param colorTo                   color to animate to.
     * @param durationInMs              duration of animation in ms.
     */
    public static void animateBetweenColors(final CardView viewToAnimateItBackground, final int colorFrom, final int colorTo, final long durationInMs) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> viewToAnimateItBackground.setCardBackgroundColor((Integer) animator.getAnimatedValue()));

        colorAnimation.setDuration(durationInMs);
        colorAnimation.start();
    }

    /**
     * Animate View alpha.
     *
     * @param viewToAnimateItAlpha the view to animate it alpha.
     * @param toAlpha              alpha to animate from.
     */
    public static void animateAlpha(final View viewToAnimateItAlpha, final float toAlpha) {
        ValueAnimator colorAnimation = ValueAnimator.ofFloat(viewToAnimateItAlpha.getAlpha(), toAlpha);
        colorAnimation.addUpdateListener(animator -> viewToAnimateItAlpha.setAlpha((Float) animator.getAnimatedValue()));

        colorAnimation.setDuration(DEFAULT_ANIMATION_DURATION);
        colorAnimation.start();
    }

    /**
     * Animate View alpha.
     *
     * @param viewToAnimateItAlpha the view to animate it alpha.
     * @param fromAlpha            alpha to animate from.
     * @param toAlpha              alpha to animate to.
     * @param durationInMs         duration of animation in ms.
     */
    public static void animateAlpha(final View viewToAnimateItAlpha, final float fromAlpha, final float toAlpha, final long durationInMs) {
        ValueAnimator colorAnimation = ValueAnimator.ofFloat(fromAlpha, toAlpha);
        colorAnimation.addUpdateListener(animator -> viewToAnimateItAlpha.setAlpha((Float) animator.getAnimatedValue()));

        colorAnimation.setDuration(durationInMs);
        colorAnimation.start();
    }
}
