package com.google.android.apps.miyagi.development.ui.diagnostics.loading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.diagnostics.Loading;
import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingPrefs;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingType;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity.Arg.FROM_DIAGNOSTICS;
import dagger.Lazy;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcinarciszew on 08.02.2017.
 */

public class DiagnosticsLoadingActivity extends BaseActivity<DiagnosticsLoadingContract.Presenter> implements DiagnosticsLoadingContract.View {

    @Inject AppColors mAppColors;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;
    @Inject Lazy<SignOutUserHelper> mSignOutUserHelper;
    @Inject Lazy<OnboardingPrefs> mOnboardingPrefs;
    @Inject DiagnosticsLoadingPresenter mPresenter;

    @BindView(R.id.diagnostics_container) FrameLayout mRootLayout;

    @BindView(R.id.diagnostics_loading_image) ImageView mImage;
    @BindView(R.id.diagnostics_loading_header) TextView mHeaderText;
    @BindView(R.id.diagnostics_loading_sub_header) TextView mSubHeaderText;

    private ErrorScreenHelper mErrorHelper;
    private Loading mLoadingData;
    private List<Integer> mGoals;
    private String mXsrfToken;
    private Type mType;
    private String mPersona;

    public static Intent createIntentWithSubmit(Context context, String persona, List<Integer> selectedStepThreeOptions) {
        Bundle args = new Bundle();
        args.putString(BundleKey.PERSONA, persona);
        args.putParcelable(BundleKey.GOALS, Parcels.wrap(selectedStepThreeOptions));
        args.putParcelable(BundleKey.POST_TYPE, Parcels.wrap(Type.SUBMIT));

        Intent intent = new Intent(context, DiagnosticsLoadingActivity.class);
        intent.putExtras(args);

        return intent;
    }

    public static Intent createIntentWithSkip(Context context) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKey.POST_TYPE, Parcels.wrap(Type.SKIP));

        Intent intent = new Intent(context, DiagnosticsLoadingActivity.class);
        intent.putExtras(args);

        return intent;
    }

    public static Intent createIntentWithSubmitCertification(Context context) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKey.POST_TYPE, Parcels.wrap(Type.CERTIFICATION));

        Intent intent = new Intent(context, DiagnosticsLoadingActivity.class);
        intent.putExtras(args);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.diagnostics_loading_activity);

        ButterKnife.bind(this);

        mRootLayout.setBackgroundColor(mAppColors.getMainBackgroundColor());

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mGoals = Parcels.unwrap(extras.getParcelable(BundleKey.GOALS));
                mType = Parcels.unwrap(extras.getParcelable(BundleKey.POST_TYPE));
                mPersona = extras.getString(BundleKey.PERSONA);
            }
        }

        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.diagnostics_loading_error));
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);
        initUi();
        performAction();
    }

    @Override
    public void onBackPressed() {
        mSignOutUserHelper.get().signOut();
        finish();
    }

    private void initUi() {
        DiagnosticsResponseData responseData = mCurrentSessionCache.getDiagnosticsResponseData();
        if (responseData == null || responseData.getLoading() == null) {
            onBackPressed();
            return;
        }

        mLoadingData = responseData.getLoading();
        mXsrfToken = responseData.getXsrfToken();

        if (mLoadingData != null) {
            mImage.setContentDescription(mLoadingData.getImageAria());
            mHeaderText.setText(mLoadingData.getTitle());
            mSubHeaderText.setText(mLoadingData.getSubhead());
        }

        animateScreen();
    }

    private void animateScreen() {
        mScreenAnimationHelper.addFlatView(mHeaderText);
        mScreenAnimationHelper.addFlatView(mSubHeaderText);
        mScreenAnimationHelper.addFlatView(mImage);

        mScreenAnimationHelper.animateScreen();
    }

    private void performAction() {
        if (mType == Type.SUBMIT) {
            mPresenter.submitDiagnostics(mPersona, mGoals, mXsrfToken);
            mErrorHelper.setOnActionClickListener(() -> mPresenter.submitDiagnostics(mPersona, mGoals, mXsrfToken));
        } else if (mType == Type.SKIP) {
            mPresenter.skipDiagnostics(mXsrfToken);
            mErrorHelper.setOnActionClickListener(() -> mPresenter.skipDiagnostics(mXsrfToken));
        } else if (mType == Type.CERTIFICATION) {
            mPresenter.submitCertification(mXsrfToken);
            mErrorHelper.setOnActionClickListener(() -> mPresenter.submitCertification(mXsrfToken));
        }
    }

    @Override
    public void onSuccess(BaseResponse r) {
        startOnboarding();
    }

    @Override
    public void onSubscribe() {
        mErrorHelper.hide();
    }

    @Override
    public void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedOut(throwable);
        mErrorHelper.show();
    }

    @Override
    public void startOnboarding() {
        if (mOnboardingPrefs.get().isOnboardingRequired(OnboardingType.DASHBOARD)) {
            boolean fromDiagnostics = getIntent().getBooleanExtra(FROM_DIAGNOSTICS, false);
            startActivity(OnboardingActivity.createIntent(this, OnboardingType.DASHBOARD, fromDiagnostics));
        } else {
            startActivity(DashboardActivity.createIntent(this, false));
        }
        finish();
    }

    @Override
    public void onScreenEnter() {
        ((GoogleApplication) getApplication()).createDiagnosticsLoadingComponent();
    }

    @Override
    public void onScreenExit() {
        ((GoogleApplication) getApplication()).releaseDiagnosticsLoadingComponent();
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) getApplication()).getDiagnosticsLoadingComponent().inject(this);
    }

    @Override
    public DiagnosticsLoadingPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            onScreenExit();
        }
    }

    @Parcel
    protected enum Type {
        SKIP, SUBMIT, CERTIFICATION
    }

    interface BundleKey {
        String PERSONA = "PERSONA";
        String POST_TYPE = "POST_TYPE";
        String GOALS = "GOALS";
    }
}
