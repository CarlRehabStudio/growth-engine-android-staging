package com.google.android.apps.miyagi.development.ui.register.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.labels.SignInSelectionLabels;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.helpers.SafeLinkMovementMethod;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.NetworkUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.app.Activity.RESULT_CANCELED;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import javax.inject.Inject;

/**
 * Created by jerzyw on 04.11.2016.
 */

public class SignInSelectionFragment extends RegistrationFragment<SignInContract.Presenter> implements SignInContract.View, GoogleApiClient.OnConnectionFailedListener {

    private static final String SINGLE_MARKET_KEY = "SINGLE_MARKET_KEY";
    private static final int SIGN_IN_REQUEST = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9999;

    @BindView(R.id.image_background) ImageView mImageBackground;
    @BindView(R.id.image_logo) ImageView mImageLogo;
    @BindView(R.id.label_header) TextView mLabelHeader;
    @BindView(R.id.label_footer) TextView mLabelFooter;
    @BindView(R.id.button_sign_in_with_google) AppCompatButton mButtonSignInWithGoogle;
    @BindView(R.id.button_sign_in_with_email) AppCompatButton mButtonSignInWithEmail;

    @Inject SignInPresenter mPresenter;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;
    @Inject AnalyticsHelper mAnalyticsHelper;

    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private boolean mGoogleSignInResultSuccess;

    /**
     * Creates new SignInSelection fragment instance.
     */
    public static SignInSelectionFragment newInstance(boolean singleMarket) {
        Bundle args = new Bundle();
        args.putBoolean(SINGLE_MARKET_KEY, singleMarket);
        SignInSelectionFragment fragment = new SignInSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mGoogleSignInResultSuccess = savedInstanceState.getBoolean(BundleKey.ON_RESULT_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_sign_in_selection_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        createGoogleApiClient();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.setSingleMarket(getArguments().getBoolean(SINGLE_MARKET_KEY));

        mAnalyticsHelper.trackScreen(getString(R.string.screen_registration_account));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ViewUtils.isSmallDevice(getContext()) && !ViewUtils.isTablet(getContext())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKey.ON_RESULT_STATE, mGoogleSignInResultSuccess);
    }

    @Override
    public void setUpUi(int googleColor, int emailColor) {
        mNavigationCallback.showLoaderScreen(mGoogleSignInResultSuccess);

        mLabelFooter.setMovementMethod(new SafeLinkMovementMethod());

        mButtonSignInWithGoogle.setOnClickListener(this::onLoginButtonClick);
        mButtonSignInWithEmail.setOnClickListener(this::onLoginButtonClick);

        ColorHelper.tintButtonBackground(mButtonSignInWithGoogle, googleColor);
        ColorHelper.tintButtonBackground(mButtonSignInWithEmail, emailColor);
    }

    @Override
    public void setUpActionBar(boolean displayHomeAsUpEnabled) {
        RegisterActivity activity = (RegisterActivity) getActivity();
        activity.setTitle(null);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
        }
    }

    private void createGoogleApiClient() {
        if (checkGooglePlayServices(getActivity())) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    public void bindData(SignInSelectionLabels labels) {
        mLabelHeader.setText(labels.getGoogleHeaderText());
        mButtonSignInWithGoogle.setText(labels.getSignInGoogle());
        mButtonSignInWithEmail.setText(labels.getSignInEmail());

        mLabelFooter.setText(HtmlHelper.formatHtmlLink(labels.getFooterLegalUrl(), labels.getFooterLegal()));

        animateScreen();
    }

    private void animateScreen() {
        mScreenAnimationHelper.addFlatView(mImageLogo);
        mScreenAnimationHelper.addFlatView(mLabelHeader);
        mScreenAnimationHelper.addFlatView(mImageBackground);
        mScreenAnimationHelper.addFlatView(mLabelFooter);

        mScreenAnimationHelper.addCtaView(mButtonSignInWithGoogle);
        mScreenAnimationHelper.addCtaView(mButtonSignInWithEmail);

        mScreenAnimationHelper.animateScreen();
    }

    private void onLoginButtonClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in_with_google:
                mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_account), getString(R.string.event_category_registration), getString(R.string.event_action_select_option), mButtonSignInWithGoogle.getText().toString());
                mPresenter.onGoogleLoginSelected();
                break;
            case R.id.button_sign_in_with_email:
                mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_account), getString(R.string.event_category_registration), getString(R.string.event_action_select_option), mButtonSignInWithEmail.getText().toString());
                mPresenter.onEmailLoginSelected();
                break;
            default:
                Lh.e(this, "onLoginButtonClick: UNKNOWN BUTTON ID");
        }
    }

    @Override
    public void signInWithGoogle() {
        if (mGoogleApiClient != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, SIGN_IN_REQUEST);
        } else {
            createGoogleApiClient();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, wait for onResume and authenticate with Firebase
                mGoogleSignInResultSuccess = true;
                GoogleSignInAccount account = result.getSignInAccount();
                boolean isOnline = NetworkUtils.isOnline(getContext());
                mPresenter.onGoogleLoggedIn(account.getIdToken(), account.getServerAuthCode(), account.getEmail(), isOnline);
            } else {
                // TODO Google Sign In failed, update UI appropriately
                mGoogleSignInResultSuccess = false;
                Lh.e(this, "Google sign in failed.");
            }
        } else if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                getActivity().finish();
            } else {
                if (mGoogleApiClient == null) {
                    createGoogleApiClient();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
        mUnbinder = null;

        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onScreenEnter() {
        ((GoogleApplication) getActivity().getApplication()).createSignInComponent();
    }

    @Override
    public void onScreenExit() {
        ((GoogleApplication) getActivity().getApplication()).releaseSignInComponent();
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) getActivity().getApplication()).getSignInComponent().inject(this);
    }

    @Override
    public SignInPresenter getPresenter() {
        return mPresenter;
    }

    private boolean checkGooglePlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                CharSequence message = getResources().getText(R.string.error_message_update_google_play_services);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //ignore
    }

    private interface BundleKey {
        String ON_RESULT_STATE = "ON_RESULT_STATE";
    }
}