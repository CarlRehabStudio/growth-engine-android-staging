package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.errors.Errors;
import com.google.android.apps.miyagi.development.data.models.errors.DeprecatedApiError;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.offline.OfflineDashboardActivity;
import com.google.android.apps.miyagi.development.ui.splash.SplashScreenActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dagger.Lazy;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;

import javax.inject.Inject;

/**
 * A wrapper that simplifies work with fullscreen error view.
 */
public class ErrorScreenHelper {

    @Inject Lazy<ConfigStorage> mConfigStorage;
    @Inject Lazy<AudioMetaDataDatabase> mAudioMetaDataDatabase;
    @Inject Lazy<SignOutUserHelper> mSignOutUserHelper;

    private final View mErrorView;
    private final Button mActionButton;
    private final ImageButton mNavigationButton;
    private final TextView mLabelMessage;
    private OnActionListener mActionButtonClickExternalListener = OnActionListener.EMPTY;
    private OnActionListener mNavigationButtonClickExternalListener = OnActionListener.EMPTY;
    private AudioModeListener mAudioModeListener = AudioModeListener.EMPTY;
    private DeprecatedApiError mDeprecatedError = null;


    /**
     * ErrorScreenHelper constructor.
     *
     * @param errorView - view to wrap.
     */
    public ErrorScreenHelper(View errorView) {
        ((GoogleApplication) errorView.getContext().getApplicationContext()).getAppComponent().inject(this);

        mErrorView = errorView;
        mErrorView.setOnClickListener(view -> { /* capture all clicks and do nothing */ });
        mActionButton = (Button) errorView.findViewById(R.id.error_button);
        mActionButton.setOnClickListener(view -> mActionButtonClickExternalListener.onActionButtonClick());
        mNavigationButton = (ImageButton) errorView.findViewById(R.id.navigation_button);
        mNavigationButton.setOnClickListener(view -> mNavigationButtonClickExternalListener.onActionButtonClick());
        mLabelMessage = (TextView) errorView.findViewById(R.id.error_label_message);
        hide();
    }

    /**
     * Sets message text.
     *
     * @param text - label text.
     */
    public void setMessage(String text) {
        mLabelMessage.setText(text);
    }

    /**
     * Sets action button text.
     *
     * @param text - text for button.
     */
    public void setButton(String text) {
        mActionButton.setText(text);
    }

    public void setOnActionClickListener(OnActionListener actionClickListener) {
        mActionButtonClickExternalListener = actionClickListener;
    }

    public void setOnNavigationClickListener(OnActionListener actionClickListener) {
        mNavigationButtonClickExternalListener = actionClickListener;
    }

    public void setAudioModeListener(AudioModeListener audioModeListener) {
        mAudioModeListener = audioModeListener;
    }

    public void show() {
        mErrorView.setVisibility(View.VISIBLE);
        KeyboardHelper.hideKeyboard(mErrorView);
    }

    public void hide() {
        mErrorView.setVisibility(View.GONE);
    }

    /**
     * Sets proper error message and button text for throwable.
     */
    public void setErrorForLoggedIn(Throwable throwable) {
        if (mConfigStorage.get().getCommonData() != null && mConfigStorage.get().getCommonData().getErrors() != null) {
            Errors errors = mConfigStorage.get().getCommonData().getErrors();
            if (throwable instanceof RetrofitException) {
                RetrofitException retrofitException = (RetrofitException) throwable;
                switch (retrofitException.getKind()) {
                    case NETWORK:
                        setNetworkErrorForLoggedIn(errors);
                        break;
                    case DEPRECATED_API:
                        setDeprecatedApiError((HttpException) throwable.getCause());
                        break;
                    case UNAUTHORIZED:
                        unauthorizedError();
                        break;
                    case HTTP:
                    case UNEXPECTED:
                    case INVALID_TOKEN:
                    default:
                        setInternalError(errors);
                        break;
                }
            } else {
                setInternalError(errors);
            }
        } else {
            setNoLabelsError();
        }
    }

    /**
     * Sets network error screen for logged in user.
     */
    public void setNetworkErrorForLoggedIn(Errors errors) {
        mAudioMetaDataDatabase.get().getSavedTopicsIdList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integers -> {
                    if (integers.size() > 0) {
                        setOfflineError(errors);
                    } else {
                        setNetworkError();
                    }
                }, throwable -> setInternalError(errors));
    }

    private void unauthorizedError() {
        Context context = mErrorView.getContext();
        mSignOutUserHelper.get().signOut();
        Intent intent = SplashScreenActivity.createIntent(context, true);
        context.startActivity(intent);
    }

    /**
     * Sets proper error message and button text for throwable.
     */
    public void setErrorForLoggedOut(Throwable throwable) {
        LabelsResponseData labelsResponseData = mConfigStorage.get().getLabels();
        if (labelsResponseData != null) {
            ErrorsLabels errors = labelsResponseData.getErrorsLabels();
            if (throwable instanceof RetrofitException) {
                RetrofitException retrofitException = (RetrofitException) throwable;
                switch (retrofitException.getKind()) {
                    case DEPRECATED_API:
                        setDeprecatedApiError((HttpException) throwable.getCause());
                        break;
                    case NETWORK:
                    case HTTP:
                    case UNEXPECTED:
                    case UNAUTHORIZED:
                    case INVALID_TOKEN:
                    default:
                        setServerError(errors);
                        break;
                }
            } else {
                setServerError(errors);
            }
        } else {
            setNoLabelsError();
        }
    }

    /**
     * Sets navigation button in upper left corner visible or gone.
     */
    public void showNavigationButton(boolean show) {
        if (show) {
            mNavigationButton.setVisibility(View.VISIBLE);
        } else {
            mNavigationButton.setVisibility(View.GONE);
        }
    }

    /**
     * Set error screen for no network connection.
     */
    public void setNetworkError() {
        if (mConfigStorage.get().getCommonData() != null && mConfigStorage.get().getCommonData().getErrors() != null) {
            Errors errors = mConfigStorage.get().getCommonData().getErrors();
            mLabelMessage.setText(errors.getNoNetworkError().getText());
            mActionButton.setText(errors.getNoNetworkError().getCta());
            mActionButton.setOnClickListener(view -> mActionButtonClickExternalListener.onActionButtonClick());
        } else {
            setNoLabelsError();
        }
    }

    private void setOfflineError(Errors errors) {
        mLabelMessage.setText(errors.getGoToOfflineError().getText());
        mActionButton.setText(errors.getGoToOfflineError().getCta());
        mActionButton.setOnClickListener(view -> {
            mErrorView.getContext().startActivity(OfflineDashboardActivity.createIntent(mErrorView.getContext()));
            mAudioModeListener.onGoToAudioModeClick();
        });
    }

    private void setInternalError(Errors errors) {
        mLabelMessage.setText(errors.getInternalError().getText());
        mActionButton.setText(errors.getInternalError().getCta());
        mActionButton.setOnClickListener(view -> mActionButtonClickExternalListener.onActionButtonClick());
    }

    private void setServerError(ErrorsLabels errors) {
        mLabelMessage.setText(errors.getServerError());
        mActionButton.setText(errors.getServerErrorButton());
        mActionButton.setOnClickListener(view -> mActionButtonClickExternalListener.onActionButtonClick());
    }

    private void setNoLabelsError() {
        mLabelMessage.setText(R.string.error_first_request_failed);
        mActionButton.setText(R.string.error_first_request_failed_cta);
        mActionButton.setOnClickListener(view -> mActionButtonClickExternalListener.onActionButtonClick());
    }

    private void setDeprecatedApiError(HttpException exception) {
        ResponseBody responseBody = exception.response().errorBody();
        String responseBodyStr = null;
        try {
            responseBodyStr = responseBody.string();
            if (responseBodyStr != null) {
                mDeprecatedError = new Gson().fromJson(responseBodyStr, DeprecatedApiError.class);
            }
        } catch (IOException exc) {
            exc.printStackTrace();
            return;
        } catch (JsonSyntaxException exc) {
            exc.printStackTrace();
        }

        if (mDeprecatedError != null) {
            mLabelMessage.setText(mDeprecatedError.getMessage());
            mActionButton.setText(mDeprecatedError.getCtaText());
            showNavigationButton(false);
            setOnActionClickListener(() -> {
                try {
                    Uri uri = Uri.parse(mDeprecatedError.getAndroidUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Context context = mErrorView.getContext();
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            });
        }
    }

    public interface OnActionListener {
        OnActionListener EMPTY = () -> { /* empty */ };

        void onActionButtonClick();
    }

    public interface AudioModeListener {
        AudioModeListener EMPTY = () -> {
        };

        void onGoToAudioModeClick();
    }
}