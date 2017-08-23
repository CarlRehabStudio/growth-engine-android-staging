package com.google.android.apps.miyagi.development.ui.lesson;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.apps.miyagi.development.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import butterknife.BindView;

public class LessonTabletActivity extends LessonBaseActivity {

    @BindView(R.id.lesson_list_container) LinearLayout mLessonsListContainer;
    @BindView(R.id.lesson_list_container_horizontal) FrameLayout mLessonListContainerHorizontal;
    @BindView(R.id.lesson_list_container_vertical) FrameLayout mLessonListContainerVertical;
    @BindView(R.id.horizontal_list_divider) View mHorizontalListDivider;

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
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
                    errorReason.getErrorDialog(LessonTabletActivity.this, RECOVERY_DIALOG_REQUEST).show();
                } else {
                    //show error message
                    String errorMessage = String.format(getString(R.string.error_youtube_player), errorReason.toString());
                    Toast.makeText(LessonTabletActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onInitializationSuccessful(boolean wasRestored) {
            }

            @Override
            public void onDisplayModeChange(boolean fullScreen) {
                doLayout(fullScreen);
                toggleHideyBar(fullScreen);
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

    private void doLayout(boolean fullScreen) {
        if (fullScreen) {
            ((ViewGroup) mLessonsListContainer.getParent()).setVisibility(View.GONE);
            mHorizontalListDivider.setVisibility(View.GONE);
            mBottomNav.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.setLayoutParams(layoutParams);
        } else {
            ((ViewGroup) mLessonsListContainer.getParent()).setVisibility(View.VISIBLE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mHorizontalListDivider.setVisibility(View.VISIBLE);
            } else {
                mHorizontalListDivider.setVisibility(View.GONE);
            }
            mBottomNav.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private void toggleHideyBar(boolean fullScreen) {
        if (fullScreen) {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateView(newConfig.orientation);
    }

    private void updateView(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLessonListContainerVertical.removeAllViews();
            mLessonListContainerHorizontal.addView(mLessonsListContainer);
            if (mPlayer != null && mPlayer.isInFullscreenMode()) {
                mLessonListContainerHorizontal.setVisibility(View.GONE);
                mHorizontalListDivider.setVisibility(View.GONE);
            } else {
                mLessonListContainerHorizontal.setVisibility(View.VISIBLE);
                mHorizontalListDivider.setVisibility(View.VISIBLE);
            }
        } else {
            mLessonListContainerHorizontal.removeAllViews();
            mLessonListContainerVertical.addView(mLessonsListContainer);
            mLessonListContainerHorizontal.setVisibility(View.GONE);
            mHorizontalListDivider.setVisibility(View.GONE);
            mLessonListContainerVertical.setVisibility(View.VISIBLE);
        }
    }
}
