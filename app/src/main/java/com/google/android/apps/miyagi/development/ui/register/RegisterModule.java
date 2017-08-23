package com.google.android.apps.miyagi.development.ui.register;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Paweł on 2017-02-25.
 */
@Module
public class RegisterModule {

    @Provides
    @ViewScope
    RegisterPresenter provideRegisterPresenter() {
        return new RegisterPresenter();
    }
}
