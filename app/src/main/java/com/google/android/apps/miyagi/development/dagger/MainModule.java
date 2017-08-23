package com.google.android.apps.miyagi.development.dagger;

import android.app.Application;
import android.content.Context;
import com.google.android.apps.miyagi.development.dagger.providers.MainRxRetrofitProvider;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.net.services.AssessmentService;
import com.google.android.apps.miyagi.development.data.net.services.AssessmentServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.AudioService;
import com.google.android.apps.miyagi.development.data.net.services.AudioServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.DashboardService;
import com.google.android.apps.miyagi.development.data.net.services.DashboardServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.DiagnosticsService;
import com.google.android.apps.miyagi.development.data.net.services.DiagnosticsServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.LabelsService;
import com.google.android.apps.miyagi.development.data.net.services.LabelsServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.LessonService;
import com.google.android.apps.miyagi.development.data.net.services.LessonServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.MarketsService;
import com.google.android.apps.miyagi.development.data.net.services.MarketsServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.NextStepServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsService;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.OnboardingService;
import com.google.android.apps.miyagi.development.data.net.services.OnboardingServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.ProfileService;
import com.google.android.apps.miyagi.development.data.net.services.ProfileServiceImpl;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserService;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserServiceImpl;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.data.storage.config.impl.RealmConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.AudioDownloadPrefs;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioDownloadService;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingPrefs;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountPresenter;
import com.google.android.apps.miyagi.development.ui.register.email.create_account.EmailCreateAccountPresenter;
import com.google.android.apps.miyagi.development.ui.register.email.enter_password.EmailEnterPasswordPresenter;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jerzyw on 05.10.2016.
 * Dagger 2 module. Provides main components for injection. Uses other Dagger module.
 */
@Module
public class MainModule {

    private final Application mApplication;

    public MainModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    public Context provideAppContext() {
        return mApplication.getApplicationContext();
    }

    /**
     * Creates AudioService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return RegistrationService instance for dagger injection.
     */
    @Singleton
    @Provides
    public AudioService provideAudioInfoServiceApi(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new AudioServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates RegistrationService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return RegistrationService instance for dagger injection.
     */
    @Singleton
    @Provides
    public MarketsService provideRegistrationServiceApi(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new MarketsServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates RegisterUserService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return RegisterUserService instance for dagger injection.
     */
    @Singleton
    @Provides
    public RegisterUserService provideRegisterUserServiceApi(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new RegisterUserServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates DashboardService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return DashboardService instance for dagger injection.
     */
    @Singleton
    @Provides
    public DashboardService provideDashboardService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new DashboardServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates NextStepService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return NextStepService instance for dagger injection.
     */
    @Singleton
    @Provides
    public NextStepService provideNextStepServiceService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new NextStepServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates LessonService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return LessonService instance for dagger injection.
     */
    @Singleton
    @Provides
    public LessonService provideLessonService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new LessonServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates AssessmentService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return AssessmentService instance for dagger injection.
     */
    @Singleton
    @Provides
    public AssessmentService provideAssessmentService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new AssessmentServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates LabelsService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return LabelsService instance for dagger injection.
     */
    @Singleton
    @Provides
    public LabelsService provideLabelsService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new LabelsServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates CommonDataService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return CommonDataService instance for dagger injection.
     */
    @Singleton
    @Provides
    public CommonDataService provideCommonDataService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new CommonDataServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates ProfileService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return ProfileService instance for dagger injection.
     */
    @Singleton
    @Provides
    public ProfileService provideProfileService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new ProfileServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates DiagnosticsService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return DiagnosticsService instance for dagger injection.
     */
    @Singleton
    @Provides
    public DiagnosticsService provideDiagnosticsService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new DiagnosticsServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates OnboardingService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return OnboardingService instance for dagger injection.
     */
    @Singleton
    @Provides
    public OnboardingService provideOnboardingService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new OnboardingServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates RegisterUserService implementation for dagger injection. Tip: Mocked object can be returned here.
     *
     * @param retrofitProvider - provider for creation custom configured Retrofit instance.
     * @return RegisterUserService instance for dagger injection.
     */
    @Singleton
    @Provides
    public NotificationsService provideNotificationsService(MainRxRetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        return new NotificationsServiceImpl(retrofitProvider, configStorage);
    }

    /**
     * Creates ConfigStorage implementation for dagger injection.
     *
     * @return ConfigStorage instance for dagger injection.
     */
    @Singleton
    @Provides
    ConfigStorage providesConfigStorage() {
        return new RealmConfigStorage();
    }

    /**
     * Creates AppColors implementation for dagger injection.
     *
     * @return AppColors instance for dagger injection.
     */
    @Singleton
    @Provides
    AppColors provideColors(ConfigStorage configStorage) {
        return configStorage.getCommonData().getColors();
    }

    /**
     * Creates AudioDownloadService implementation for dagger injection.
     *
     * @return AudioDownloadService instance for dagger injection.
     */
    @Provides
    @Singleton
    public AudioDownloadService provideAudioDownloadService(AudioService audioService, AudioMetaDataDatabase audioMetaDataDatabase, Context context) {
        return new AudioDownloadService(audioService, audioMetaDataDatabase, context);
    }

    /**
     * Creates AudioMetaDataDatabase implementation for dagger injection.
     *
     * @return AudioMetaDataDatabase instance for dagger injection.
     */
    @Provides
    @Singleton
    public AudioMetaDataDatabase provideAudioMetaDataDatabase() {
        return new AudioMetaDataDatabase();
    }

    /**
     * Creates AudioMetaDataDatabase implementation for dagger injection.
     *
     * @return AudioMetaDataDatabase instance for dagger injection.
     */
    @Provides
    @Singleton
    public AnalyticsHelper provideAnalyticsHelper(Context context) {
        return new AnalyticsHelper(context);
    }

    /**
     * Creates ScreenAnimationHelper implementation for dagger injection.
     *
     * @return ScreenAnimationHelper instance for dagger injection.
     */
    @Provides
    public ScreenAnimationHelper provideScreenAnimationHelper(Context context) {
        return new ScreenAnimationHelper(context);
    }

    /**
     * Creates OnboardingPrefs implementation for dagger injection.
     *
     * @return OnboardingPrefs instance for dagger injection.
     */
    @Provides
    public OnboardingPrefs provideOnboardingPrefs(Context context) {
        return new OnboardingPrefs(context);
    }

    /**
     * Creates AudioDownloadPrefs implementation for dagger injection.
     *
     * @return AudioDownloadPrefs instance for dagger injection.
     */
    @Provides
    public AudioDownloadPrefs provideAudioDownloadPrefs(Context context) {
        return new AudioDownloadPrefs(context);
    }

    /**
     * Creates SignOutUserHelper implementation for dagger injection.
     *
     * @return SignOutUserHelper instance for dagger injection.
     */
    @Provides
    public SignOutUserHelper provideSignOutUserHelper(ConfigStorage configStorage,
                                                      AudioDownloadService audioDownloadService,
                                                      CurrentSessionCache currentSessionCache,
                                                      AudioDownloadPrefs audioDownloadPrefs) {

        return new SignOutUserHelper(configStorage, audioDownloadService, currentSessionCache, audioDownloadPrefs);
    }

    /**
     * Creates EmailEnterPasswordPresenter implementation for dagger injection.
     *
     * @return EmailEnterPasswordPresenter instance for dagger injection.
     */
    @Singleton
    @Provides
    public EmailEnterPasswordPresenter provideEmailEnterPasswordPresenter() {
        return new EmailEnterPasswordPresenter();
    }

    /**
     * Creates EmailCreateAccountPresenter implementation for dagger injection.
     *
     * @return EmailCreateAccountPresenter instance for dagger injection.
     */
    @Singleton
    @Provides
    public EmailCheckAccountPresenter provideEmailCheckAccountPresenter() {
        return new EmailCheckAccountPresenter();
    }

    /**
     * Creates EmailCreateAccountPresenter implementation for dagger injection.
     *
     * @return EmailCreateAccountPresenter instance for dagger injection.
     */
    @Singleton
    @Provides
    public EmailCreateAccountPresenter provideEmailCreateAccountPresenter() {
        return new EmailCreateAccountPresenter();
    }
}
