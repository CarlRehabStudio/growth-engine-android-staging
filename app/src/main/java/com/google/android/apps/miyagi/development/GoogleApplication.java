package com.google.android.apps.miyagi.development;

import android.graphics.Typeface;
import android.support.multidex.MultiDexApplication;

import com.google.android.apps.miyagi.development.dagger.ApplicationComponent;
import com.google.android.apps.miyagi.development.dagger.ConfigModule;
import com.google.android.apps.miyagi.development.dagger.DaggerApplicationComponent;
import com.google.android.apps.miyagi.development.dagger.MainModule;
import com.google.android.apps.miyagi.development.dagger.NetModule;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingComponent;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingModule;
import com.google.android.apps.miyagi.development.ui.register.RegisterComponent;
import com.google.android.apps.miyagi.development.ui.register.RegisterModule;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInComponent;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInModule;
import com.google.android.apps.miyagi.development.utils.Lh;

import com.tsengvn.typekit.Typekit;
import io.realm.Realm;

/**
 * Created by jerzyw on 05.10.2016.
 */

public class GoogleApplication extends MultiDexApplication {

    private static GoogleApplication sInstance;
    private ApplicationComponent mAppComponent;

    private RegisterComponent mRegisterComponent;
    private SignInComponent mSignInComponent;
    private DiagnosticsLoadingComponent mDiagnosticsLoadingComponent;

    public static GoogleApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        mAppComponent = DaggerApplicationComponent.builder()
                .mainModule(new MainModule(this))
                .netModule(new NetModule(this))
                .configModule(new ConfigModule())
                .build();
        mAppComponent.inject(this);
        sInstance = this;

        setupFonts();
    }

    /**
     * Get main application component.
     *
     * @return ApplicationComponent instance.
     */
    public ApplicationComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * Get SignIn component.
     *
     * @return SignInComponent instance.
     */
    public SignInComponent getSignInComponent() {
        return mSignInComponent;
    }

    /**
     * Get Register component.
     *
     * @return RegisterComponent instance.
     */
    public RegisterComponent getRegisterComponent() {
        return mRegisterComponent;
    }

    /**
     * Init SignInComponent if not exists.
     */
    public void createSignInComponent() {
        if (mSignInComponent == null) {
            Lh.d("SignIn", "Create signIn component");
            mSignInComponent = mAppComponent.plus(new SignInModule());
        }
    }

    /**
     * Destroy SignIn component.
     */
    public void releaseSignInComponent() {
        Lh.d("SignIn", "Realeasing SignIn component");
        mSignInComponent = null;
    }

    /**
     * Init RegisterComponent if not exists.
     */
    public void createRegisterComponent() {
        if (mRegisterComponent == null) {
            Lh.d("SignIn", "Create Register component");
            mRegisterComponent = mAppComponent.plus(new RegisterModule());
        }
    }

    /**
     * Destroy Register component.
     */
    public void releaseRegisterComponent() {
        Lh.d("SignIn", "Realeasing Register component");
        mRegisterComponent = null;
    }

    /**
     * Get DiagnosticsLoading component.
     *
     * @return DiagnosticsLoadingComponent instance.
     */
    public DiagnosticsLoadingComponent getDiagnosticsLoadingComponent() {
        return mDiagnosticsLoadingComponent;
    }

    /**
     * Init DiagnosticsLoading if not exists.
     */
    public void createDiagnosticsLoadingComponent() {
        if (mDiagnosticsLoadingComponent == null) {
            Lh.d("SignIn", "Create DiagnosticsLoading component");
            mDiagnosticsLoadingComponent = mAppComponent.plus(new DiagnosticsLoadingModule());
        }
    }

    /**
     * Destroy DiagnosticsLoading component.
     */
    public void releaseDiagnosticsLoadingComponent() {
        Lh.d("SignIn", "Realeasing DiagnosticsLoading component");
        mDiagnosticsLoadingComponent = null;
    }

    private void setupFonts() {
        Typekit.getInstance()
                .add("roboto_light", Typekit.createFromAsset(this, "Roboto-Light.ttf"))
                .add("roboto_medium", Typekit.createFromAsset(this, "Roboto-Medium.ttf"))
                .add("roboto_regular", Typekit.createFromAsset(this, "Roboto-Regular.ttf"));
    }

    /**
     * Reset project fonts to default system ones.
     */
    public void setupSystemFonts() {
        Typekit.getInstance()
                .add("roboto_light", Typeface.DEFAULT)
                .add("roboto_medium", Typeface.DEFAULT_BOLD)
                .add("roboto_regular", Typeface.DEFAULT);
    }
}
