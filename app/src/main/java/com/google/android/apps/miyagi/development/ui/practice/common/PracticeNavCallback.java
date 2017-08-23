package com.google.android.apps.miyagi.development.ui.practice.common;

import android.support.v4.view.ViewPager;

import com.google.android.apps.miyagi.development.utils.Lh;

public interface PracticeNavCallback {

    PracticeNavCallback NULL = new PracticeNavCallback() {

        @Override
        public void setNavigationCallback(Callback callback) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void goToStepRqst(Step step) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void goToStepRqst(Step step, boolean isSuccessful) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void goBack() {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void showInfoButton() {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void hideInfoButton() {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setNextEnable(boolean enable) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setPrevEnable(boolean enable) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setNextButtonVisibility(int visibility) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setPrevButtonVisibility(int visibility) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setNextButtonText(String text) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setNavInstructionVisibility(int visibility) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setViewPager(ViewPager viewPager) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setBackgroundColor(int backgroundColor) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }
    };

    void goBack();

    void showInfoButton();

    void hideInfoButton();

    void goToStepRqst(Step step);

    void goToStepRqst(Step step, boolean isSuccessful);

    void setNextEnable(boolean enable);

    void setPrevEnable(boolean enable);

    void setNextButtonVisibility(int visibility);

    void setPrevButtonVisibility(int visibility);

    void setNextButtonText(String text);

    void setNavInstructionVisibility(int visibility);

    void setViewPager(ViewPager viewPager);

    void setNavigationCallback(Callback callback);

    void setBackgroundColor(int backgroundColor);

    interface Callback {
        Callback EMPTY = new Callback() {

            @Override
            public void onNextClick() {
                Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
            }

            @Override
            public void onPrevClick() {
                Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
            }
        };

        void onNextClick();

        void onPrevClick();
    }

    enum Step {
        INSTRUCTION, SWIPE_SELECTOR, STRIKE_SELECTOR, TAG_SELECTOR, TWITTER, SWITCHES, LARGE,
        FORTUNE_WHEEL, CLOCK, BOOLEAN_SELECTOR, REORDER, SELECT_RIGHT, RESULT_INCORRECT, RESULT_ALMOST, RESULT_CORRECT
    }
}
