package com.google.android.apps.miyagi.development.dagger;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.inject.Scope;

/**
 * Created by Paweł on 2017-02-24.
 */

/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the activity to be memorized in the
 * correct component.
 */
@Scope
@Retention(RUNTIME)
public @interface ViewScope {
}