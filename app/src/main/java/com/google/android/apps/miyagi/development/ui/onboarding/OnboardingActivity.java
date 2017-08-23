package com.google.android.apps.miyagi.development.ui.onboarding;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.onboarding.OnboardingStep;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.validator.OnboardingResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.OnboardingService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.InkPageIndicator;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingPrefs;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingType;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingViewPager;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import org.parceler.Parcels;
import rx.Subscription;

import static com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData.ONBOARDING_DATA_KEY;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 19.12.2016.
 */

public class OnboardingActivity extends BaseActivity {

    @Inject OnboardingService mOnboardingService;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject Lazy<OnboardingPrefs> mOnboardingPrefs;
    @Inject Navigator mNavigator;

    @BindView(R.id.onboarding_container) View mContainerView;
    @BindView(R.id.onboarding_pager) OnboardingViewPager mOnboardingPager;
    @BindView(R.id.bottom_nav_dot_pager_indicator) InkPageIndicator mPageIndicator;
    @BindView(R.id.button_prev) NavigationButton mNavBtnPrev;
    @BindView(R.id.button_next) NavigationButton mNavBtnSubmit;

    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;
    private OnboardingAdapter mOnboardingAdapter;
    private Subscription mApiSubscription;

    private OnboardingResponseData mOnboardingData;
    private OnboardingType mType;
    private ArgbEvaluator mArgbEvaluator;

    /**
     * Creates new instance of OnboardingActivity.
     */
    public static Intent createIntent(Context context, OnboardingType type, boolean fromDiagnostics) {
        return createIntent(context, type, fromDiagnostics, null);
    }

    /**
     * Creates new instance of OnboardingActivity.
     */
    public static Intent createIntent(Context context, OnboardingType type, boolean fromDiagnostics, OnboardingResponseData onboardingResponse) {
        Intent intent = new Intent(context, OnboardingActivity.class);
        intent.putExtra(OnboardingType.ARG_KEY, Parcels.wrap(type));
        intent.putExtra(Arg.FROM_DIAGNOSTICS, fromDiagnostics);
        if (onboardingResponse != null) {
            intent.putExtra(Arg.ONBOARDING_DATA, Parcels.wrap(onboardingResponse));
        }
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.onboarding_activity);
        ButterKnife.bind(this);

        if (ViewUtils.isSmallDevice(this) && !ViewUtils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        mAnalyticsHelper.trackScreen(getString(R.string.screen_lesson_onboarding));

        mType = Parcels.unwrap(getIntent().getExtras().getParcelable(OnboardingType.ARG_KEY));

        if (savedInstanceState != null) {
            mOnboardingData = Parcels.unwrap(savedInstanceState.getParcelable(ONBOARDING_DATA_KEY));
        } else {

            mOnboardingData = Parcels.unwrap(getIntent().getExtras().getParcelable(Arg.ONBOARDING_DATA));
            if (mOnboardingData != null) {
                showPushOnBoardingIfRequired(mOnboardingData);
            }
        }

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.onboarding_preloader));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.onboarding_error));
        mErrorHelper.setOnActionClickListener(this::getOnboardingData);
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);
        mArgbEvaluator = new ArgbEvaluator();

        if (mOnboardingData == null) {
            getOnboardingData();
        } else {
            initUi();
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void initUi() {
        initPager();
        initBottomNav();
    }

    void initPager() {
        if (mOnboardingAdapter == null) {
            mOnboardingAdapter = new OnboardingAdapter(getSupportFragmentManager(), getSteps());
        }

        mOnboardingPager.addOnPageChangeListener(new ColorTransitionScrollListener());

        mOnboardingPager.setAdapter(mOnboardingAdapter);

        mPageIndicator.setViewPager(mOnboardingPager);
        if (mOnboardingAdapter.getCount() < 2) {
            mPageIndicator.setVisibility(View.INVISIBLE);
        }

        mOnboardingPager.setCurrentItem(0);
    }

    private void initBottomNav() {
        mNavBtnPrev.setText(mOnboardingData.getSkipText());
        mNavBtnPrev.setOnClickListener(v -> skipOnboarding());

        mNavBtnSubmit.setText(mOnboardingData.getNextText());
        mNavBtnSubmit.setOnClickListener(v -> {
            if (mOnboardingPager.getCurrentItem() == mOnboardingPager.getChildCount() - 1) {
                skipOnboarding();
            } else {
                mOnboardingPager.setCurrentItem(mOnboardingPager.getCurrentItem() + 1, true);
            }
        });
    }

    void skipOnboarding() {
        mOnboardingPrefs.get().setOnboardingVisited(mType);
        if (mType == OnboardingType.LESSON) {
            mNavigator.navigateToLesson(this, false, false);
            finish();
        } else {
            startActivity(DashboardActivity.createIntent(this, false));
            finish();
        }
    }

    void getOnboardingData() {
        // source dash -> returning user, source diagnostics -> new user, source from next step api
        boolean newUser = mCurrentSessionCache.getOnboardingUserType() == CurrentSessionCache.OnboardingUserType.NEW_USER;

        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mOnboardingService.getOnboarding(newUser)
                .map(OnboardingResponseValidator::validate)
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::onDataOnboardingReceived, this::onDataError);
    }

    private void onDataOnboardingReceived(OnboardingResponseData onboardingResponse) {
        showPushOnBoardingIfRequired(onboardingResponse);
        mOnboardingData = onboardingResponse;
        mPreloaderHelper.hide();
        initUi();
    }

    private void showPushOnBoardingIfRequired(OnboardingResponseData onboardingResponse) {
        if (onboardingResponse.getPushStep() != null && mType != OnboardingType.LESSON) {
            startActivityForResult(OnboardingPushActivity.createIntent(this, onboardingResponse), OnboardingPushActivity.REQUEST_CODE);
        }
    }

    private List<OnboardingStep> getSteps() {
        switch (mType) {
            case DASHBOARD:
                return mOnboardingData.getDashboardStep();
            case LESSON:
                return mOnboardingData.getLessonStep();
            default:
                return null;
        }
    }

    private void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.show();
        mPreloaderHelper.hide();
    }

    private void onSubscribe() {
        mPreloaderHelper.show();
        mErrorHelper.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OnboardingPushActivity.REQUEST_CODE) {
                mPreloaderHelper.hide();
                mOnboardingPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mOnboardingPager.getCurrentItem() == 0) {
            skipOnboarding();
        } else {
            mOnboardingPager.setCurrentItem(mOnboardingPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ONBOARDING_DATA_KEY, Parcels.wrap(mOnboardingData));
    }

    private int getBackgroundColor(int position, float positionOffset) {
        List<OnboardingStep> steps = getSteps();

        if (position < mOnboardingAdapter.getLastItemPosition()) {
            return (Integer) mArgbEvaluator.evaluate(positionOffset,
                    steps.get(position).getBackgroundColor(),
                    steps.get(position + 1).getBackgroundColor());
        } else {
            return steps.get(position).getBackgroundColor();
        }
    }

    public interface Arg {
        String FROM_DIAGNOSTICS = "FROM_DIAGNOSTICS";
        String ONBOARDING_DATA = "ONBOARDING_DATA";
    }

    private class ColorTransitionScrollListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int backgroundColor = getBackgroundColor(position, positionOffset);
            mContainerView.setBackgroundColor(backgroundColor);
        }

        @Override
        public void onPageSelected(int position) {
            mOnboardingAdapter.hideInvisibleItemContent(position);
            mOnboardingAdapter.animateFragment(position);
            if (position < mOnboardingAdapter.getLastItemPosition()) {
                mNavBtnPrev.setVisibility(View.VISIBLE);
            } else {
                mNavBtnPrev.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
