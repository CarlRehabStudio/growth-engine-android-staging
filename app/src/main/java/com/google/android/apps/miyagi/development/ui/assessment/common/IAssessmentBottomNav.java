package com.google.android.apps.miyagi.development.ui.assessment.common;

import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 15.03.2017.
 */

public interface IAssessmentBottomNav {

    void setNextButtonText(String text);

    void setPrevButtonText(String text);

    void setBackgroundColor(int color);

    void setNextEnable(boolean enable);

    void setPrevEnable(boolean enable);

    void setLabelText(String label);

    void setLabelVisibility(int visibility);

    void setNextButtonVisibility(int visibility);

    void setPrevButtonVisibility(int visibility);

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
}
