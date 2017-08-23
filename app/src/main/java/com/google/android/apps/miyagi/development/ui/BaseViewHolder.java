package com.google.android.apps.miyagi.development.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2017-03-07.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder  {

    private List<Animator> mAnimators;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mAnimators = new ArrayList<>();
        scrollAnimators(mAnimators);
    }

    protected void bind(T item) {
        int lastAnimatedPosition = getAdapter().getLastAnimatedPosition();
        int position = getAdapterPosition();
        if (position > lastAnimatedPosition) {
            onAnimated();

            long delay = (position - lastAnimatedPosition) * getAdapter().getStepDelay();

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(mAnimators);
            animatorSet.setStartDelay(delay);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    getAdapter().setLastAnimatedPosition(position);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSet.start();
        }
    }

    protected void onAnimated() {
    }

    protected abstract void scrollAnimators(@NonNull List<Animator> animators);

    protected abstract BaseAdapter<T> getAdapter();

    protected boolean areAllAnimated() {
        return getAdapter().getLastAnimatedPosition() == getAdapter().getItemCount() - 1;
    }
}
