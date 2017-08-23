package com.google.android.apps.miyagi.development.ui.audio.transcript.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;

import java.util.List;

/**
 * Audio Transcript List class
 */

public class AudioTranscriptListItem
        extends AbstractAudioTranscriptItem<AudioTranscriptListItem.ViewHolder>
        implements ISectionable<AudioTranscriptListItem.ViewHolder, IHeader> {

    IHeader mHeader;
    private AudioLesson mAudioLesson;

    /**
     * Creates new audio transcript list item.
     */
    public AudioTranscriptListItem(AudioLesson audioLesson) {
        mAudioLesson = audioLesson;
        setDraggable(false);
    }

    @Override
    public IHeader getHeader() {
        return mHeader;
    }

    @Override
    public void setHeader(IHeader header) {
        mHeader = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.audio_transcript_list_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(mAudioLesson);
    }

    static final class ViewHolder extends ExpandableViewHolder {

        @BindView(R.id.label_key_learnings) TextView mLabelKeyLearnings;
        @BindView(R.id.web_view_lesson_description) WebView mWebViewLessonDescription;
        @BindView(R.id.label_transcript_title) TextView mLabelTranscriptTitle;
        @BindView(R.id.web_view_transcript_text) WebView mWebViewTranscriptText;

        /**
         * Constructs view holder for audio transcript list item.
         */
        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            ButterKnife.bind(this, view);
        }

        public void bind(AudioLesson item) {
            mLabelKeyLearnings.setText(item.getKeyLearnings());
            HtmlHelper.bindWebviewWithText(mWebViewLessonDescription, item.getDescription());
            mLabelTranscriptTitle.setText(item.getTranscriptTitle());
            HtmlHelper.bindWebviewWithTextAndBrTags(mWebViewTranscriptText, item.getTranscriptText());
        }

    }
}
