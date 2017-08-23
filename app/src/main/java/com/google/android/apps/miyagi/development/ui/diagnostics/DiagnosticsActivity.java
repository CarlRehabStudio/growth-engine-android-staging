package com.google.android.apps.miyagi.development.ui.diagnostics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.diagnostics.Common;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.validator.DiagnosticsResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.DiagnosticsService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.InkPageIndicator;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.DiagnosticsCallback;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.LearningType;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectGoalsFragment;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.practice.fortunewheel.NonSwipeableViewPager;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import org.parceler.Parcels;
import rx.Subscription;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public class DiagnosticsActivity extends BaseActivity implements DiagnosticsCallback {

    @Inject DiagnosticsService mDiagnosticsService;
    @Inject AppColors mAppColors;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject Lazy<SignOutUserHelper> mSignOutUserHelper;
    @Inject Navigator mNavigator;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.diagnostics_view_pager) NonSwipeableViewPager mViewPager;
    @BindView(R.id.diagnostics_container) FrameLayout mRootLayout;

    @BindView(R.id.button_prev) NavigationButton mNavBtnSkip;
    @BindView(R.id.button_next) NavigationButton mNavBtnNext;

    @BindView(R.id.bottom_nav_dot_pager_indicator) InkPageIndicator mPageIndicator;

    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;
    private DiagnosticsPagerAdapter mPagerAdapter;

    private DiagnosticsResponseData mDiagnosticsData;
    private String mPersona;
    private LearningType mLearningType;
    private List<Integer> mGoals;

    private Subscription mApiSubscription;

    /**
     * Creates new instance of DiagnosticsActivity.
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, DiagnosticsActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.diagnostics_activity);

        ButterKnife.bind(this);
        mRootLayout.setBackgroundColor(mAppColors.getMainBackgroundColor());

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.diagnostics_preloader));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.diagnostics_error));
        mErrorHelper.setOnActionClickListener(this::getRemoteData);
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);

        mNavBtnSkip.setCompoundDrawables(null, null, null, null);
        mNavBtnSkip.setOnClickListener(v -> skipDiagnostics());

        mNavBtnNext.disable();
        mNavBtnNext.setOnClickListener(v -> onNavBtnNextClick());

        if (savedInstanceState == null) {
            getRemoteData();
        } else {
            getSavedData(savedInstanceState);
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void getSavedData(Bundle savedInstanceState) {
        mPersona = savedInstanceState.getString(BundleKey.PERSONA);
        String learningType = savedInstanceState.getString(BundleKey.LEARNING_TYPE);
        if (learningType != null) {
            if (learningType.equals(LearningType.CERTIFICATION.getType())) {
                mLearningType = LearningType.CERTIFICATION;
            } else if (learningType.equals(LearningType.PLAN.getType())) {
                mLearningType = LearningType.PLAN;
            }
        }
        mGoals = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.GOALS));

        bindData();

        int currentPageIndex = savedInstanceState.getInt(BundleKey.CURRENT_PAGE_INDEX);
        if (currentPageIndex > 0) {
            mViewPager.setCurrentItem(currentPageIndex - 1);
        }

        if (mPersona != null && mViewPager.getCurrentItem() == 0
                || mLearningType != null && mViewPager.getCurrentItem() == 1
                || mGoals != null && mGoals.size() > 0 && mViewPager.getCurrentItem() == 2) {
            mNavBtnNext.enable();
        } else {
            mNavBtnNext.disable();
        }
    }

    private void onNavBtnNextClick() {
        int currentItem = mViewPager.getCurrentItem();

        String screenType = getString(mPagerAdapter.getItemScreenName(currentItem));

        mAnalyticsHelper.trackEvent(
                screenType,
                getString(R.string.event_category_diagnostics),
                getString(R.string.event_action_button),
                mNavBtnNext.getText().toString());

        if (currentItem == 0) {
            mViewPager.setCurrentItem(currentItem + 1);
            mNavBtnNext.disable();
        } else if (currentItem == 1) {
            if (mLearningType.getType().equals(LearningType.CERTIFICATION.getType())) {
                mNavigator.navigateToLoadingWithSubmitCertification(this);
                finish();
            } else {
                mViewPager.setCurrentItem(currentItem + 1);
                mNavBtnNext.disable();
            }
        } else if (currentItem == 2) {
            mNavigator.navigateToLoadingWithSubmit(this, mPersona, mGoals);
            finish();
        } else {
            mNavigator.navigateToLoadingWithSkip(this);
            finish();
        }
    }

    private void getRemoteData() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mDiagnosticsService.getDiagnostics()
                .map(DiagnosticsResponseValidator::validate)
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onDiagnosticsReceived, this::onDataError);
    }

    private void onDiagnosticsReceived(DiagnosticsResponse diagnosticsResponse) {
        mCurrentSessionCache.setDiagnosticsResponseData(diagnosticsResponse.getResponseData());
        bindData();
    }

    private void bindData() {
        mDiagnosticsData = mCurrentSessionCache.getDiagnosticsResponseData();
        if (mDiagnosticsData == null || mDiagnosticsData.getCommon() == null) {
            onBackPressed();
            return;
        }

        mPagerAdapter = new DiagnosticsPagerAdapter(getSupportFragmentManager(), mDiagnosticsData);
        mPagerAdapter.setDiagnosticsCallback(this);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mPagerAdapter);
        mPageIndicator.setViewPager(mViewPager);

        Common commonData = mDiagnosticsData.getCommon();
        mNavBtnSkip.setText(commonData.getButtonSkipText());
        mNavBtnNext.setText(commonData.getButtonNextText());
    }

    private void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedOut(throwable);
        mErrorHelper.show();
    }

    private void onSubscribe() {
        mErrorHelper.hide();
        mPreloaderHelper.show();
    }

    private void onTerminate() {
        mPreloaderHelper.hide();
    }

    public void skipDiagnostics() {
        int currentItem = mViewPager.getCurrentItem();
        String screenType = getString(mPagerAdapter.getItemScreenName(currentItem));

        mAnalyticsHelper.trackEvent(
                screenType,
                getString(R.string.event_category_diagnostics),
                getString(R.string.event_action_button),
                mNavBtnSkip.getText().toString());

        mNavigator.navigateToLoadingWithSkip(this);
        finish();
    }

    @Override
    public void enableNavBtnNext(boolean enabled) {
        if (enabled) {
            mNavBtnNext.enable();
        } else {
            mNavBtnNext.disable();
        }
    }

    @Override
    public void selectPersona(String persona) {
        mPersona = persona;

        SelectGoalsFragment fragment = (SelectGoalsFragment) mPagerAdapter.instantiateItem(mViewPager, 2);
        if (fragment != null && mPersona != null) {
            fragment.setPersona(mPersona);
        }
    }

    @Override
    public void selectLearningType(LearningType type) {
        mLearningType = type;
    }

    @Override
    public void selectGoals(List<Integer> goals) {
        mGoals = goals;
    }

    @Override
    public void onBackPressed() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
        mSignOutUserHelper.get().signOut();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPersona != null) {
            outState.putString(BundleKey.PERSONA, mPersona);
        }
        if (mLearningType != null) {
            outState.putString(BundleKey.LEARNING_TYPE, mLearningType.getType());
        }
        outState.putInt(BundleKey.CURRENT_PAGE_INDEX, mViewPager.getCurrentItem() + 1);
        if (mGoals != null) {
            outState.putParcelable(BundleKey.GOALS, Parcels.wrap(mGoals));
        }
        super.onSaveInstanceState(outState);
    }

    interface BundleKey {
        String PERSONA = "PERSONA";
        String LEARNING_TYPE = "LEARNING_TYPE";
        String CURRENT_PAGE_INDEX = "CURRENT_PAGE_INDEX";
        String GOALS = "GOALS";
    }
}
