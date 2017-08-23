package com.google.android.apps.miyagi.development.ui.audio.player;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.data.models.audio.AudioMetaData;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseValidator;
import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;
import com.google.android.apps.miyagi.development.data.net.services.AudioService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.audio.AudioSeekBar;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.AudioPlayerListAdapter;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.AudioPlayerListAdapterCallback;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.items.AudioOfflineListTopicItem;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.items.AudioPlayerListHeaderItem;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.items.AudioPlayerListLessonItem;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioDownloadService;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioPlaybackService;
import com.google.android.apps.miyagi.development.ui.audio.service.IPlayback;
import com.google.android.apps.miyagi.development.ui.audio.service.Player;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.offline.OfflineAdapterAction;
import com.google.android.apps.miyagi.development.ui.offline.adapter.item.OfflineListTopicItem;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOAD;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import org.parceler.Parcels;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 28.03.2017.
 */

public abstract class AudioPlayerBaseActivity extends BaseActivity implements AudioPlayerListAdapterCallback, IPlayback.Callback {

    private static final int NO_TOPIC_ID = -1;
    private static final int UPDATE_PROGRESS_INTERVAL = 500;
    private static final int SKIP_TOPIC_TIME = 2500;

    @Inject AudioService mAudioService;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject ConfigStorage mConfigStorage;
    @Inject AudioDownloadService mAudioDownloadService;
    @Inject AudioMetaDataDatabase mAudioMetaDataDatabase;
    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject Navigator mNavigator;
    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.audio_scroll_view) NestedScrollView mAudioScrollView;
    @BindView(R.id.button_forward) ImageView mButtonForward;
    @BindView(R.id.audio_player_activity_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.audio_player_container) LinearLayout mAudioPlayerContainer;
    @BindView(R.id.label_topic_title) TextView mLabelTopicTitle;
    @BindView(R.id.label_lesson_title) TextView mLabelLessonTitle;
    @BindView(R.id.label_cue_point) TextView mLabelCuePoint;
    @BindView(R.id.progress_bar) AudioSeekBar mProgressBar;
    @BindView(R.id.label_duration) TextView mLabelDuration;
    @BindView(R.id.button_rewind) ImageView mButtonBackward;
    @BindView(R.id.button_rewind15) ImageView mButtonBackward15;
    @BindView(R.id.button_play_pause) FloatingActionButton mButtonPlayPause;
    @BindView(R.id.button_forward15) ImageView mButtonForward15;
    @BindView(R.id.audio_buffer_progress) ProgressBar mBufferProgress;
    @Nullable @BindView(R.id.fab) FloatingActionButton mFab;
    private MenuItem mMenuItemPlayPause;
    private MenuItem mMenuItemFileAction;
    private MenuItem mMenuItemFileActionText;
    private MenuItem mMenuItemShowTranscript;
    protected Toolbar mToolbar;
    protected PopupMenu mPopupMenu;
    protected AudioPlayerListAdapter mListAdapter;
    private PreloaderHelper mLoaderScreenHelper;
    private ErrorScreenHelper mErrorScreenHelper;

    private int[] mTopicDuration;
    private Mode mMode;
    private String mCurrentFileUrl;
    private AudioDownloadStatus mAudioDownloadStatus;

    private PublishSubject<Long> mPlayerUpdateSubject;
    private Subscription mPlayerUpdateSubscription;
    private Subscription mPlayerTimerSubscription;
    private Subscription mApiSubscription;
    private Subscription mDownloadSubscription;
    private Subscription mCacheSubscription;
    private Subscription mNextLessonSubscription;
    private AnimatorSet mAnimatorSet;

    protected AudioResponseData mResponseData;
    protected IPlayback mPlayer = IPlayback.EMPTY;
    protected int mCurrentLessonIndex = 0;
    private int mTopicId = NO_TOPIC_ID;
    protected boolean mIsServiceBound;
    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioPlaybackService playbackService = ((AudioPlaybackService.LocalBinder) service).getService();
            onPlaybackServiceBound(playbackService);
        }

        public void onServiceDisconnected(ComponentName className) {
            onPlaybackServiceUnbound();
        }
    };

    /**
     * Creates calling to AudioPlayerActivity or AudioPlayerTabletActivity.
     *
     * @param context the context.
     * @param mode    the internet mode.
     *
     * @return the AudioPlayerActivity calling intent.
     */
    public static Intent getCallingIntent(Context context, Mode mode) {
        Intent audioIntent;
        if (ViewUtils.isTablet(context)) {
            audioIntent = AudioPlayerTabletActivity.getCallingIntent(context, mode);
        } else {
            audioIntent = AudioPlayerActivity.getCallingIntent(context, mode);
        }
        audioIntent.putExtra(ArgKey.MODE, Parcels.wrap(mode));
        return audioIntent;
    }

    protected void onPlaybackServiceBound(AudioPlaybackService playbackService) {
        mIsServiceBound = true;
        mPlayer = playbackService;
        mPlayer.addCallback(this);
        mPlayer.prepare(mCurrentFileUrl, false);
    }

    protected void onPlaybackServiceUnbound() {
        if (mIsServiceBound) {
            mPlayer = IPlayback.EMPTY;
            unbindService(mConnection);

            Intent stopIntent = new Intent(AudioPlayerBaseActivity.this, AudioPlaybackService.class);
            stopIntent.setAction(AudioPlaybackService.ACTION_STOP_SERVICE);
            startService(stopIntent);

            mIsServiceBound = false;
            mCurrentLessonIndex = 0;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(ArgKey.NEXT_TOPIC, false)) {
            onPlaybackServiceUnbound();
            mCurrentSessionCache.removeAudioCache();
            initialize();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mToolbar.setNavigationIcon(R.drawable.ic_close_black);

        mLoaderScreenHelper = new PreloaderHelper(findViewById(R.id.loader_screen));
        mErrorScreenHelper = new ErrorScreenHelper(findViewById(R.id.error_screen));
        mErrorScreenHelper.setOnActionClickListener(this::loadData);
        mErrorScreenHelper.setAudioModeListener(this::finish);
        mErrorScreenHelper.setOnNavigationClickListener(this::onBackPressed);

        mMode = Parcels.unwrap(getIntent().getParcelableExtra(ArgKey.MODE));

        mCurrentSessionCache.removeAudioCache();

        initialize();
    }

    public int getLayoutRes() {
        return R.layout.audio_player_activity;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    protected void initialize() {
        initData();
        initUi();
        initRecyclerView();
        loadData();
    }

    private void initData() {
        mTopicId = mCurrentSessionCache.getTopicId();
        mAnalyticsHelper.trackScreen(String.format(getString(R.string.screen_topic_audio), mTopicId));
    }

    private void initUi() {
        onInitUi();
        mLabelCuePoint.setText(TimeUtils.formatDuration(0));
        mLabelDuration.setText(TimeUtils.formatDuration(0));

        AppColors appColors = mConfigStorage.getCommonData().getColors();

        mButtonPlayPause.setBackgroundTintList(ColorStateList.valueOf(appColors.getMainCtaColor()));
        mAudioPlayerContainer.setBackgroundColor(appColors.getMainBackgroundColor());
        mProgressBar.setTintColor(appColors.getMainCtaColor());
    }

    protected abstract void onInitUi();

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        FlexibleItemDecoration divider = new FlexibleItemDecoration(getApplicationContext());
        divider.withDrawOver(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(divider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startObservablePlayerChanges();
        startIntervalPlayerChanges();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
        mToolbar.hideOverflowMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.audio_player, menu);
        mMenuItemPlayPause = menu.findItem(R.id.action_play_pause);
        mMenuItemFileAction = menu.findItem(R.id.action_download_eject);
        mMenuItemFileActionText = menu.findItem(R.id.action_download_eject_text);
        mMenuItemShowTranscript = menu.findItem(R.id.action_show_transcript);
        setMenuItem();
        bindFileActionItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_download_eject:
            case R.id.action_download_eject_text:
                handleFileAction();
                return true;
            case R.id.action_play_pause:
                handlePlayPause();
                return true;
            case R.id.action_show_transcript:
                AudioLesson lesson = mResponseData.getLessons().get(mCurrentLessonIndex);
                mNavigator.navigateToAudioTranscript(this, lesson.getLessonId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void loadData() {
        if (mMode == Mode.OFFLINE) {
            loadDataInOfflineMode();
        } else {
            if (mCurrentSessionCache.getAudioResponseData() != null) {
                Observable.just(mCurrentSessionCache.getAudioResponseData())
                        .zipWith(mAudioMetaDataDatabase.getAudioMetaDataForTopicId(mTopicId), (audioResponse, audioMetaData) -> {
                            if (audioMetaData != null) {
                                mAudioDownloadStatus = AudioDownloadStatus.DELETE;
                            } else {
                                mAudioDownloadStatus = DOWNLOAD;
                            }
                            return audioResponse;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::onSubscribe)
                        .subscribe(this::onDataReceived);
            } else {
                SubscriptionHelper.unsubscribe(mCacheSubscription);
                mCacheSubscription = mAudioMetaDataDatabase.getAudioMetaDataForTopicId(mTopicId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::onSubscribe)
                        .subscribe(audioMetaData -> {
                            if (audioMetaData == null) {
                                loadDataInOnlineMode();
                            } else {
                                onDataOfflineReceived(audioMetaData);
                            }
                        });
            }
        }
    }

    private void loadDataInOnlineMode() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = mAudioService.getTopicAudio(mTopicId)
                .zipWith(mAudioMetaDataDatabase.getAudioMetaDataForTopicId(mTopicId), (audioResponse, audioMetaData) -> {
                    if (audioMetaData != null) {
                        mAudioDownloadStatus = AudioDownloadStatus.DELETE;
                    } else {
                        mAudioDownloadStatus = DOWNLOAD;
                    }
                    return audioResponse;
                })
                .map(AudioResponseValidator::validate)
                .map(BaseResponse::getResponseData)
                .doOnSubscribe(this::onSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDataReceived, this::onDataError);
    }

    private void loadDataInOfflineMode() {
        SubscriptionHelper.unsubscribe(mDownloadSubscription);
        mDownloadSubscription = mAudioMetaDataDatabase.getAudioMetaDataForTopicId(mTopicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(audioMetaData -> {
                    mAudioDownloadStatus = AudioDownloadStatus.DELETE;
                    if (audioMetaData == null) {
                        return Observable.error(new NullPointerException());
                    } else {
                        return Observable.just(audioMetaData);
                    }
                })
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::onDataOfflineReceived, this::onDataError);
    }

    private void onSubscribe() {
        mErrorScreenHelper.hide();
        mLoaderScreenHelper.show();
    }

    private void onDataReceived(AudioResponseData audioResponseData) {
        mResponseData = audioResponseData;
        mCurrentSessionCache.setAudioResponseData(mResponseData, Mode.ONLINE);
        mCurrentFileUrl = audioResponseData.getFileUrl();

        bindData();
    }

    private void onDataOfflineReceived(AudioMetaData audioMetaData) {
        mResponseData = audioMetaData.getAudioResponseData();
        mCurrentSessionCache.setAudioResponseData(audioMetaData.getAudioResponseData(), Mode.OFFLINE);
        mCurrentFileUrl = audioMetaData.getAudioFilePath();
        mAudioDownloadStatus = AudioDownloadStatus.DELETE;

        bindData();
    }

    private void onDataError(Throwable throwable) {
        mLoaderScreenHelper.hide();
        mErrorScreenHelper.setErrorForLoggedIn(throwable);
        mErrorScreenHelper.show();
    }

    protected void bindData() {
        List<AudioLesson> audioLessons = mResponseData.getLessons();

        for (int i = 0; i < audioLessons.size(); i++) {
            if (mCurrentSessionCache.getLessonId() == audioLessons.get(i).getLessonId()) {
                mCurrentLessonIndex = i;
            }
        }

        onBindData(audioLessons);

        if (!mIsServiceBound) {
            Intent playBackService = new Intent(this, AudioPlaybackService.class);
            playBackService.putExtra(AudioPlaybackService.ArgKey.TOPIC_TITLE, mResponseData.getTitle());
            playBackService.putExtra(AudioPlaybackService.ArgKey.AUDIO_LESSONS, Parcels.wrap(List.class, mResponseData.getLessons()));
            bindService(playBackService, mConnection, BIND_AUTO_CREATE);
        }
    }

    protected void onBindData(List<AudioLesson> audioLessons) {
        mToolbar.setTitle(mConfigStorage.getCommonData().getCopy().getAudioHeader());

        createAdapter();
        addNextLessonItem();

        AudioLesson currentAudioLesson = audioLessons.get(mCurrentLessonIndex);
        mLabelTopicTitle.setText(mResponseData.getTitle());
        mLabelLessonTitle.setText(currentAudioLesson.getTitle());

        setMenuItem();
        bindFileActionItem();
    }

    protected void addNextLessonItem() {
        addNextLessonItem(0);
    }

    private void addNextLessonItem(int replaceAtPosition) {
        if (mMode == Mode.OFFLINE) {
            SubscriptionHelper.unsubscribe(mNextLessonSubscription);
            mNextLessonSubscription = mAudioMetaDataDatabase.findNextLesson(mResponseData.getTopicId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(nextTopicAudioLesson -> {
                        addNextLessonItem(replaceAtPosition, nextTopicAudioLesson);
                    }, throwable -> Actions.empty());
        }
    }

    private void addNextLessonItem(int replaceAtPosition, AudioResponseData nextTopicAudioLesson) {
        if (nextTopicAudioLesson != null) {
            addNextLessonItemOrReplaceAtPosition(replaceAtPosition, nextTopicAudioLesson);
        } else if (replaceAtPosition > 0) {
            removeNextLessonSection(replaceAtPosition);
        }
    }

    private void addNextLessonItemOrReplaceAtPosition(int replaceAtPosition, AudioResponseData nextTopicAudioLesson) {
        AudioPlayerListHeaderItem nextLessonHeaderItem = null;
        if (replaceAtPosition == 0) {
            nextLessonHeaderItem = new AudioPlayerListHeaderItem(
                    mResponseData.getNextTopicHeader(),
                    mConfigStorage.getCommonData().getColors().getSectionBackgroundColor());

        } else {
            mListAdapter.removeItem(replaceAtPosition);
            AbstractFlexibleItem item = mListAdapter.getItem(mListAdapter.getItemCount() - 1);
            if (item instanceof AudioPlayerListHeaderItem) {
                nextLessonHeaderItem = ((AudioPlayerListHeaderItem) item);
            }
        }

        if (nextLessonHeaderItem != null) {
            OfflineListTopicItem offlineListTopicItem = createOfflineListTopicItem(nextTopicAudioLesson, nextLessonHeaderItem);

            if (replaceAtPosition == 0) {
                mListAdapter.addItem(nextLessonHeaderItem);
            } else {
                mListAdapter.addSubItem(mListAdapter.getGlobalPositionOf(nextLessonHeaderItem),
                        nextLessonHeaderItem.getSubItems().indexOf(offlineListTopicItem),
                        offlineListTopicItem);
            }
            mListAdapter.expandAll();
        }
    }

    private void removeNextLessonSection(final int replaceAtPosition) {
        View header = mRecyclerView.findViewHolderForAdapterPosition(replaceAtPosition - 1).itemView;
        View item = mRecyclerView.findViewHolderForAdapterPosition(replaceAtPosition).itemView;

        int newScrollY = mAudioScrollView.getChildAt(0).getHeight() - mAudioScrollView.getHeight() - item.getHeight() - header.getHeight();

        ObjectAnimator scrollAnimator = ObjectAnimator.ofInt(mAudioScrollView, "scrollY", newScrollY);
        ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(item, "alpha", 1.0f, 0.0f);
        ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mRecyclerView.findViewHolderForAdapterPosition(replaceAtPosition - 1).itemView, "alpha", 1.0f, 0.0f);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(scrollAnimator, alphaAnimator1, alphaAnimator2);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mListAdapter.removeItem(replaceAtPosition);
                mListAdapter.removeItem(replaceAtPosition - 1);
                mAnimatorSet = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimatorSet.setDuration(getResources().getInteger(R.integer.animation_duration));
        mAnimatorSet.start();
    }

    @NonNull
    private OfflineListTopicItem createOfflineListTopicItem(AudioResponseData nextTopicAudioLesson, AudioPlayerListHeaderItem nextLessonHeaderItem) {
        int mainCtaColor = mConfigStorage.getCommonData().getColors().getMainCtaColor();

        AudioOfflineListTopicItem offlineListTopicItem = new AudioOfflineListTopicItem(nextTopicAudioLesson, mainCtaColor);
        offlineListTopicItem.setHeader(nextLessonHeaderItem);
        nextLessonHeaderItem.addSubItem(offlineListTopicItem);
        return offlineListTopicItem;
    }

    protected void setMenuItem() {
        if (mResponseData != null) {
            if (mMenuItemShowTranscript != null) {
                mMenuItemShowTranscript.setTitle(mResponseData.getMenuViewTranscript());
            }

            if (mMenuItemFileAction != null) {
                mMenuItemFileAction.setTitle(mResponseData.getDeleteAudioLabel());
            }

            if (mMenuItemFileActionText != null) {
                mMenuItemFileActionText.setTitle(mResponseData.getDeleteAudioLabel());
            }
        }
    }

    protected void bindFileActionItem() {
        if (mResponseData != null && mMenuItemFileAction != null && mMenuItemFileActionText != null) {
            if (mAudioDownloadStatus == AudioDownloadStatus.DELETE) {
                mMenuItemFileAction.setIcon(R.drawable.ic_eject);
                mMenuItemFileAction.setTitle(mResponseData.getDeleteAudioLabel());
                mMenuItemFileActionText.setTitle(mResponseData.getDeleteAudioLabel());
            } else {
                mMenuItemFileAction.setIcon(R.drawable.ic_file_download);
                mMenuItemFileAction.setTitle(mResponseData.getDownloadAudioLabel());
                mMenuItemFileActionText.setTitle(mResponseData.getDownloadAudioLabel());
            }
        }
    }

    protected void createAdapter() {
        List<AudioLesson> audioLessons = mResponseData.getLessons();

        AudioPlayerListHeaderItem lessonsInTopicHeader = new AudioPlayerListHeaderItem(
                mResponseData.getLessonsInTopicHeader(),
                mConfigStorage.getCommonData().getColors().getSectionBackgroundColor());

        List<AbstractFlexibleItem> items = new ArrayList<>();
        items.add(lessonsInTopicHeader);

        int mainCtaColor = mConfigStorage.getCommonData().getColors().getMainCtaColor();
        for (AudioLesson audioLesson : audioLessons) {
            AudioPlayerListLessonItem audioLessonItem = new AudioPlayerListLessonItem(audioLesson, mainCtaColor);
            audioLessonItem.setHeader(lessonsInTopicHeader);
            lessonsInTopicHeader.addSubItem(audioLessonItem);
        }

        mListAdapter = new AudioPlayerListAdapter(items, null);

        mListAdapter.setAudioPlayerListAdapterCallback(this);
        mListAdapter.setMode(SelectableAdapter.Mode.SINGLE);
        mListAdapter
                .setAutoScrollOnExpand(false)
                .setAutoCollapseOnExpand(false);

        mListAdapter.setOfflineAdapterCallback(new OfflineAdapterAction.Callback() {
            @Override
            public void onTopicClick(int topicId) {
                openNextTopic(topicId);
            }

            @Override
            public void onMenuClick(View view, int topicId, int position) {
                if (mPopupMenu != null) {
                    mPopupMenu.dismiss();
                    mPopupMenu = null;
                }
                mPopupMenu = new PopupMenu(view.getContext(), view);
                mPopupMenu.inflate(R.menu.offline_dashboard_topic_context);

                MenuItem menuPlayAudio = mPopupMenu.getMenu().findItem(R.id.action_play_audio);
                MenuItem menuFileAction = mPopupMenu.getMenu().findItem(R.id.action_download_eject);

                menuPlayAudio.setTitle(mResponseData.getPlayAudioLabel());
                menuFileAction.setTitle(mResponseData.getDeleteAudioLabel());

                mPopupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_play_audio:
                            openNextTopic(topicId);
                            return true;
                        case R.id.action_download_eject:
                            mListAdapter.onFileActionClick(position, topicId);
                            return true;
                        default:
                            break;
                    }
                    return false;
                });
                mPopupMenu.show();
            }

            @Override
            public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
                mConfigStorage.saveShouldUpdateDashboard(true);
                if (audioDownloadStatus == AudioDownloadStatus.DOWNLOADING) {
                    showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getFileDownloadingMessage());
                } else if (audioDownloadStatus == AudioDownloadStatus.DOWNLOAD) {
                    showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getFileDeletingMessage());
                }
            }
        });

        mListAdapter.expandAll();
        mListAdapter.toggleSelection(mCurrentLessonIndex + 1);
        mRecyclerView.setAdapter(mListAdapter);
    }

    private void openNextTopic(int topicId) {
        mCurrentSessionCache.setTopicId(topicId);
        mAudioScrollView.fullScroll(View.FOCUS_UP);
        Intent audioPlayerIntent = getCallingIntent(this, mMode);
        audioPlayerIntent.putExtra(ArgKey.NEXT_TOPIC, true);
        startActivity(audioPlayerIntent);
    }

    protected void bindButtons() {
        mButtonPlayPause.setOnClickListener(v -> handlePlayPause());
        mButtonForward15.setOnClickListener(v -> onForwardButtonClick());
        mButtonBackward15.setOnClickListener(v -> onBackwardButtonClick());

        mButtonForward.setOnClickListener(v -> onNextButtonClick());
        mButtonBackward.setOnClickListener(v -> onPreviousButtonClick());

        if (mFab != null) {
            mFab.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "[T] Missing url in api");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://google.com");
                startActivity(Intent.createChooser(shareIntent, "[T] Missing title in api"));
            });
        }
    }

    protected void handleSeekBar() {
        mProgressBar.setOnSeekBarChangeListener(new AudioSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(AudioSeekBar seekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressText((int) (mTopicDuration[mCurrentLessonIndex] * progress));
                }
            }

            @Override
            public void onStartTrackingTouch(AudioSeekBar seekBar) {
                stopIntervalPlayerChanges();
            }

            @Override
            public void onStopTrackingTouch(AudioSeekBar seekBar) {
                int previousDuration = 0;
                for (int i = 0; i < mCurrentLessonIndex; i++) {
                    previousDuration += mTopicDuration[i];
                }

                mPlayer.seekTo((int) (mTopicDuration[mCurrentLessonIndex] * seekBar.getProgress()) + previousDuration);
                startIntervalPlayerChanges();
            }
        });
    }

    private void handlePlayPause() {
        if (mPlayer.getState() == PlaybackStateCompat.STATE_STOPPED) {
            mPlayer.prepare(mCurrentFileUrl, true);
        } else {
            if (mPlayer.isPlaying()) {
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_pause), mCurrentLessonIndex);
                mPlayer.pause();
            } else {
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_play), mCurrentLessonIndex);
                mPlayer.play();
            }
        }
    }

    private void onForwardButtonClick() {
        int currentCuePoint = (int) (mResponseData.getLessons().get(mCurrentLessonIndex).getCuePoint() * 1000);
        int currentPosition = mPlayer.getCurrentStreamPosition() - currentCuePoint;

        if (currentPosition + Player.SEEK_TIME < mTopicDuration[mCurrentLessonIndex]) {
            mPlayer.forward();
        }

        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_skip_forward), mCurrentLessonIndex);
        updatePlayerState();
    }

    private void onBackwardButtonClick() {
        int currentCuePoint = (int) (mResponseData.getLessons().get(mCurrentLessonIndex).getCuePoint() * 1000);
        int currentPosition = mPlayer.getCurrentStreamPosition() - currentCuePoint;

        if (currentPosition - Player.SEEK_TIME > 0) {
            mPlayer.backward();
        }

        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_skip_back), mCurrentLessonIndex);
        updatePlayerState();
    }

    private void onPreviousButtonClick() {
        List<AudioLesson> audioLessons = mResponseData.getLessons();
        AudioLesson currentLesson = audioLessons.get(mCurrentLessonIndex);
        if (mCurrentLessonIndex > 0) {
            if ((mPlayer.getCurrentStreamPosition() - currentLesson.getCuePoint() * 1000) < SKIP_TOPIC_TIME) {
                onLessonChange(audioLessons.get(mCurrentLessonIndex - 1));
            } else {
                onLessonChange(audioLessons.get(mCurrentLessonIndex));
            }
        } else {
            onLessonChange(currentLesson);
        }
        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_chapter_back), mCurrentLessonIndex);
        updatePlayerState();
    }

    private void onNextButtonClick() {
        List<AudioLesson> audioLessons = mResponseData.getLessons();
        if (mCurrentLessonIndex < audioLessons.size() - 1) {
            onLessonChange(audioLessons.get(mCurrentLessonIndex + 1));
        }
        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_chapter_forward), mCurrentLessonIndex);
        updatePlayerState();
    }

    private void startIntervalPlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerTimerSubscription);
        mPlayerTimerSubscription = Observable.interval(UPDATE_PROGRESS_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    if (mPlayerUpdateSubject != null) {
                        mPlayerUpdateSubject.onNext(aLong);
                    } else {
                        SubscriptionHelper.unsubscribe(mPlayerTimerSubscription);
                        mPlayerTimerSubscription = null;
                    }
                });
    }

    private void stopIntervalPlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerTimerSubscription);
        mPlayerTimerSubscription = null;
    }

    private void startObservablePlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerUpdateSubscription);

        mPlayerUpdateSubject = PublishSubject.create();
        mPlayerUpdateSubscription = mPlayerUpdateSubject
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mTopicDuration != null) {
                        List<AudioLesson> lessons = mResponseData.getLessons();
                        long currentTime = mPlayer.getCurrentStreamPosition();

                        int currentLessonIndex = 0;
                        for (int i = 0; i < lessons.size(); i++) {
                            float cuePoint = (lessons.get(i).getCuePoint()) * 1000;
                            if (currentTime >= cuePoint) {
                                currentLessonIndex = i;
                            }
                        }

                        if (mCurrentLessonIndex != currentLessonIndex) {
                            mCurrentLessonIndex = currentLessonIndex;
                            onCuePointChanged();
                        }

                        float currentCue = lessons.get(currentLessonIndex).getCuePoint() * 1000;
                        float progress = (currentTime - currentCue) / (mTopicDuration[currentLessonIndex]);


                        updateProgressText((int) (currentTime - currentCue));
                        mProgressBar.setProgress(progress);
                    }
                });
    }

    private void stopObservablePlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerUpdateSubscription);
        mPlayerUpdateSubscription = null;
    }

    protected void updateProgressText(int progress) {
        mLabelCuePoint.setText(TimeUtils.formatDuration(progress));
    }

    protected void updateDurationText(int duration) {
        mLabelDuration.setText(TimeUtils.formatDuration(duration));
    }

    private void onCuePointChanged() {
        List<AudioLesson> audioLessons = mResponseData.getLessons();
        AudioLesson lesson = audioLessons.get(mCurrentLessonIndex);

        updateDurationText(mTopicDuration[mCurrentLessonIndex]);
        mListAdapter.toggleSelection(mCurrentLessonIndex + 1);
        mLabelLessonTitle.setText(lesson.getTitle());
    }

    protected void updatePlaybackState(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                mButtonPlayPause.setImageResource(R.drawable.ic_player_pause);
                mButtonPlayPause.setClickable(true);
                mMenuItemPlayPause.setTitle(mResponseData.getPauseAudioLabel());
                mBufferProgress.setVisibility(View.GONE);
                break;
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_BUFFERING:
                mButtonPlayPause.setImageDrawable(null);
                mButtonPlayPause.setClickable(false);
                mBufferProgress.setVisibility(View.VISIBLE);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_STOPPED:
                mButtonPlayPause.setImageResource(R.drawable.ic_player_play);
                mButtonPlayPause.setClickable(true);
                mMenuItemPlayPause.setTitle(mResponseData.getPlayAudioLabel());
                mBufferProgress.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mListAdapter.onPlayStatusChanged(state == PlaybackStateCompat.STATE_PLAYING);
    }

    private void updatePlayerState() {
        if (mPlayerUpdateSubject != null) {
            mPlayerUpdateSubject.onNext(0L);
        }
    }

    private void handleFileAction() {
        SubscriptionHelper.unsubscribe(mDownloadSubscription);
        mDownloadSubscription = Observable.just(mAudioDownloadStatus)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(audioDownloadStatus -> {
                    onSubscribeDownload();
                    Observable<Boolean> actionObservable = Observable.empty();
                    if (audioDownloadStatus == DOWNLOAD) {
                        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_topic_audio), mTopicId), getString(R.string.event_category_audio), getString(R.string.event_action_download), mTopicId);
                        mConfigStorage.saveShouldUpdateDashboard(true);
                        actionObservable = mAudioDownloadService.downloadAudio(mTopicId);
                        showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getFileDownloadingMessage());
                    } else if (audioDownloadStatus == AudioDownloadStatus.DELETE) {
                        mConfigStorage.saveShouldUpdateDashboard(true);
                        actionObservable = mAudioDownloadService.deleteAudio(mTopicId);
                        showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getFileDeletingMessage());
                    }
                    return actionObservable;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(this::onTerminateDownload)
                .subscribe(this::onDownloadFileReceived, this::onDownloadFileError);
    }

    private void onDownloadFileReceived(Boolean download) {
        if (mAudioDownloadStatus == DOWNLOAD) {
            mAudioDownloadStatus = AudioDownloadStatus.DELETE;
        } else if (mAudioDownloadStatus == AudioDownloadStatus.DELETE) {
            mAudioDownloadStatus = DOWNLOAD;
            if (mMode == Mode.OFFLINE) {
                finish();
            }
        }
        bindFileActionItem();
    }

    private void onDownloadFileError(Throwable throwable) {
        mErrorScreenHelper.setErrorForLoggedIn(throwable);
        mErrorScreenHelper.show();
    }

    private void onSubscribeDownload() {
        mMenuItemFileAction.setActionView(R.layout.progress_menu_layout);
    }

    private void onTerminateDownload() {
        mMenuItemFileAction.setActionView(null);
    }

    protected void onLessonChange(AudioLesson audioLesson) {
        stopIntervalPlayerChanges();
        List<AudioLesson> audioLessons = mResponseData.getLessons();
        for (int i = 0; i < audioLessons.size(); i++) {
            if (audioLesson.getLessonId() == audioLessons.get(i).getLessonId()) {
                mCurrentLessonIndex = i;
                break;
            }
        }

        mPlayer.seekTo((int) (audioLesson.getCuePoint() * 1000));
        onCuePointChanged();
        updatePlayerState();
        startIntervalPlayerChanges();
    }

    @Override
    public void onLessonItemClick(AudioLesson audioLesson) {
        stopIntervalPlayerChanges();
        int lessonIndex = -1;
        List<AudioLesson> audioLessons = mResponseData.getLessons();
        for (int i = 0; i < audioLessons.size(); i++) {
            if (audioLesson.getLessonId() == audioLessons.get(i).getLessonId()) {
                lessonIndex = i;
            }
        }

        if (mCurrentLessonIndex != lessonIndex) {
            mCurrentLessonIndex = lessonIndex;
            mPlayer.seekTo((int) (audioLesson.getCuePoint() * 1000));
            onCuePointChanged();
            updatePlayerState();
        }

        if (mPlayer.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
        startIntervalPlayerChanges();
    }

    @Override
    public void onNextLessonChange(int position) {
        addNextLessonItem(position);
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(state);
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            startIntervalPlayerChanges();
        } else {
            stopIntervalPlayerChanges();
        }
    }

    public void onPrepared() {
        mProgressBar.setProgress(0f);

        generateTopicDuration();

        if (mCurrentSessionCache.getLessonId() != mResponseData.getLessons().get(0).getLessonId()) {
            for (AudioLesson audioLesson : mResponseData.getLessons()) {
                if (mCurrentSessionCache.getLessonId() == audioLesson.getLessonId()) {
                    onLessonChange(audioLesson);
                    break;
                }
            }
        }

        updateDurationText(mTopicDuration[mCurrentLessonIndex]);
        updateProgressText(0);

        bindButtons();
        handleSeekBar();

        mLoaderScreenHelper.hideWithoutDelay();
        showWarningForOffline();
    }

    @Override
    public void onComplete() {
        updateProgressText(mTopicDuration[mCurrentLessonIndex]);
        mProgressBar.setProgress(1f);
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
        stopIntervalPlayerChanges();
    }

    @Override
    public void onError() {
        mErrorScreenHelper.setErrorForLoggedIn(new RetrofitException(null, null, null, RetrofitException.Kind.UNEXPECTED, null, null));
        mErrorScreenHelper.show();
        mLoaderScreenHelper.hide();
    }

    protected void showWarningForOffline() {
        if (mMode == Mode.OFFLINE) {
            if (mConfigStorage.getCommonData() != null) {
                Observable.timer(PreloaderHelper.LOADER_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getAudioInfo()),
                                throwable -> Actions.empty());
            }
        }
    }

    protected void generateTopicDuration() {
        mTopicDuration = new int[mResponseData.getLessons().size()];

        List<AudioLesson> audioLessons = mResponseData.getLessons();

        for (int i = 0; i < audioLessons.size(); i++) {
            if (i < audioLessons.size() - 1) {
                mTopicDuration[i] = (int) Math.abs(audioLessons.get(i).getCuePoint() - audioLessons.get(i + 1).getCuePoint()) * 1000;
            } else {
                mTopicDuration[i] = (int) (mPlayer.getCurrentStreamDuration() - audioLessons.get(i).getCuePoint() * 1000);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPlaybackServiceUnbound();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopIntervalPlayerChanges();
        stopObservablePlayerChanges();
        if (mListAdapter != null) {
            mListAdapter.onStop();
        }
        SubscriptionHelper.unsubscribe(mApiSubscription);
        SubscriptionHelper.unsubscribe(mDownloadSubscription);
        SubscriptionHelper.unsubscribe(mCacheSubscription);
        SubscriptionHelper.unsubscribe(mNextLessonSubscription);
        mApiSubscription = null;
        mDownloadSubscription = null;
        mCacheSubscription = null;
        mNextLessonSubscription = null;
        mMenuItemFileAction.setActionView(null);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            onPlaybackServiceUnbound();
        }
        super.onDestroy();
    }

    interface ArgKey {
        String NEXT_TOPIC = "NEXT_TOPIC";
        String MODE = "MODE";
    }
}