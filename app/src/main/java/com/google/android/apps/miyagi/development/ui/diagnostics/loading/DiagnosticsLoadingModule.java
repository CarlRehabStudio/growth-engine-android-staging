package com.google.android.apps.miyagi.development.ui.diagnostics.loading;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lukaszweglinski on 08.03.2017.
 */

@Module
public class DiagnosticsLoadingModule {

    @Provides
    @ViewScope
    DiagnosticsLoadingPresenter provideDiagnosticsLoadingPresenter() {
        return new DiagnosticsLoadingPresenter();
    }
}
