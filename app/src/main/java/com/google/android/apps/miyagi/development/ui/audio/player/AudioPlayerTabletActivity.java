package com.google.android.apps.miyagi.development.ui.audio.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.ui.components.widget.ExpandableLinearLayout;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import butterknife.BindView;
import org.parceler.Parcels;

import java.util.List;

/**
 * Created by lukaszweglinski on 24.01.2017.
 */
public class AudioPlayerTabletActivity extends AudioPlayerBaseActivity {

    @BindView(R.id.audio_label_key_learning) TextView mAudioLabelKeyLearning;
    @BindView(R.id.audio_web_view_description) WebView mAudioDescWebView;
    @BindView(R.id.lesson_label_transcript_title) TextView mAudioTranscriptTitle;
    @BindView(R.id.audio_transcript_text) TextView mAudioTranscriptText;
    @BindView(R.id.audio_view_transcript) ExpandableLinearLayout mViewTranscript;
    @BindView(R.id.lesson_image_transcript_arrow) ImageView mImageTranscriptArrow;
    @BindView(R.id.lesson_list_container_horizontal) FrameLayout mLessonListContainerHorizontal;
    @BindView(R.id.lesson_list_container_vertical) FrameLayout mLessonListContainerVertical;
    @BindView(R.id.horizontal_list_divider) View mHorizontalListDivider;

    /**
     * Creates calling to AudioPlayerTabletActivity.
     *
     * @param context - the context.
     * @param mode    -  the internet mode.
     * @return the AudioPlayerTabletActivity calling intent.
     */
    public static Intent getCallingIntent(Context context, Mode mode) {
        Intent intent = new Intent(context, AudioPlayerTabletActivity.class);
        intent.putExtra(ArgKey.MODE, Parcels.wrap(mode));
        return intent;
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateView(newConfig.orientation);
    }

    private void updateView(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLessonListContainerVertical.removeAllViews();
            mLessonListContainerHorizontal.addView(mRecyclerView);
            mLessonListContainerHorizontal.setVisibility(View.VISIBLE);
            mHorizontalListDivider.setVisibility(View.VISIBLE);
        } else {
            mLessonListContainerHorizontal.removeAllViews();
            mLessonListContainerVertical.addView(mRecyclerView);
            mLessonListContainerHorizontal.setVisibility(View.GONE);
            mHorizontalListDivider.setVisibility(View.GONE);
            mLessonListContainerVertical.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onInitUi() {
        mAudioTranscriptTitle.setOnClickListener(v -> mViewTranscript.toggle());

        mViewTranscript.setOnStateChangeListener(() -> {
            if (mViewTranscript.isCollapsed()) {
                mImageTranscriptArrow.setImageResource(R.drawable.ic_arrow_down);
            } else {
                mImageTranscriptArrow.setImageResource(R.drawable.ic_arrow_up);
            }
        });
        mViewTranscript.collapse(0);
    }

    @Override
    protected void onBindData(List<AudioLesson> audioLessons) {
        super.onBindData(audioLessons);
        updateTranscript(audioLessons.get(mCurrentLessonIndex));
    }

    protected void updateTranscript(AudioLesson currentAudioLesson) {
        mAudioLabelKeyLearning.setText(currentAudioLesson.getKeyLearnings());
        HtmlHelper.bindWebviewWithText(mAudioDescWebView, currentAudioLesson.getDescription());
        mAudioTranscriptTitle.setText(currentAudioLesson.getTranscriptTitle());
        mAudioTranscriptText.setText(HtmlHelper.fromHtml(currentAudioLesson.getTranscriptText()));
    }

    @Override
    protected void onLessonChange(AudioLesson audioLesson) {
        super.onLessonChange(audioLesson);
        updateTranscript(audioLesson);
    }

    @Override
    public void onLessonItemClick(AudioLesson audioLesson) {
        super.onLessonItemClick(audioLesson);
        mViewTranscript.collapse();
        updateTranscript(audioLesson);
    }
}