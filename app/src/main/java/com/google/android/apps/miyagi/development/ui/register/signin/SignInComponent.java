package com.google.android.apps.miyagi.development.ui.register.signin;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Subcomponent;

/**
 * Created by Paweł on 2017-02-25.
 */
@ViewScope
@Subcomponent(modules = SignInModule.class)
public interface SignInComponent {
    void inject(SignInSelectionFragment fragment);
}
