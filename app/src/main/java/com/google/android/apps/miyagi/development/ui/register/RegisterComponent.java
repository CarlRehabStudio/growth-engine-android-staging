package com.google.android.apps.miyagi.development.ui.register;

import com.google.android.apps.miyagi.development.dagger.ViewScope;

import dagger.Subcomponent;

/**
 * Created by Paweł on 2017-02-25.
 */
@ViewScope
@Subcomponent(modules = RegisterModule.class)
public interface RegisterComponent {
    void inject(RegisterActivity activity);
}
