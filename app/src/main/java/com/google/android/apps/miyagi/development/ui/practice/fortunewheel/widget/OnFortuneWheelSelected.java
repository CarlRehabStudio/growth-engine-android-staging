package com.google.android.apps.miyagi.development.ui.practice.fortunewheel.widget;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelAnswerOption;

/**
 * Created by Paweł on 2017-02-21.
 */

public interface OnFortuneWheelSelected {
    void onItemSelected(FortuneWheelAnswerOption option, int pageIndex);
}
