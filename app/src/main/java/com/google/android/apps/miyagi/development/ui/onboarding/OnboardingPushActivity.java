package com.google.android.apps.miyagi.development.ui.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.onboarding.OnboardingStep;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.parceler.Parcels;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData.ONBOARDING_DATA_KEY;

import javax.inject.Inject;

/**
 * Created by Lukasz on 21.01.2017.
 */

public class OnboardingPushActivity extends BaseActivity {

    public static final int REQUEST_CODE = 12331;
    @Inject NotificationsService mNotificationsService;
    @Inject ConfigStorage mConfigStorage;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;
    @BindView(R.id.button_prev) NavigationButton mNavBtnPrev;
    @BindView(R.id.button_next) NavigationButton mNavBtnSubmit;

    @BindView(R.id.content_container) View mContentContainer;
    @BindView(R.id.onboarding_container) View mContainerView;
    @BindView(R.id.onboarding_image) ImageView mImage;
    @BindView(R.id.onboarding_header) TextView mHeader;
    @BindView(R.id.onboarding_push_switch) Switch mPushSwitch;

    private ErrorScreenHelper mErrorHelper;
    private OnboardingResponseData mOnboardingData;

    private Subscription mApiSubscription;

    /**
     * Creates new instance of OnboardingPushActivity.
     */
    public static Intent createIntent(Context context, OnboardingResponseData onboardingResponseData) {
        Intent intent = new Intent(context, OnboardingPushActivity.class);
        intent.putExtra(OnboardingStep.ARG_KEY, Parcels.wrap(onboardingResponseData));
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.onboarding_push_activity);
        ButterKnife.bind(this);

        if (ViewUtils.isSmallDevice(this) && !ViewUtils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        mOnboardingData = Parcels.unwrap(getIntent().getParcelableExtra(OnboardingStep.ARG_KEY));

        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.onboarding_error));
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);

        initUi();
        initBottomNav();
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void initUi() {
        mContainerView.setBackgroundColor(mOnboardingData.getPushStep().getBackgroundColor());
        mHeader.setText(mOnboardingData.getPushStep().getTitle());

        mPushSwitch.setChecked(mConfigStorage.readPushToken() != null);
        mPushSwitch.jumpDrawablesToCurrentState();
        mPushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pushSwitchChanged(isChecked);
        });

        initAnimation();
        animateScreen();
    }

    public void initAnimation() {
        mScreenAnimationHelper.addAsyncImageView(ImageUrlHelper.getUrlFor(this, mOnboardingData.getPushStep().getImages()), mImage);
        mScreenAnimationHelper.addFlatView(mContentContainer);
    }

    public void animateScreen() {
        mScreenAnimationHelper.animateScreen();
    }

    private void initBottomNav() {
        mNavBtnPrev.setText(mOnboardingData.getSkipText());
        mNavBtnPrev.setOnClickListener(v -> finishWithResult());

        mNavBtnSubmit.setText(mOnboardingData.getNextText());
        mNavBtnSubmit.setOnClickListener(v -> finishWithResult());
    }

    private void finishWithResult() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    private void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOnboardingData = Parcels.unwrap(savedInstanceState.getParcelable(ONBOARDING_DATA_KEY));
    }

    private void pushSwitchChanged(boolean isChecked) {
        if (isChecked) {
            String pushToken = FirebaseInstanceId.getInstance().getToken();
            mConfigStorage.savePushToken(pushToken);
            sendNotificationToken(false);
        } else {
            sendNotificationToken(true);
            mConfigStorage.savePushToken(null);
        }
    }

    private void sendNotificationToken(boolean removeExistingToken) {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = Observable.create(
                (Observable.OnSubscribe<ResponseStatus>) subscriber -> {
                    NotificationsRequestData notificationsRequestData = new NotificationsRequestData(
                            mConfigStorage.readPushToken(),
                            mOnboardingData.getPushXsrfToken());

                    Subscriber<ResponseStatus> notificationSubscriber = new Subscriber<ResponseStatus>() {
                        @Override
                        public void onCompleted() {
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            subscriber.onError(throwable);
                        }

                        @Override
                        public void onNext(ResponseStatus responseStatus) {
                            subscriber.onNext(responseStatus);
                        }
                    };

                    if (removeExistingToken) {
                        mNotificationsService.removeNotificationsToken(notificationsRequestData).subscribe(notificationSubscriber);
                    } else {
                        mNotificationsService.addNotificationsToken(notificationsRequestData).subscribe(notificationSubscriber);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNotificationTokenSent, this::onDataError);
    }

    private void onNotificationTokenSent(ResponseStatus responseStatus) {
        mConfigStorage.saveShouldUpdatePushToken(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ONBOARDING_DATA_KEY, Parcels.wrap(mOnboardingData));
        super.onSaveInstanceState(outState);
    }
}
