package com.google.android.apps.miyagi.development.ui.diagnostics.common;

import java.util.List;

/**
 * Created by marcinarciszew on 08.02.2017.
 */

public interface DiagnosticsCallback {

    void enableNavBtnNext(boolean enabled);

    void selectPersona(String persona);

    void selectLearningType(LearningType type);

    void selectGoals(List<Integer> goals);
}
