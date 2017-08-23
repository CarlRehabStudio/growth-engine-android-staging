package com.google.android.apps.miyagi.development.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileUpdateRequestData;
import com.google.android.apps.miyagi.development.data.net.responses.profile.validator.ProfileResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsService;
import com.google.android.apps.miyagi.development.data.net.services.ProfileService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.InputValidator;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rengwuxian.materialedittext.MaterialEditText;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 08.11.2016.
 */

public class ProfileActivity extends BaseActivity {

    @Inject ProfileService mProfileService;
    @Inject NotificationsService mNotificationsService;
    @Inject AppColors mAppColors;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject ConfigStorage mConfigStorage;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.form_layout) View mMainLayout;
    @BindView(R.id.profile_first_name_input) MaterialEditText mFirstNameInput;
    @BindView(R.id.profile_last_name_input) MaterialEditText mLastNameInput;
    @BindView(R.id.push_notifications) Switch mPushSwitch;
    @BindView(R.id.push_notifications_text) TextView mPushNotificationText;
    @BindView(R.id.email_notifications) Switch mEmailSwitch;
    @BindView(R.id.email_notifications_text) TextView mEmailNotificationText;
    @BindView(R.id.profile_delete_account) TextView mDeleteAccountText;
    @BindView(R.id.profile_personal_section_header) TextView mPersonalSectionHeader;
    @BindView(R.id.profile_settings_section_header) TextView mSettingSectionHeader;
    @BindView(R.id.profile_login_information_section_header) TextView mLoginSectionHeader;
    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_next) NavigationButton mSaveButton;

    private Toolbar mToolbar;

    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;
    private Subscription mProfileSubscription;

    private ProfileResponseData mProfileResponse;
    private boolean mEmailNotificationsEnabled;

    private boolean mShowBottomNavigation;

    public static Intent createIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_profile));

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mBottomNav.setBackgroundColor(mAppColors.getMainBackgroundColor());

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.profile_preloader));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.profile_error_screen));
        mErrorHelper.setOnActionClickListener(this::getProfileData);
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);

        if (savedInstanceState != null) {
            mShowBottomNavigation = savedInstanceState.getBoolean(BundleKey.SHOW_BOTTOM_NAVIGATION);
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProfileResponse = mCurrentSessionCache.getProfileResponseData();
        if (mProfileResponse != null) {
            onDataProfileReceived(mProfileResponse);
        } else {
            getProfileData();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKey.SHOW_BOTTOM_NAVIGATION, mShowBottomNavigation);
    }

    private void getProfileData() {
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);
        SubscriptionHelper.unsubscribe(mProfileSubscription);
        mProfileSubscription = mProfileService.getProfile()
                .map(ProfileResponseValidator::validate)
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onDataProfileReceived, this::onDataError);
    }

    private void onDataProfileReceived(ProfileResponseData profileResponseData) {
        mCurrentSessionCache.setProfileResponseData(profileResponseData);
        mProfileResponse = profileResponseData;
        mToolbar.setTitle(mProfileResponse.getHeader());

        //Personal section
        mPersonalSectionHeader.setBackgroundColor(mAppColors.getSectionBackgroundColor());
        mPersonalSectionHeader.setText(mProfileResponse.getPersonal().getHeader());

        mFirstNameInput.setText(mProfileResponse.getStudent().getName());
        mFirstNameInput.setHint(mProfileResponse.getPersonal().getFirstNameLabel());
        mFirstNameInput.setFloatingLabelText(mProfileResponse.getPersonal().getFirstNameLabel());
        mFirstNameInput.addTextChangedListener(mProfileChangeWatcher);

        mLastNameInput.setText(mProfileResponse.getStudent().getLastName());
        mLastNameInput.setHint(mProfileResponse.getPersonal().getLastNameLabel());
        mLastNameInput.setFloatingLabelText(mProfileResponse.getPersonal().getLastNameLabel());
        mLastNameInput.addTextChangedListener(mProfileChangeWatcher);

        //Setting section
        mSettingSectionHeader.setBackgroundColor(mAppColors.getSectionBackgroundColor());
        mSettingSectionHeader.setText(mProfileResponse.getSettings().getHeader());
        mPushSwitch.setChecked(mConfigStorage.readPushToken() != null);
        mPushSwitch.jumpDrawablesToCurrentState();
        mPushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showNavigationBar(true);
            pushSwitchChanged(isChecked);
        });
        mPushNotificationText.setText(mProfileResponse.getSettings().getPushNotification());

        mEmailNotificationsEnabled = profileResponseData.getStudent().isNotifications();
        mEmailSwitch.setChecked(mEmailNotificationsEnabled);
        mEmailSwitch.jumpDrawablesToCurrentState();
        mEmailSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showNavigationBar(true);
            saveChangesInCurrentCache();
        });
        mEmailNotificationText.setText(mProfileResponse.getSettings().getEmailNotification());

        //Login section
        mLoginSectionHeader.setBackgroundColor(mAppColors.getSectionBackgroundColor());
        mLoginSectionHeader.setText(mProfileResponse.getLogin().getHeader());

        mDeleteAccountText.setText(HtmlHelper.fromHtml(mProfileResponse.getLogin().getDeleteAccount()));
        mDeleteAccountText.setOnClickListener(view -> {
            try {
                Uri uri = Uri.parse(mProfileResponse.getWebProfileUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            } catch (Exception ex) {
                // ignore
            }
        });

        mSaveButton.setText(mProfileResponse.getForm().getSaveButton());
        mSaveButton.setOnClickListener(v -> validateInput());

        ProfileUpdateRequestData profileUpdateData = mCurrentSessionCache.getProfileUpdateRequestData();
        if (profileUpdateData != null) {
            // programmatically change the state of inputs and email switch impacts on bottom
            // navigation visibility, keep old value and restore it
            boolean savedValue = mShowBottomNavigation;
            mFirstNameInput.setText(profileUpdateData.getFirstName());
            mLastNameInput.setText(profileUpdateData.getLastName());
            mEmailSwitch.setChecked(profileUpdateData.isEmailNotifications());
            mShowBottomNavigation = savedValue;
        }

        showNavigationBar(mShowBottomNavigation);
    }

    private void validateInput() {
        boolean isValid = true;

        if (InputValidator.notNullAndNotEmpty(mFirstNameInput.getText().toString())) {
            mFirstNameInput.setError(null);
        } else {
            mFirstNameInput.setError(mProfileResponse.getForm().getValidation().getEmptyFirstName());
            isValid = false;
        }

        if (InputValidator.notNullAndNotEmpty(mLastNameInput.getText().toString())) {
            mLastNameInput.setError(null);
        } else {
            mLastNameInput.setError(mProfileResponse.getForm().getValidation().getEmptyLastName());
            isValid = false;
        }

        if (isValid) {
            updateProfile();
        }
    }

    private void updateProfile() {
        mErrorHelper.setOnNavigationClickListener(() -> mErrorHelper.hide());
        SubscriptionHelper.unsubscribe(mProfileSubscription);
        mProfileSubscription = Observable.create(
                (Observable.OnSubscribe<ResponseStatus>) subscriber -> {
                    ProfileUpdateRequestData profileUpdateRequestData = new ProfileUpdateRequestData(mFirstNameInput.getText().toString(),
                            mLastNameInput.getText().toString(),
                            mEmailSwitch.isChecked(),
                            mEmailSwitch.isChecked() != mEmailNotificationsEnabled,
                            mProfileResponse.getStudentEditXsrfToken());

                    mProfileService.updateProfile(profileUpdateRequestData).subscribe(new Subscriber<ResponseStatus>() {
                        @Override
                        public void onCompleted() {
                            mConfigStorage.saveShouldUpdateDashboard(true);
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
                    });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onProfileUpdated, this::onDataError);
    }

    private void onProfileUpdated(ResponseStatus responseStatus) {
        showNavigationBar(false);
        mEmailNotificationsEnabled = mEmailSwitch.isChecked();
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
        SubscriptionHelper.unsubscribe(mProfileSubscription);
        mProfileSubscription = Observable.create(
                (Observable.OnSubscribe<ResponseStatus>) subscriber -> {
                    NotificationsRequestData notificationsRequestData = new NotificationsRequestData(
                            mConfigStorage.readPushToken(),
                            mProfileResponse.getPushXsrfToken());

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

    private void onTerminate() {
        mPreloaderHelper.hide();
    }

    private void onSubscribe() {
        mMainLayout.requestFocus();
        KeyboardHelper.hideKeyboard(this);
        mPreloaderHelper.show();
        mErrorHelper.hide();
    }

    private void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mProfileSubscription);
        mProfileSubscription = null;
    }

    private void showNavigationBar(boolean show) {
        mShowBottomNavigation = show;
        if (show && mBottomNav.getVisibility() != View.VISIBLE) {
            mBottomNav.setVisibility(View.VISIBLE);
            Animation headerAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_bottom);
            headerAnimation.setInterpolator(new FastOutSlowInInterpolator());
            mBottomNav.startAnimation(headerAnimation);
        } else if (!show) {
            mBottomNav.setVisibility(View.GONE);
        }
    }

    private void saveChangesInCurrentCache() {
        mCurrentSessionCache.setProfileUpdateRequestData(new ProfileUpdateRequestData(mFirstNameInput.getText().toString(),
                mLastNameInput.getText().toString(),
                mEmailSwitch.isChecked(),
                mEmailSwitch.isChecked() != mEmailNotificationsEnabled,
                mProfileResponse.getStudentEditXsrfToken()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCurrentSessionCache.setProfileResponseData(null);
    }

    private TextWatcher mProfileChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            showNavigationBar(true);
            saveChangesInCurrentCache();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    interface BundleKey {
        String SHOW_BOTTOM_NAVIGATION = "SHOW_BOTTOM_NAVIGATION";
    }
}
