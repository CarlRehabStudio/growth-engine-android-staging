package com.google.android.apps.miyagi.development.ui.assessment.navigation;

import com.google.android.apps.miyagi.development.ui.assessment.common.IAssessmentBottomNav;
import com.google.android.apps.miyagi.development.utils.Lh;

import org.parceler.Parcel;

import java.util.Map;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public interface Navigation extends IAssessmentBottomNav {

    Navigation EMPTY = new Navigation() {

        @Override
        public void goTo(Step step) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void goBack() {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void submitAnswers(Map<String, Integer> answers) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setNextButtonText(String text) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setPrevButtonText(String text) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setBackgroundColor(int color) {
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
        public void setLabelText(String label) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void setLabelVisibility(int visibility) {
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
    };

    void goTo(Step step);

    void goBack();

    void submitAnswers(Map<String, Integer> answers);

    @Parcel
    enum Step {
        INSTRUCTION, TEST, TEST_SUBMIT, RESULT, BADGE
    }
}
