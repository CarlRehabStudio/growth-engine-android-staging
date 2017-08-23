package com.google.android.apps.miyagi.development.ui.register.signin;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Paweł on 2017-02-25.
 */
@Module
public class SignInModule {

    @Provides
    @ViewScope
    SignInPresenter provideSignInPresenter() {
        return new SignInPresenter();
    }
}
