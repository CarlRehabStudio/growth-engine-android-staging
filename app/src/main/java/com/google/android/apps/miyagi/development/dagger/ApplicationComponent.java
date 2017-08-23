package com.google.android.apps.miyagi.development.dagger;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.dagger.providers.MainRxRetrofitProvider;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RxErrorHandlingCallAdapterFactory;
import com.google.android.apps.miyagi.development.helpers.AudioFileAdapterHelper;
import com.google.android.apps.miyagi.development.helpers.CertificateDownloadHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.notifications.GoogleInstanceIdService;
import com.google.android.apps.miyagi.development.notifications.GoogleMessagingService;
import com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity;
import com.google.android.apps.miyagi.development.ui.assessment.result.BadgeFragment;
import com.google.android.apps.miyagi.development.ui.assessment.result.ResultFragment;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerTabletActivity;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioDownloadService;
import com.google.android.apps.miyagi.development.ui.audio.transcript.AudioTranscriptActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.LegalMenuActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicencesListActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.NavigationDrawerFragment;
import com.google.android.apps.miyagi.development.ui.diagnostics.DiagnosticsActivity;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingComponent;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingModule;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingPresenter;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectGoalsFragment;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectLearningTypeFragment;
import com.google.android.apps.miyagi.development.ui.diagnostics.pages.SelectPersonaFragment;
import com.google.android.apps.miyagi.development.ui.lesson.LessonActivity;
import com.google.android.apps.miyagi.development.ui.lesson.LessonTabletActivity;
import com.google.android.apps.miyagi.development.ui.offline.OfflineDashboardActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingBaseFragment;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingPushActivity;
import com.google.android.apps.miyagi.development.ui.practice.PracticeActivity;
import com.google.android.apps.miyagi.development.ui.profile.ProfileActivity;
import com.google.android.apps.miyagi.development.ui.register.RegisterComponent;
import com.google.android.apps.miyagi.development.ui.register.RegisterModule;
import com.google.android.apps.miyagi.development.ui.register.RegisterPresenter;
import com.google.android.apps.miyagi.development.ui.register.email.EmailResetPasswordFragment;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountFragment;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountPresenter;
import com.google.android.apps.miyagi.development.ui.register.email.create_account.EmailCreateAccountFragment;
import com.google.android.apps.miyagi.development.ui.register.email.create_account.EmailCreateAccountPresenter;
import com.google.android.apps.miyagi.development.ui.register.email.enter_password.EmailEnterPasswordFragment;
import com.google.android.apps.miyagi.development.ui.register.email.enter_password.EmailEnterPasswordPresenter;
import com.google.android.apps.miyagi.development.ui.register.google.CompleteAccountInfoFragment;
import com.google.android.apps.miyagi.development.ui.register.market.MarketSelectionFragment;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInComponent;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInModule;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInPresenter;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInWithProviderFragment;
import com.google.android.apps.miyagi.development.ui.result.ResultActivity;
import com.google.android.apps.miyagi.development.ui.splash.SplashScreenActivity;
import com.google.android.apps.miyagi.development.ui.statistics.StatisticsActivity;
import com.google.android.apps.miyagi.development.ui.statistics.StatisticsTabletActivity;
import com.google.android.apps.miyagi.development.ui.web.WebViewActivity;
import com.google.android.apps.miyagi.development.utils.RetryWithTokenRefresh;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by Paweł on 2017-02-25.
 */

@Singleton
@Component(modules = {MainModule.class, NetModule.class, ConfigModule.class})
public interface ApplicationComponent {

    //inject methods
    void inject(GoogleApplication googleApplication);

    void inject(ResultFragment resultFragment);

    void inject(MarketSelectionFragment marketSelectionFragment);

    void inject(BadgeFragment badgeFragment);

    void inject(SignInPresenter presenter); //TODO sprawdzic czy mozna przeniesc do SignInComponent

    void inject(RegisterPresenter presenter);

    void inject(DashboardActivity dashboardActivity);

    void inject(OfflineDashboardActivity offlineDashboardActivity);

    void inject(AudioFileAdapterHelper dashboardFileHelper);

    void inject(CertificateDownloadHelper certificationDownloadHelper);

    void inject(SplashScreenActivity splashScreenActivity);

    void inject(SignInWithProviderFragment signInWithProviderFragment);

    void inject(EmailCheckAccountFragment emailCheckAccountFragment);

    void inject(EmailEnterPasswordFragment fragment);

    void inject(EmailResetPasswordFragment emailResetPasswordFragment);

    void inject(EmailCreateAccountFragment emailCreateAccountFragment);

    void inject(CompleteAccountInfoFragment completeAccountInfoFragment);

    void inject(LessonActivity lessonActivity);

    void inject(LessonTabletActivity lessonActivity);

    void inject(PracticeActivity practiceActivity);

    void inject(AssessmentActivity assessmentActivity);

    void inject(ProfileActivity profileActivity);

    void inject(DiagnosticsActivity diagnosticsActivity);

    void inject(AudioPlayerActivity audioPlayerActivity);

    void inject(AudioPlayerTabletActivity audioPlayerActivity);

    void inject(AudioDownloadService audioDownloadService);

    void inject(OnboardingActivity onboardingActivity);

    void inject(OnboardingPushActivity onboardingPushActivity);

    void inject(AudioTranscriptActivity audioTranscriptActivity);

    void inject(LegalMenuActivity legalMenuActivity);

    void inject(LicencesListActivity licencesListActivity);

    void inject(NavigationDrawerFragment navigationDrawerFragment);

    void inject(RxErrorHandlingCallAdapterFactory rxErrorHandlingCallAdapterFactory);

    void inject(RetryWithTokenRefresh retryWithSessionRefresh);

    void inject(MainRxRetrofitProvider mainRxRetrofitProvider);

    void inject(SignOutUserHelper signoutUserHelper);

    void inject(ErrorScreenHelper errorScreenHelper);

    void inject(ResultActivity resultActivity);

    void inject(StatisticsActivity statisticsActivity);

    void inject(StatisticsTabletActivity statisticsActivity);

    void inject(GoogleInstanceIdService instanceIdService);

    void inject(DiagnosticsLoadingPresenter diagnosticsLoadingPresenter);

    void inject(SelectLearningTypeFragment selectLearningTypeFragment);

    void inject(SelectGoalsFragment selectGoalsFragment);

    void inject(SelectPersonaFragment selectPersonaFragment);

    void inject(OnboardingBaseFragment fragment);

    void inject(GoogleMessagingService service);

    void inject(WebViewActivity webViewActivity);

    SignInComponent plus(SignInModule signInModule);

    RegisterComponent plus(RegisterModule registerModule);

    DiagnosticsLoadingComponent plus(DiagnosticsLoadingModule diagnosticsLoadingModule);

    void inject(EmailEnterPasswordPresenter presenter);

    void inject(EmailCheckAccountPresenter emailCheckAccountPresenter);

    void inject(EmailCreateAccountPresenter emailCreateAccountPresenter);
}
