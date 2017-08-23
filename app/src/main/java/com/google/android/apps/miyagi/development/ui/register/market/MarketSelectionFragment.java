package com.google.android.apps.miyagi.development.ui.register.market;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.config.ServiceConfig;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.labels.validator.LabelsResponseValidator;
import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.markets.validator.MarketResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.AssessmentService;
import com.google.android.apps.miyagi.development.data.net.services.AudioService;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.DashboardService;
import com.google.android.apps.miyagi.development.data.net.services.DiagnosticsService;
import com.google.android.apps.miyagi.development.data.net.services.LabelsService;
import com.google.android.apps.miyagi.development.data.net.services.LessonService;
import com.google.android.apps.miyagi.development.data.net.services.MarketsService;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsService;
import com.google.android.apps.miyagi.development.data.net.services.OnboardingService;
import com.google.android.apps.miyagi.development.data.net.services.ProfileService;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import org.parceler.Parcels;
import rx.Subscription;

import java.util.List;

import javax.inject.Inject;

/**
 * A screen that offers to market for content.
 */
public class MarketSelectionFragment extends RegistrationFragment {

    private List<Market> mItems;

    interface BundleKey {
        String MARKETS = "MARKETS";
    }

    @Inject ServiceConfig mServiceConfig;
    @Inject ConfigStorage mConfigStorage;
    @Inject MarketsService mRegistrationService;
    @Inject LabelsService mLabelsService;
    @Inject AssessmentService mAssessmentService;
    @Inject AudioService mAudioService;
    @Inject CommonDataService mCommonDataService;
    @Inject DashboardService mDasboardService;
    @Inject DiagnosticsService mDiagnosticsService;
    @Inject LessonService mLessonService;
    @Inject NextStepService mNextStepService;
    @Inject NotificationsService mNotificationsService;
    @Inject OnboardingService mOnboardingService;
    @Inject ProfileService mProfileService;
    @Inject RegisterUserService mRegisterService;
    @Inject AnalyticsHelper mAnalyticsHelper;

    private MarketAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private Subscription mSubscription;

    public MarketSelectionFragment() {
    }

    /**
     * Creates new MarketSelection fragment.
     */
    public static MarketSelectionFragment newInstance() {
        return new MarketSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_market_selection_fragment, container, false);
        GoogleApplication.getInstance().getAppComponent().inject(this);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_registration_country));

        setupUi(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            List<Market> markets = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.MARKETS));
            if (markets != null) {
                mItems = markets;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (mItems != null) {
            bindData(mItems);
        } else {
            getMarketsDataFromApi();
        }
    }

    private void setupUi(View rootView) {
        setupActionBar();

        Context context = getContext();

        // setup recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(context));

        // setup helpers for Preloader screen and Error screen
        mNavigationCallback.setErrorScreenOnActionClickListener(this::getMarketsDataFromApi);
    }

    private void setupActionBar() {
        RegisterActivity activity = (RegisterActivity) getActivity();
        activity.setTitle(getString(R.string.register_market_selection_title));
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void getMarketsDataFromApi() {
        SubscriptionHelper.unsubscribe(mSubscription);
        mRegistrationService.changeServiceUrl(mServiceConfig.getBaseApiUrl());
        mSubscription = mRegistrationService.getMarkets()
                .map(MarketResponseValidator::validate)
                .doOnSubscribe(this::doOnSubscribe)
                .doOnTerminate(this::doOnTerminate)
                .subscribe(this::onMarketsDataReceived, this::onDataError);
    }

    private void onMarketsDataReceived(MarketsResponse response) {
        MarketsResponseData responseData = response.getResponseData();
        List<Market> marketList = responseData.getMarkets();
        bindData(marketList);
    }

    private void bindData(List<Market> marketList) {
        if (marketList.size() == 1) {
            // skip market selection, select the only market and go to next step
            onMarketSelected(marketList.get(0));
        } else {
            mNavigationCallback.showLoaderScreen(false);
            mNavigationCallback.showErrorScreen(false);
            mItems = marketList;
            setAdapter(mItems);
        }
    }

    private void setAdapter(List<Market> items) {
        mAdapter = new MarketAdapter(getContext(), items, this::onMarketSelected);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    private void getLabelsDataFromApi(String endpointUrl) {
        SubscriptionHelper.unsubscribe(mSubscription);
        mLabelsService.changeServiceUrl(endpointUrl);
        mSubscription = mLabelsService.getLabels()
                .map(LabelsResponseValidator::validate)
                .doOnSubscribe(this::doOnSubscribe)
                .subscribe(this::onLabelsDataReceived, this::onDataError);
    }

    private void onLabelsDataReceived(LabelsResponse response) {
        LabelsResponseData labels = response.getResponseData();
        mConfigStorage.saveLabels(labels);
        mNavigationCallback.goToSignInSelection(false);
    }

    private void onMarketSelected(Market selectedMarket) {
        mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_country), getString(R.string.event_category_registration), getString(R.string.event_action_select_option), selectedMarket.getDisplayName());
        mConfigStorage.saveSelectedMarket(selectedMarket);
        updateEndpointUrl(selectedMarket.getEndpointUrl());
        getLabelsDataFromApi(selectedMarket.getEndpointUrl());
    }

    private void onDataError(Throwable throwable) {
        mNavigationCallback.showLoaderScreen(false);
        mNavigationCallback.setErrorScreenText(throwable);
        mNavigationCallback.showErrorScreenWithoutNavigationButton(true);
    }

    private void doOnSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreenWithoutNavigationButton(false);
    }

    private void doOnTerminate() {
        mNavigationCallback.showLoaderScreen(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        mAdapter = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mItems != null) {
            outState.putParcelable(BundleKey.MARKETS, Parcels.wrap(mItems));
        }
    }

    // todo: temp code, instead of this method use dagger scope:
    // http://frogermcs.github.io/building-userscope-with-dagger2/
    private void updateEndpointUrl(String newEndpointUrl) {
        mLabelsService.changeServiceUrl(newEndpointUrl);
        mAssessmentService.changeServiceUrl(newEndpointUrl);
        mAudioService.changeServiceUrl(newEndpointUrl);
        mCommonDataService.changeServiceUrl(newEndpointUrl);
        mDasboardService.changeServiceUrl(newEndpointUrl);
        mDiagnosticsService.changeServiceUrl(newEndpointUrl);
        mLessonService.changeServiceUrl(newEndpointUrl);
        mNextStepService.changeServiceUrl(newEndpointUrl);
        mNotificationsService.changeServiceUrl(newEndpointUrl);
        mOnboardingService.changeServiceUrl(newEndpointUrl);
        mProfileService.changeServiceUrl(newEndpointUrl);
        mRegisterService.changeServiceUrl(newEndpointUrl);
    }

}