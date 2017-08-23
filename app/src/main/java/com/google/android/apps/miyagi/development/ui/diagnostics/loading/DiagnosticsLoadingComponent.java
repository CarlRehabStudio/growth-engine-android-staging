package com.google.android.apps.miyagi.development.ui.diagnostics.loading;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Subcomponent;

/**
 * Created by lukaszweglinski on 08.03.2017.
 */

@ViewScope
@Subcomponent(modules = DiagnosticsLoadingModule.class)
public interface DiagnosticsLoadingComponent {
    void inject(DiagnosticsLoadingActivity activity);
}