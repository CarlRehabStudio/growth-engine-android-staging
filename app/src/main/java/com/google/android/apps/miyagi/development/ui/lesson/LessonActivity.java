package com.google.android.apps.miyagi.development.ui.lesson;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class LessonActivity extends LessonBaseActivity {

    private OrientationEventListener mOrientationEventListener;
    private ContentObserver mAccelerometerSettingObserver;
    private boolean mInLandscapeOrientation;
    private int mOrientation;
    private boolean mLockOrientation;
    private boolean mIsUserRotationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initAccelerometerRotation();
        doLayout();
    }

    private void initAccelerometerRotation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }

        mIsUserRotationEnabled = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

        mAccelerometerSettingObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mIsUserRotationEnabled = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
                if (mIsUserRotationEnabled) {
                    if (mOrientationEventListener.canDetectOrientation()) {
                        mOrientationEventListener.enable();
                    }
                } else {
                    mOrientationEventListener.disable();
                }
            }

            @Override
            public boolean deliverSelfNotifications() {
                return true;
            }
        };

        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            static final int THRESHOLD = 30;

            @Override
            public void onOrientationChanged(int orientation) {
                if (mIsUserRotationEnabled && !(orientation == OrientationEventListener.ORIENTATION_UNKNOWN)) {
                    if (mLockOrientation) {
                        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && isPortrait(orientation)) {
                            mLockOrientation = false;
                        } else if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE && isLandscape(orientation)) {
                            mLockOrientation = false;
                        }
                    }

                    if (mPlayer != null) {
                        if (isPortrait(orientation) && mOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE && !mLockOrientation) {
                            mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                            setRequestedOrientation(mOrientation);
                        } else if (isLandscape(orientation) && mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !mLockOrientation) {
                            mOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                            setRequestedOrientation(mOrientation);
                        }
                    }
                }
            }

            private boolean isLandscape(int orientation) {
                return orientation >= (90 - THRESHOLD) && orientation <= (270 + THRESHOLD);
            }

            private boolean isPortrait(int orientation) {
                return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
            }
        };
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri accelerometerSetting = Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION);
        getContentResolver().registerContentObserver(accelerometerSetting, false, mAccelerometerSettingObserver);

        if (mOrientationEventListener.canDetectOrientation() && mIsUserRotationEnabled) {
            mOrientationEventListener.enable();
        } else {
            mOrientationEventListener.disable();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mPlayer == null) {
            mInLandscapeOrientation = false;
        } else {
            mInLandscapeOrientation = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        }
        try {
            if (mPlayer != null) {
                if (!mIsError) {
                    mPlayer.setFullscreenMode(mInLandscapeOrientation);
                } else {
                    doLayout();
                }
            }
        } catch (IllegalStateException ex) {
            recreate();
        }
    }

    @Override
    protected void initPlayer() {
        mYoutubeDeveloperKey = getString(R.string.youtube_developer_key);
        mPlayerProvider = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.lesson_player_fragment);

        mPlayer = new PlayerWrapper();
        mPlayer.setPlayerStateListener(new PlayerWrapper.PlayerStateListener() {

            @Override
            public void onInitializationFailure(YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    // dialog
                    errorReason.getErrorDialog(LessonActivity.this, RECOVERY_DIALOG_REQUEST).show();
                } else {
                    //show error message
                    String errorMessage = String.format(getString(R.string.error_youtube_player), errorReason.toString());
                    Toast.makeText(LessonActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onInitializationSuccessful(boolean wasRestored) {
                if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    if (!mIsError) {
                        mPlayer.setFullscreenMode(mInLandscapeOrientation);
                    }
                }
            }

            @Override
            public void onDisplayModeChange(boolean fullScreen) {
                if (fullScreen) {
                    mOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                    setRequestedOrientation(mOrientation);
                } else {
                    //from full screen we always return to portrait
                    mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    setRequestedOrientation(mOrientation);
                }
                mLockOrientation = true;
                doLayout();
                toggleHideyBar();
            }

            @Override
            public void onVideoEnded() {
                mPlayerImage.setVisibility(View.VISIBLE);
                String videoId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getYoutubeUrl();
                int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lessonId), getString(R.string.event_category_video), getString(R.string.event_action_end), videoId);
            }

            @Override
            public void onPlaying() {
                int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
                String videoId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getYoutubeUrl();
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lessonId), getString(R.string.event_category_video), getString(R.string.event_action_play), videoId);
            }

            @Override
            public void onPaused() {
                if (mCurrentSessionCache.getLessonResponse() != null) {
                    int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
                    String videoId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getYoutubeUrl();
                    mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson), lessonId), getString(R.string.event_category_video), getString(R.string.event_action_pause), videoId);
                }
            }
        });
        mPlayer.initialize(mPlayerProvider, mYoutubeDeveloperKey);
    }

    @Override
    protected void bindPlayer(final Lesson lesson) {
        super.bindPlayer(lesson);
    }

    private void doLayout() {
        if (mPlayer != null && mOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private void toggleHideyBar() {
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            mToolbar.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mToolbar.setVisibility(View.VISIBLE);
        }

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions
                ^ View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                ^ View.SYSTEM_UI_FLAG_FULLSCREEN
                ^ View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    @Override
    public void onBackPressed() {
        if (mPlayer != null && mPlayer.isInFullscreenMode() && !mIsError) {
            mPlayer.setFullscreenMode(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOrientationEventListener.disable();
        getContentResolver().unregisterContentObserver(mAccelerometerSettingObserver);
    }
}
