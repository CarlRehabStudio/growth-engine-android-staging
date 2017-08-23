package com.google.android.apps.miyagi.development.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.dashboard.TopicGroup;
import com.google.android.apps.miyagi.development.data.models.dashboard.TopicMenu;
import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.data.models.dashboard.UpNext;
import com.google.android.apps.miyagi.development.data.models.dashboard.UpNextAction;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.statistics.Badge;
import com.google.android.apps.miyagi.development.data.models.statistics.Page;
import com.google.android.apps.miyagi.development.data.models.statistics.Statistics;
import com.google.android.apps.miyagi.development.data.models.statistics.StatisticsPageType;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponseData;
import com.google.android.apps.miyagi.development.data.net.services.DashboardService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.AudioDownloadPrefs;
import com.google.android.apps.miyagi.development.helpers.CertificateDownloadHelper;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.AdjustableImageView;
import com.google.android.apps.miyagi.development.ui.components.widget.AutoResizeTextView;
import com.google.android.apps.miyagi.development.ui.components.widget.UserProgressBadge;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.ExpandStateListener;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.MyDashboardAdapter;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic.TopicGroupItem;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic.TopicGroupToFlexibleItemMapper;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic.UserProgressItem;
import com.google.android.apps.miyagi.development.ui.dashboard.common.DashboardAdapterCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.MenuClickCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.UpNextActionType;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.LegalMenuActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.NavigationDrawerFragment;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.NavigationDrawerCallbacks;
import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.AppBarLayout;
import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.CollapsingToolbarLayout;
import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.CoordinatorLayout;
import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.FloatingActionButton;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingPrefs;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingType;
import com.google.android.apps.miyagi.development.ui.profile.ProfileActivity;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.splash.SplashScreenActivity;
import com.google.android.apps.miyagi.development.ui.statistics.StatisticsActivity;
import com.google.android.apps.miyagi.development.ui.statistics.StatisticsTabletActivity;
import com.google.android.apps.miyagi.development.ui.web.WebViewActivity;
import com.google.android.apps.miyagi.development.utils.DisplayUtils;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.android.apps.miyagi.development.ui.dashboard.navigation.NavigationDrawerFragment.NAVIGATION_DRAWER_TAG;
import static com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity.Arg.FROM_DIAGNOSTICS;
import dagger.Lazy;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.Observable;
import rx.Subscription;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import rx.schedulers.Schedulers;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class DashboardActivity extends BaseActivity implements NavigationDrawerCallbacks, MenuClickCallback, DashboardAdapterCallback, ExpandStateListener {

    private static final int DEFAULT_TOOLBAR_ELEVATION_DP = 8;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11333;
    private static final int LIST_DISABLE_TIME = 600;

    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject Lazy<OnboardingPrefs> mOnboardingPrefs;
    @Inject Lazy<SignOutUserHelper> mSignOutUserHelper;
    @Inject Lazy<AudioDownloadPrefs> mAudioDownloadPrefs;
    @Inject Navigator mNavigator;
    @Inject DashboardService mDashboardService;
    @Inject ConfigStorage mConfigStorage;
    @Inject AudioMetaDataDatabase mAudioMetaDataDatabase;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;

    @BindView(R.id.dashboard_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.sticky_header_container) FrameLayout mStickyHeaderContainer;
    @BindView(R.id.appbar) ControllableAppBarLayout mAppBarLayout;
    @BindView(R.id.main_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @BindView(R.id.dashboard_header_container) View mContainer;
    @BindView(R.id.dashboard_header_content) View mContent;
    @BindView(R.id.image_intro) AdjustableImageView mImageIntro;
    @BindView(R.id.label_title) AutoResizeTextView mLabelTitle;
    @BindView(R.id.label_description) AutoResizeTextView mLabelDescription;
    @BindView(R.id.button_start) AppCompatButton mButtonStart;
    @BindView(R.id.button_start_container) FrameLayout mButtonStartContainer;
    @BindView(R.id.certificate_download_progress) ProgressBar mCertificateProgress;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private UserProgressBadge mUserProgressView;
    private MyDashboardAdapter mAdapter;

    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;
    private PopupMenu mPopupMenu;

    private int mCertificationBadgeCount;
    private boolean mHorizontalMode;
    private boolean mRefreshData = true;

    private Subscription mApiSubscription;
    private Subscription mMappingSubscription;
    private Subscription mCertificateDownloadSubscription;

    private List<Integer> mDownloadedTopicAudio;
    private String mCertificateUrl;

    private Handler mHandler;

    private ControllableAppBarLayout.State mState = ControllableAppBarLayout.State.EXPANDED;
    private boolean mFabNavigatingUp;
    private Intent mNavigationIntent;

    private Map<String, Boolean> mSectionsExpandState;
    private int mFirstPosition;
    private boolean mIsDrawerOpened;

    private DrawerLayout.DrawerListener mNavigationDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            snapToolbarIfNotExpanded();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            if (mNavigationIntent != null) {
                startActivity(mNavigationIntent);
                mNavigationIntent = null;
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    private Runnable mListEnableTask = () -> {
        if (mRecyclerView != null) {
            mRecyclerView.setNestedScrollingEnabled(true);
        }
    };

    /**
     * Creates calling intent for dashboard screen.
     */
    public static Intent createIntent(Context context, boolean fromDiagnostics) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(FROM_DIAGNOSTICS, fromDiagnostics);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        if (mOnboardingPrefs.get().isOnboardingRequired(OnboardingType.DASHBOARD)) {
            boolean fromDiagnostics = getIntent().getBooleanExtra(FROM_DIAGNOSTICS, false);
            startActivity(OnboardingActivity.createIntent(this, OnboardingType.DASHBOARD, fromDiagnostics));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        snapToolbarIfNotExpanded();
        dismissPopupMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.onStop();
        }
        stopCertificationDownload();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        SubscriptionHelper.unsubscribe(mMappingSubscription);
        SubscriptionHelper.unsubscribe(mCertificateDownloadSubscription);
        mApiSubscription = null;
        mMappingSubscription = null;
        mCertificateDownloadSubscription = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mErrorHelper.setOnActionClickListener(null);
        mRecyclerView = null;
        mPreloaderHelper = null;
        mErrorHelper = null;
        mAdapter = null;
        mFab = null;
        mUserProgressView = null;
    }

    private void initView() {
        setContentView(R.layout.dashboard_activity);
        ButterKnife.bind(this);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_dashboard));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addNavigationFragment();

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.dashboard_preloader));
        mPreloaderHelper.setBackgroundColor(mConfigStorage.getCommonData().getColors().getMainBackgroundColor());
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.dashboard_error_screen));
        mErrorHelper.showNavigationButton(false);
        mPreloaderHelper.show();

        if (!ViewUtils.isTablet(this)) {
            enableFab();
        } else {
            disableFab();
        }

        mHorizontalMode = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        if (mRefreshData || mCurrentSessionCache.getDashboardResponse() == null) {
            getDashboardData();
        } else {
            bindData();
        }

        mRefreshData = false;
    }

    private void enableFab() {
        mFab.setOnClickListener(view1 -> {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_down), getString(R.string.event_label_down_arrow));
            performFabClick();
        });
    }

    private void disableFab() {
        mFab.setVisibility(View.GONE);
    }

    private void performFabClick() {
        if (mFabNavigatingUp) {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_up), getString(R.string.event_label_up_arrow));
            animateDown();
        } else {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_down), getString(R.string.event_label_down_arrow));
            animateUp();
        }
        mFabNavigatingUp = !mFabNavigatingUp;
    }

    private void animateUp() {
        mAppBarLayout.setExpanded(false, true);
    }

    private void animateDown() {
        // With Sticky Headers enabled, this seems necessary to give
        // time at the RV to be in correct state before scrolling
        mAppBarLayout.setExpanded(true, true);
        mRecyclerView.post(() -> mRecyclerView.scrollToPosition(0));
    }

    private void showFab(boolean show) {
        if (!ViewUtils.isTablet(this)) {
            if (show) {
                mFab.show();
            } else {
                mFab.hide();
            }
        }
    }

    private void showToolbar(boolean visible) {
        mToolbar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateToolbar(String title) {
        mToolbar.setTitle(title == null ? "" : title);
        // refresh user progress badge in options menu
        invalidateOptionsMenu();

        updateToolbarHeight();

        mAppBarLayout.setOnStateChangeListener(toolbarState -> {
            if (!ViewUtils.isTablet(this) && toolbarState == AppBarLayout.State.IDLE && mState == AppBarLayout.State.EXPANDED) {
                disableList();
            }

            if (toolbarState == ControllableAppBarLayout.State.EXPANDED) {
                mFabNavigatingUp = false;
                ViewCompat.animate(mFab).rotation(FabConst.DOWN_ROTATION)
                        .setDuration(getResources().getInteger(R.integer.animation_fab_duration));
            } else {
                if (mState == ControllableAppBarLayout.State.EXPANDED) {
                    mFabNavigatingUp = true;
                    ViewCompat.animate(mFab).rotation(FabConst.UP_ROTATION)
                            .setDuration(getResources().getInteger(R.integer.animation_fab_duration));
                }
            }

            mState = toolbarState;
        });
    }

    private void disableList() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mRecyclerView.setNestedScrollingEnabled(false);
        mHandler.removeCallbacks(mListEnableTask);
        mHandler.postDelayed(mListEnableTask, LIST_DISABLE_TIME);
    }

    private void updateToolbarHeight() {
        if (!ViewUtils.isLandTablet(this)) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;

            int statusBarHeight = DisplayUtils.getStatusBarHeight(this);

            int newHeight;
            if (!ViewUtils.isTablet(this)) {
                newHeight = screenHeight - statusBarHeight;
            } else {
                newHeight = screenWidth / 2;
            }

            AppBarLayout.LayoutParams layoutParams = ((AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams());
            layoutParams.height = newHeight;
            mCollapsingToolbar.setLayoutParams(layoutParams);
            mCollapsingToolbar.invalidate();
        }
    }

    private void elevateToolbar(boolean elevate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (elevate) {
                mAppBarLayout.setElevation((float) ViewUtils.dp2px(this, DEFAULT_TOOLBAR_ELEVATION_DP));
            } else {
                mAppBarLayout.setElevation(0f);
            }
        }
    }

    private void snapToolbarOnOrientationChange() {
        if (mState == ControllableAppBarLayout.State.COLLAPSED) {
            mAppBarLayout.setExpanded(false);
        }
    }

    private void snapToolbarIfNotExpanded() {
        if (mState != ControllableAppBarLayout.State.EXPANDED) {
            mAppBarLayout.setExpanded(false, true);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, BaseNavigationMenuItem item) {
        if (item.getType() == BaseNavigationMenuItem.MenuType.PROFILE) {
            mNavigationIntent = ProfileActivity.createIntent(this);
        } else if (item.getType() == BaseNavigationMenuItem.MenuType.FOOTER) {
            mNavigationIntent = LegalMenuActivity.createIntent(this);
        } else if (item.getType() == BaseNavigationMenuItem.MenuType.SIGNOUT) {
            mSignOutUserHelper.get().signOut();
            mNavigationIntent = getRegistrationActivityIntent();
        } else if (item.getType() == BaseNavigationMenuItem.MenuType.WEB) {
            NavigationMenuItem webItem = (NavigationMenuItem) item;
            mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_menu_click), webItem.getContentUrl());

            Uri url = Uri.parse(webItem.getContentUrl());
            if (isBaseUrl(url)) {
                mNavigationIntent = WebViewActivity.createIntent(this, webItem.getContentUrl(), webItem.getDisplayName());
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
    }

    private boolean isBaseUrl(Uri url) {
        return mConfigStorage.getSelectedMarket().getEndpointUrl().contains(url.getHost());
    }

    private Intent getRegistrationActivityIntent() {
        Intent intent = RegisterActivity.createIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private void goToAssessment(int topicId, int actionType) {
        startActivity(AssessmentActivity.createIntent(this, topicId, actionType));
    }

    private void goToLesson(int lessonId, int topicId, boolean isTopicCompleted, boolean isLessonsCompleted) {
        mCurrentSessionCache.setLessonId(lessonId);
        mCurrentSessionCache.setTopicId(topicId);
        if (mOnboardingPrefs.get().isOnboardingRequired(OnboardingType.LESSON)) {
            startActivity(OnboardingActivity.createIntent(this, OnboardingType.LESSON, false));
        } else {
            mNavigator.navigateToLesson(this, isTopicCompleted, isLessonsCompleted);
        }
    }

    private void goToAudioPlayer(int lessonId, int topicId) {
        mCurrentSessionCache.setLessonId(lessonId);
        mCurrentSessionCache.setTopicId(topicId);
        mNavigator.navigateToAudioPlayer(this);
    }

    private void goToStatistics(Statistics statistics) {
        if (ViewUtils.isTablet(this)) {
            startActivity(StatisticsTabletActivity.createIntent(this, statistics));
        } else {
            startActivity(StatisticsActivity.createIntent(this, statistics));
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentSessionCache.getDashboardResponse() == null || mConfigStorage.readShouldUpdateDashboard()) {
            getDashboardData();
        } else if (mAdapter != null) {
            mAdapter.onStart();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissPopupMenu();
        if (!ViewUtils.isTablet(this)) {
            changeOrientation(newConfig);
        } else {
            saveInstanceState();
            removeNavigationFragment();
            initView();
        }
    }

    private void saveInstanceState() {
        mSectionsExpandState = new HashMap<>();
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                AbstractFlexibleItem item = mAdapter.getItem(i);
                if (item instanceof TopicGroupItem) {
                    TopicGroupItem header = (TopicGroupItem) item;
                    mSectionsExpandState.put(header.getTitle(), header.isExpanded());
                }
            }
            mFirstPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }
        mIsDrawerOpened = mNavigationDrawerFragment.isDrawerOpen();
    }

    private void changeOrientation(Configuration newConfig) {
        mHorizontalMode = newConfig.orientation == ORIENTATION_LANDSCAPE;
        updateToolbarHeight();
        updateNavigationDrawerWidth();
        changeHeaderOrientation(mHorizontalMode);
        snapToolbarOnOrientationChange();
    }

    private void updateNavigationDrawerWidth() {
        View view = findViewById(R.id.fragment_container);
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) view.getLayoutParams();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        try {
            width = (int) getResources().getDimension(R.dimen.navigation_drawer_width);
        } catch (Resources.NotFoundException e) {
        }
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    private void updateHeaderContent(UpNext upNext, AppColors colors) {
        mContainer.setBackgroundColor(colors.getMainBackgroundColor());
        mCollapsingToolbar.setContentScrimColor(colors.getMainBackgroundColor());

        mLabelTitle.setText(upNext.getTitle());
        mLabelDescription.setText(upNext.getSubhead());
        ColorHelper.tintButtonBackground(mButtonStart, colors.getMainCtaColor());
        mButtonStart.setText(upNext.getCta());

        changeHeaderOrientation(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE);

        mImageIntro.setContentDescription(upNext.getImageAlt());
        mButtonStart.setOnClickListener(v -> onStartButtonClick(upNext.getAction(), upNext.getCta()));

        animateHeaderView(upNext);
    }

    private void changeHeaderOrientation(boolean isHorizontalMode) {
        if (!ViewUtils.isTablet(this)) {
            setHeaderPadding();
            if (isHorizontalMode) {
                int horizontalMargin = DisplayUtils.getScreenHeight(this) / 2;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                params.rightMargin = horizontalMargin;
                mContent.setLayoutParams(params);
                params = (FrameLayout.LayoutParams) mImageIntro.getLayoutParams();
                params.leftMargin = horizontalMargin;
                mImageIntro.setLayoutParams(params);
                mImageIntro.setScaleType(ImageView.ScaleType.FIT_END);
            } else {
                int rightMargin = (int) getResources().getDimension(R.dimen.medium_margin);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                params.rightMargin = rightMargin;
                mContent.setLayoutParams(params);
                params = (FrameLayout.LayoutParams) mImageIntro.getLayoutParams();
                params.leftMargin = 0;
                mImageIntro.setLayoutParams(params);
                mImageIntro.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } else {
            int rightMargin = (int) getResources().getDimension(R.dimen.medium_margin);
            if (isHorizontalMode) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                params.rightMargin = rightMargin;
                mContent.setLayoutParams(params);
                params = (FrameLayout.LayoutParams) mImageIntro.getLayoutParams();
                params.leftMargin = 0;
                mImageIntro.setLayoutParams(params);
                mImageIntro.setScaleType(ImageView.ScaleType.FIT_END);
            } else {
                setHeaderPadding();
                int horizontalMargin = DisplayUtils.getScreenWidth(this) / 2 + rightMargin;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                params.rightMargin = horizontalMargin;
                mContent.setLayoutParams(params);
                params = (FrameLayout.LayoutParams) mImageIntro.getLayoutParams();
                params.leftMargin = horizontalMargin;
                mImageIntro.setLayoutParams(params);
                mImageIntro.setScaleType(ImageView.ScaleType.FIT_END);
            }
        }
    }

    private void setHeaderPadding() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            mContainer.setPadding(0, actionBarHeight, 0, 0);
        }
    }

    private void animateHeaderView(UpNext upNext) {
        mScreenAnimationHelper.resetAnimations();
        mScreenAnimationHelper.addFlatView(mLabelTitle);
        mScreenAnimationHelper.addFlatView(mLabelDescription);

        mScreenAnimationHelper.addCtaView(mButtonStart);

        if (upNext.getImages() != null) {
            mScreenAnimationHelper.addAsyncImageView(ImageUrlHelper.getUrlFor(GoogleApplication.getInstance().getBaseContext(), upNext.getImages()), mImageIntro);
        }

        mScreenAnimationHelper.animateScreen();
    }

    private void addNavigationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        NavigationDrawerFragment fragment = NavigationDrawerFragment.newInstnce(mRefreshData);
        ft.replace(R.id.fragment_container, fragment, NAVIGATION_DRAWER_TAG).commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentByTag(NAVIGATION_DRAWER_TAG);
        mNavigationDrawerFragment.setup(mToolbar, mDrawerLayout);
        mDrawerLayout.addDrawerListener(mNavigationDrawerListener);

        if (mIsDrawerOpened) {
            mNavigationDrawerFragment.openDrawer();
        }
    }

    private void removeNavigationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) fragmentManager.findFragmentByTag(NAVIGATION_DRAWER_TAG);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        mUserProgressView = (UserProgressBadge) menu.findItem(R.id.user_progress).getActionView();
        if (mCurrentSessionCache.getDashboardResponse() != null) {
            Statistics statistics = mCurrentSessionCache.getDashboardResponse().getResponseData().getStatictics();
            mUserProgressView.setOnClickListener(v -> goToStatistics(statistics));
            if (mConfigStorage.getCommonData() != null) {
                mUserProgressView.setColor(mConfigStorage.getCommonData().getColors().getMainCtaColor());
            }
            mUserProgressView.setProgressAnimation(mCertificationBadgeCount);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void getDashboardData() {
        if (mCurrentSessionCache.getDashboardResponse() == null) {
            // hide user progress icon in menu item
            //TODO setHasOptionsMenu(false);
        }

        mErrorHelper.setOnActionClickListener(this::getDashboardData);

        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mDashboardService.getDashboardData()
                .zipWith(mAudioMetaDataDatabase.getSavedTopicsIdList(),
                        (dashboardResponse, integers) -> {
                            mDownloadedTopicAudio = integers;
                            return dashboardResponse;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::onDataDashboardReceived, this::onDataError);
    }

    private void onDataDashboardReceived(DashboardResponse response) {
        mCurrentSessionCache.clearAllData();
        mCurrentSessionCache.setDashboardResponse(response);
        mConfigStorage.saveShouldUpdateDashboard(false);

        bindData();
    }

    private void bindData() {
        //TODO setHasOptionsMenu(true);
        DashboardResponseData responseData = mCurrentSessionCache.getDashboardResponse().getResponseData();
        mapTopicListAsyncAndSetToList(responseData);
        mAppBarLayout.setExpanded(true);
    }

    private void mapTopicListAsyncAndSetToList(final DashboardResponseData responseData) {
        SubscriptionHelper.unsubscribe(mMappingSubscription);
        mMappingSubscription = Observable.just(responseData.getTopics())
                .subscribeOn(Schedulers.newThread())
                .flatMap(topicGroup -> new TopicGroupToFlexibleItemMapper(mConfigStorage.getCommonData().getColors().getSectionBackgroundColor()).getTopicListItem(topicGroup))
                .observeOn(mainThread())
                .subscribe(abstractFlexibleItems -> {
                    Page certificationPage = responseData.getStatictics().getPages().getPageFromType(StatisticsPageType.CERTIFICATION);
                    updateToolbar(responseData.getTitle());

                    mCertificationBadgeCount = 0;
                    List<Badge> certificationBadge = mCurrentSessionCache.getDashboardResponse().getResponseData().getStatictics().getPages().getPageFromType(StatisticsPageType.CERTIFICATION).getBadges();
                    for (Badge badge : certificationBadge) {
                        if (badge.isBadgeComplete()) {
                            mCertificationBadgeCount += 1;
                        }
                    }

                    int certificationProgress = certificationPage.getProgress();
                    addYourProgressItem(abstractFlexibleItems, responseData, certificationProgress);

                    createAdapterWithItem(abstractFlexibleItems, responseData.getTopics());

                    onTerminate();
                    updateHeaderContent(responseData.getUpNext(), mConfigStorage.getCommonData().getColors());
                }, this::onDataError);
    }

    private void addYourProgressItem(List<AbstractFlexibleItem> abstractFlexibleItems, DashboardResponseData responseData, int certificationProgress) {
        TopicGroupItem userProgressHeaderItem = new TopicGroupItem(responseData.getCertification().getTitle(), mConfigStorage.getCommonData().getColors().getSectionBackgroundColor());
        userProgressHeaderItem.setExpanded(true);

        Statistics statistics = mCurrentSessionCache.getDashboardResponse().getResponseData().getStatictics();
        UserProgressItem userProgressItem = new UserProgressItem(certificationProgress, responseData.getCertification().getLabel(), mConfigStorage.getCommonData().getColors().getMainBackgroundColor());
        userProgressItem.setOnClickListener(view -> goToStatistics(statistics));
        userProgressItem.setHeader(userProgressHeaderItem);
        userProgressHeaderItem.addSubItem(userProgressItem);

        abstractFlexibleItems.add(0, userProgressHeaderItem);
    }

    private void createAdapterWithItem(List<AbstractFlexibleItem> items, TopicGroup topics) {
        for (int i = 0; i < items.size(); i++) {
            AbstractFlexibleItem item = items.get(i);
            if (item instanceof TopicGroupItem) {
                TopicGroupItem header = (TopicGroupItem) item;
                if (mSectionsExpandState != null) {
                    if (mSectionsExpandState.containsKey(header.getTitle())) {
                        header.setExpanded(mSectionsExpandState.get(header.getTitle()));
                    }
                }

                header.setExpandListener(this);
            }
        }

        mAdapter = new MyDashboardAdapter(items, mDownloadedTopicAudio, topics);

        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int offset = recyclerView.computeVerticalScrollOffset();

                // toolbar elevation
                if (offset > 0) {
                    elevateToolbar(true);
                } else {
                    elevateToolbar(false);
                }
            }
        });

        mAdapter.expandItemsAtStartUp()
                .setStickyHeaders(true, mStickyHeaderContainer)
                .setAutoScrollOnExpand(true)
                .setAutoCollapseOnExpand(false)
                .setOnlyEntryAnimation(true);
        mAdapter.setDashboardAdapterCallback(this);
        mAdapter.setMenuClickAdapterCallback(this);

        mRecyclerView.setAdapter(mAdapter);
        if (mFirstPosition > 0) {
            mRecyclerView.scrollToPosition(mFirstPosition);
            mAppBarLayout.setExpanded(false);
        }
    }

    @Override
    public void onExpanded() {
        if (mState == AppBarLayout.State.EXPANDED) {
            mAppBarLayout.setExpanded(false, true);
        }
    }

    private void onDataError(Throwable throwable) {
        //TODO setHasOptionsMenu(false);
        if (throwable instanceof RetrofitException) {
            RetrofitException exception = (RetrofitException) throwable;
            if (exception.getKind() == RetrofitException.Kind.INVALID_TOKEN) {
                ErrorsLabels errorsLabels = mConfigStorage.getLabels().getErrorsLabels();
                mErrorHelper.setButton(errorsLabels.getDeletedAccountButton());
                mErrorHelper.setMessage(errorsLabels.getDeletedAccount());
                mErrorHelper.setOnActionClickListener(this::onDeletedAccountErrorButtonClick);
                mErrorHelper.show();
            } else {
                mErrorHelper.setErrorForLoggedIn(throwable);
                mErrorHelper.show();
            }
        } else {
            mErrorHelper.setErrorForLoggedIn(throwable);
            mErrorHelper.show();
        }
        mPreloaderHelper.hide();
    }

    private void onDeletedAccountErrorButtonClick() {
        mSignOutUserHelper.get().signOut();
        Intent intent = SplashScreenActivity.createIntent(this, true);
        startActivity(intent);
        finish();
    }

    private void onSubscribe() {
        if (mCurrentSessionCache.getDashboardResponse() == null) {
            showToolbar(false);
        }
        showFab(false);
        mPreloaderHelper.show();
        mErrorHelper.hide();
    }

    private void onTerminate() {
        showToolbar(true);
        mPreloaderHelper.hide();
        showFab(true);
    }

    @Override
    public void onMenuClick(View view, Topics topic, int position, AudioDownloadStatus audioDownloadStatus) {
        dismissPopupMenu();
        mPopupMenu = new PopupMenu(this, view);
        mPopupMenu.inflate(R.menu.dashboard_topic_context);

        MenuItem menuPlayVideo = mPopupMenu.getMenu().findItem(R.id.action_play_video);
        MenuItem menuPlayAudio = mPopupMenu.getMenu().findItem(R.id.action_play_audio);
        MenuItem menuFileAction = mPopupMenu.getMenu().findItem(R.id.action_download_eject);

        TopicMenu topicMenu = mCurrentSessionCache.getDashboardResponse().getResponseData().getTopicMenu();

        menuPlayVideo.setTitle(topicMenu.getPlayVideo());
        menuPlayAudio.setTitle(topicMenu.getPlayAudio());
        if (audioDownloadStatus == AudioDownloadStatus.DELETE) {
            menuFileAction.setTitle(topicMenu.getDeleteAudio());
        } else {
            menuFileAction.setTitle(topicMenu.getDownloadAudio());
        }

        mPopupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_play_audio:
                    goToAudioPlayer(topic.getNextLesson().getId(), topic.getId());
                    return true;
                case R.id.action_play_video:
                    goToLesson(topic.getNextLesson().getId(), topic.getId(), topic.isCompleted(), topic.isLessonCompleted());
                    return true;
                case R.id.action_download_eject:
                    if (audioDownloadStatus == AudioDownloadStatus.DELETE) {
                        mAdapter.onFileActionClick(position, topic.getId());
                    } else {
                        mAdapter.onFileActionClick(position, topic.getId());
                    }
                    return true;
                default:
                    break;
            }
            return false;
        });
        mPopupMenu.show();
    }

    @Override
    public void onStartButtonClick(UpNextAction upNextAction, String buttonText) {
        mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_button), buttonText);
        switch (upNextAction.getType()) {
            case UpNextActionType.LESSON:
                goToLesson(upNextAction.getLessonId(), upNextAction.getTopicId(), false, false);
                break;
            case UpNextActionType.TOPIC_ASSESMENT:
            case UpNextActionType.CERTIFICATION_ASSESMENT:
                goToAssessment(upNextAction.getTopicId(), upNextAction.getType());
                break;
            case UpNextActionType.DOWNLOAD_CERTIFICATE:
                requestWritePermission(upNextAction.getCertificateUrl());
                break;
            case UpNextActionType.CERTIFICATION_FAILED:
            case UpNextActionType.TOPIC_LIBRARY:
            default:
                // ignore
                break;
        }
    }

    private void requestWritePermission(String certificateUrl) {
        mCertificateUrl = certificateUrl;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //TODO We should show user information why we needs WRITE_EXTERNAL_STORAGE permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            downloadCertificate(mCertificateUrl);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                downloadCertificate(mCertificateUrl);
            } else {
                //TODO permission to write external storage not granted, show info to user.
            }
        }
    }

    private void downloadCertificate(String certificateUrl) {
        SubscriptionHelper.unsubscribe(mCertificateDownloadSubscription);
        mCertificateDownloadSubscription = new CertificateDownloadHelper()
                .downloadCertificate(certificateUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .doOnSubscribe(() -> startCertificationDownload())
                .doOnTerminate(() -> stopCertificationDownload())
                .subscribe(path -> {
                    File file = new File(path);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.fromFile(file), CertificateDownloadHelper.MIME_TYPE_PDF);

                    Intent chooser = Intent.createChooser(browserIntent, null);
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (chooser.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    } else {
                        //TODO handle no activity to open file
                    }
                }, this::onCertificateDownloadError);
    }

    void startCertificationDownload() {
        mButtonStart.setVisibility(View.INVISIBLE);
        mCertificateProgress.setVisibility(View.VISIBLE);
    }

    void stopCertificationDownload() {
        mButtonStart.setVisibility(View.VISIBLE);
        mCertificateProgress.setVisibility(View.GONE);
    }

    private void onCertificateDownloadError(Throwable throwable) {
        showFab(false);
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.setOnActionClickListener(() -> onCertificateDownloadRetry());
        mErrorHelper.show();
    }

    @NonNull
    protected void onCertificateDownloadRetry() {
        mErrorHelper.hide();
        downloadCertificate(mCertificateUrl);
    }

    @Override
    public void onTopicClick(Topics topic) {
        mAnalyticsHelper.trackEvent(getString(R.string.screen_dashboard), getString(R.string.event_category_navigation), getString(R.string.event_action_select_option), topic.getTitle());

        if (topic.isLessonCompleted() && !topic.isCompleted()) {
            goToAssessment(topic.getId(), UpNextActionType.TOPIC_ASSESMENT);
        } else {
            goToLesson(topic.getNextLesson().getId(), topic.getId(), topic.isCompleted(), topic.isLessonCompleted());
        }
    }

    @Override
    public void onTopicLinkClick(Topics topic) {
        if (topic.getNextLesson().isCompleted() && !topic.isCompleted()) {
            goToAssessment(topic.getId(), UpNextActionType.TOPIC_ASSESMENT);
        } else if (!topic.getNextLesson().isCompleted()) {
            onTopicClick(topic);
        }
    }

    @Override
    public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        mConfigStorage.saveShouldUpdateDashboard(true);
        if (audioDownloadStatus == AudioDownloadStatus.DOWNLOADING) {
            if (mAudioDownloadPrefs.get().isFirstAudioDownload()) {
                showInfoSnackbar(mFab, mConfigStorage.getCommonData().getCopy().getFirstAudioDownloadMessage());
            } else {
                showInfoSnackbar(mFab, mConfigStorage.getCommonData().getCopy().getFileDownloadingMessage());
            }
        } else if (audioDownloadStatus == AudioDownloadStatus.DELETED) {
            showInfoSnackbar(mFab, mConfigStorage.getCommonData().getCopy().getFileDeletingMessage());
        }
    }

    @Override
    public void onAudioDownloadError(Throwable t, int topicId, int position) {
        showFab(false);
        mErrorHelper.setErrorForLoggedIn(t);
        mErrorHelper.setOnActionClickListener(() -> onAudioDownloadRetry(topicId, position));
        mErrorHelper.show();
    }

    private void onAudioDownloadRetry(int topicId, int position) {
        mErrorHelper.hide();
        mAdapter.onFileActionClick(position, topicId);
    }

    private void dismissPopupMenu() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
    }

    interface FabConst {
        float UP_ROTATION = -180f;
        float DOWN_ROTATION = 0f;
    }
}