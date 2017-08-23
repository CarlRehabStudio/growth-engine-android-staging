package com.google.android.apps.miyagi.development.ui.lesson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.TopicLesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.validator.LessonResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.LessonService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.ExpandableLinearLayout;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.NetworkUtils;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.functions.Func1;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by Lukasz on 24.03.2017.
 */

public abstract class LessonBaseActivity extends BaseActivity {

    protected static final int RECOVERY_DIALOG_REQUEST = 1;
    protected static final int LOADER_DELAY = 500;
    private static final String IS_TOPIC_COMPLETED = "IS_TOPIC_COMPLETED";
    private static final String IS_LESSON_COMPLETED = "IS_LESSON_COMPLETED";

    @Inject ConfigStorage mConfigStorage;
    @Inject LessonService mLessonService;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject Navigator mNavigator;
    @BindView(R.id.lesson_label_title) TextView mLabelTitle;
    @BindView(R.id.lesson_label_sub_title) TextView mLabelTopicTitle;
    @BindView(R.id.lesson_label_length) TextView mLabelLessonLength;
    @BindView(R.id.lesson_label_transcript_title) TextView mLabelTranscriptTitle;
    @BindView(R.id.lesson_label_transcript_text) TextView mLabelTranscriptText;
    @BindView(R.id.lesson_label_all_lessons_title) TextView mLabelAllLessonsTitle;
    @BindView(R.id.lesson_label_share) TextView mLabelShare;
    @BindView(R.id.lesson_web_view_description) WebView mWebViewDescription;
    @BindView(R.id.lesson_view_transcipt_title) View mViewTranscriptTitle;
    @BindView(R.id.lesson_view_transcript) ExpandableLinearLayout mViewTranscript;
    @BindView(R.id.lesson_list_topic_lesson) RecyclerView mListTopicLesson;
    @BindView(R.id.lesson_image_transcript_arrow) ImageView mImageTranscriptArrow;
    @BindView(R.id.lesson_player_image) ImageView mPlayerImage;
    @BindView(R.id.button_next) NavigationButton mButtonPractice;
    @BindView(R.id.lesson_player_container) FrameLayout mPlayerContainer;
    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.lesson_scroll_view) NestedScrollView mScrollView;
    @BindView(R.id.fab) FloatingActionButton mFab;

    protected Toolbar mToolbar;
    protected YouTubePlayerSupportFragment mPlayerProvider;
    protected String mYoutubeDeveloperKey;
    protected PlayerWrapper mPlayer;
    protected PreloaderHelper mPreloaderHelper;
    protected ErrorScreenHelper mErrorHelper;
    protected Practice mPracticeData;
    protected PracticeFeedback mPracticeFeedback;

    protected Subscription mApiSubscription;
    protected Subscription mWebLoaderSubscription;
    protected Subscription mImageLoaderSubscription;

    protected boolean mIsError;
    protected boolean mIsPageLoaded;
    protected boolean mIsImageLoaded;

    private boolean mIsTopicCompleted;
    private boolean mIsLessonsCompleted;

    public static Intent getCallingIntent(Context context, boolean isTopicCompleted, boolean isLessonsCompleted) {
        Intent intent;
        if (ViewUtils.isTablet(context)) {
            intent = new Intent(context, LessonTabletActivity.class);
        } else {
            intent = new Intent(context, LessonActivity.class);
        }
        intent.putExtra(IS_TOPIC_COMPLETED, isTopicCompleted);
        intent.putExtra(IS_LESSON_COMPLETED, isLessonsCompleted);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_activity);
        ButterKnife.bind(this);
        mBottomNav.setBackgroundColor(mConfigStorage.getCommonData().getColors().getMainBackgroundColor());
        ColorHelper.tintForeground(mFab.getDrawable(), mConfigStorage.getCommonData().getColors().getMainBackgroundColor());
        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.lesson_preloader));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.lesson_error));
        mErrorHelper.setOnActionClickListener(() -> getLessonData(mCurrentSessionCache.getLessonId()));
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);

        setupUi();

        if (!(mCurrentSessionCache.getLessonId() > 0)) {
            if (savedInstanceState != null && savedInstanceState.containsKey(CurrentSessionCache.ArgKey.LESSON_ID)) {
                mCurrentSessionCache.setLessonId(savedInstanceState.getInt(CurrentSessionCache.ArgKey.LESSON_ID));
            } else {
                //Lessons index lost (app terminate, CurrentSessionCache singleton killed) so exit.
                finish();
            }
        }

        mIsTopicCompleted = getIntent().getBooleanExtra(IS_TOPIC_COMPLETED, false);
        mIsLessonsCompleted = getIntent().getBooleanExtra(IS_LESSON_COMPLETED, false);

        LessonResponse cachedResponse = mCurrentSessionCache.getLessonResponse();
        if (cachedResponse != null && cachedResponse.getResponseData().getLesson().getLessonId() == mCurrentSessionCache.getLessonId()) {
            bindData();
        } else {
            getLessonData(mCurrentSessionCache.getLessonId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(() -> resetUi(false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECOVERY_DIALOG_REQUEST:
                // Retry initialization if user performed a recovery action
                mPlayer.initialize(mPlayerProvider, mYoutubeDeveloperKey);
                break;
            default:
                //pass result to child fragments
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.lesson_menu_audio:
                if (NetworkUtils.isOffline(this)) {
                    ToolbarHelper.setColorForStatusAndToolbar(this, ContextCompat.getColor(this, R.color.colorLightGrey));
                    mErrorHelper.setNetworkErrorForLoggedIn(mConfigStorage.getCommonData().getErrors());
                    mErrorHelper.show();
                    mIsError = true;
                } else {
                    int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
                    mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lessonId), getString(R.string.event_category_audio), getString(R.string.event_action_open), lessonId);
                    mNavigator.navigateToAudioPlayer(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayer != null && mPlayer.isInFullscreenMode() && !mIsError) {
            mPlayer.setFullscreenMode(false);
        } else {
            super.onBackPressed();
        }
    }

    private void onBtnPracticeClick() {
        if (NetworkUtils.isOffline(this)) {
            mNavigator.navigateToDashboard(this);
            return;
        }

        if (mPracticeData == null) {
            Lh.e(this, "Practice data == null");
            return;
        }

        int currLessonId = mCurrentSessionCache.getLessonId();
        int currLessonState = 0;
        List<TopicLesson> lessons = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getTopicLessons();
        for (TopicLesson lesson : lessons) {
            if (lesson.getLessonId() == currLessonId) {
                currLessonState = lesson.getLessonState();
                break;
            }
        }

        if (mIsTopicCompleted) {
            mNavigator.launchResultRightTopicCompleted(this);
        } else if (mIsLessonsCompleted) {
            mNavigator.launchResultRightLessonsCompleted(this);
        } else if (currLessonState == LessonState.COMPLETED.getValue()) {
            mNavigator.launchResultRight(this);
        } else {
            mNavigator.launchPractice(this, mPracticeData, mPracticeFeedback, PracticeResult.FAIL);
        }
    }

    private void setupUi() {
        mButtonPractice.setOnClickListener(v -> onBtnPracticeClick());

        mViewTranscriptTitle.setOnClickListener(v -> {
            if (mViewTranscript.isCollapsed()) {
                int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lessonId), getString(R.string.event_category_transcript), getString(R.string.event_action_open), lessonId);
            }
            mViewTranscript.toggle();
        });

        mViewTranscript.setOnStateChangeListener(() -> {
            if (mViewTranscript.isCollapsed()) {
                mImageTranscriptArrow.setImageResource(R.drawable.ic_arrow_down);
            } else {
                mImageTranscriptArrow.setImageResource(R.drawable.ic_arrow_up);
                markLessonAsViewed();
            }
        });
        mViewTranscript.collapse(0);
    }

    protected void resetUi(boolean loadingNewLesson) {
        if (loadingNewLesson) {
            ToolbarHelper.setColorForStatusAndToolbar(this, ContextCompat.getColor(this, R.color.colorLightGrey));
            mPlayerImage.setImageBitmap(null);
            mPlayerImage.setBackgroundColor(Color.BLACK);
        }

        mViewTranscript.collapse(0);
        if (mPlayer != null) {
            try {
                mPlayer.resetVideo();
            } catch (IllegalStateException ex) {
                recreate();
            }
        }
        mPlayerImage.setVisibility(View.VISIBLE);
        mScrollView.post(() -> mScrollView.scrollTo(0, 0));
    }

    private void markLessonAsViewed() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mLessonService.markLessonAsViewed(
                mCurrentSessionCache.getLessonId(),
                mCurrentSessionCache.getLessonXsrfToken())
                .doOnCompleted(() -> mConfigStorage.saveShouldUpdateDashboard(true))
                .subscribe(Actions.empty(), Actions.empty());
    }

    private void bindData() {
        mPreloaderHelper.show();

        LessonResponse response = mCurrentSessionCache.getLessonResponse();
        final LessonResponseData responseData = response.getResponseData();
        final Lesson lesson = responseData.getLesson();
        bindLesson(lesson);

        mPracticeData = responseData.getPractice();
        mPracticeFeedback = responseData.getFeedback();
    }

    private void bindLesson(Lesson lesson) {
        bindPlayer(lesson);
        bindLabels(lesson);
        bindButtons(lesson);
        bindTopicContent(lesson);
    }

    protected void bindPlayer(final Lesson lesson) {
        Glide.with(this)
                .load(ImageUrlHelper.getUrlFor(this, lesson.getPlayerImage()))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        onImageLoaded(lesson);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        SubscriptionHelper.unsubscribe(mImageLoaderSubscription);
                        mImageLoaderSubscription = Observable.timer(LOADER_DELAY, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aLong -> onImageLoaded(lesson), Actions.empty());
                        return false;
                    }
                })
                .into(mPlayerImage);
        mPlayerImage.setOnClickListener(v -> {
            mPlayerImage.setVisibility(View.INVISIBLE);
            markLessonAsViewed();
            if (mPlayer == null) {
                initPlayer();
            }
            mPlayer.play(lesson.getYoutubeUrl());
        });
    }

    private void bindTopicContent(final Lesson lesson) {
        TopicLessonAdapter adapter = new TopicLessonAdapter(this, lesson.getTopicLessons(), lesson.getLessonId(), selectedLesson -> {
            resetUi(true);
            mCurrentSessionCache.setLessonId(selectedLesson.getLessonId());
            getLessonData(selectedLesson.getLessonId());
        });
        mListTopicLesson.setLayoutManager(new LinearLayoutManager(this));
        mListTopicLesson.setAdapter(adapter);
    }

    private void bindButtons(final Lesson lesson) {
        mLabelShare.setText(lesson.getLessonShareTitle());

        mFab.setOnClickListener(v -> {
            mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lesson.getLessonId()), getString(R.string.event_category_share), getString(R.string.event_action_share), lesson.getLessonShareUrl());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, lesson.getLessonShareText());
            shareIntent.putExtra(Intent.EXTRA_TEXT, lesson.getLessonShareUrl());
            startActivity(Intent.createChooser(shareIntent, lesson.getLessonShareTitle()));
        });

        mButtonPractice.setText(lesson.getLessonPractice());
    }

    private void bindLabels(final Lesson lesson) {
        mToolbar.setTitle(lesson.getHeader());

        mLabelTitle.setText(lesson.getLessonTitle());
        mLabelTopicTitle.setText(lesson.getTopicTitle());
        mLabelLessonLength.setText(lesson.getLessonLength());
        mLabelTranscriptTitle.setText(lesson.getLessonTranscriptTitle());
        mLabelAllLessonsTitle.setText(HtmlHelper.fromHtmlToSpanned(lesson.getAllLessonsTitle()));
        mLabelTranscriptText.setText(HtmlHelper.fromHtml(lesson.getLessonTranscriptText()));

        mWebViewDescription.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                SubscriptionHelper.unsubscribe(mWebLoaderSubscription);
                mWebLoaderSubscription = Observable.timer(LOADER_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            mIsPageLoaded = true;
                            bindToolbar(lesson);
                            hidePreloader();
                        }, throwable -> Actions.empty());
            }
        });
        HtmlHelper.bindWebviewWithText(mWebViewDescription, lesson.getLessonDescription());
    }

    private void bindToolbar(final Lesson lesson) {
        if (mIsImageLoaded && mIsPageLoaded) {
            ToolbarHelper.setColorForStatusAndToolbar(this, lesson.getTopicColor());
        }
    }

    private void onImageLoaded(Lesson lesson) {
        mIsImageLoaded = true;
        bindToolbar(lesson);
        hidePreloader();
    }

    private void hidePreloader() {
        if (mIsPageLoaded && mIsImageLoaded) {
            mPreloaderHelper.hideWithoutDelay();
        }
    }

    protected void getLessonData(final int lessonId) {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mLessonService.getLessonData(lessonId)
                .map(LessonResponseValidator::validate)
                .concatMap(new Func1<LessonResponse, Observable<LessonResponse>>() {
                    @Override
                    public Observable<LessonResponse> call(LessonResponse lessonResponse) {
                        return Observable.zip(Observable.just(lessonResponse),
                                new PracticeImagePreloader().call(lessonResponse, LessonBaseActivity.this),
                                (t1, t2) -> t1);
                    }
                })
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::onDataLessonReceived, this::onDataError);
    }

    private void onDataLessonReceived(LessonResponse response) {
        mCurrentSessionCache.setLessonResponse(response);
        int lessonId = response.getResponseData().getLesson().getLessonId();
        mAnalyticsHelper.trackScreen(String.format(getString(R.string.screen_lesson), lessonId));

        bindData();
    }

    private void onDataError(Throwable throwable) {
        if (mPlayer != null) {
            mPlayer.setFullscreenMode(false);
        }
        mPreloaderHelper.hide();
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.show();
        mIsError = true;
    }

    private void onSubscribe() {
        mErrorHelper.hide();
        mIsError = false;
        mPreloaderHelper.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApiSubscription = null;
        mWebLoaderSubscription = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        SubscriptionHelper.unsubscribe(mWebLoaderSubscription);
        SubscriptionHelper.unsubscribe(mImageLoaderSubscription);
        releaseYtPlayer();
    }

    private void releaseYtPlayer() {
        if (mPlayer != null && isFinishing()) {
            mPlayer.setPlayerStateListener(null);
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CurrentSessionCache.ArgKey.LESSON_ID, mCurrentSessionCache.getLessonId());
    }

    protected abstract void initPlayer();
}
