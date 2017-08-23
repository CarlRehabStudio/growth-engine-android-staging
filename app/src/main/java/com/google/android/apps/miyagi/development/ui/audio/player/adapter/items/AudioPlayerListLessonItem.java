package com.google.android.apps.miyagi.development.ui.audio.player.adapter.items;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.helpers.DrawableHelper;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.AudioPlayerListAdapter;
import com.google.android.apps.miyagi.development.ui.audio.player.adapter.AudioPlayerListAdapterCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;

import java.util.List;

/**
 * Audio Player lesson List
 */

public class AudioPlayerListLessonItem
        extends AbstractAudioPlayerItem<AudioPlayerListLessonItem.ViewHolder>
        implements ISectionable<AudioPlayerListLessonItem.ViewHolder, IHeader> {

    private final int mMainCtaColor;
    private final AudioLesson mAudioLesson;
    IHeader mHeader;

    /**
     * Creates new audio player list item.
     */
    public AudioPlayerListLessonItem(AudioLesson audioLesson, int mainCtaColor) {
        mAudioLesson = audioLesson;
        mMainCtaColor = mainCtaColor;
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
        return R.layout.audio_player_list_lesson_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter, mMainCtaColor);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        boolean selected = adapter.isSelected(position);
        boolean isPlaying = ((AudioPlayerListAdapter) adapter).isPlaying();
        holder.bind(mAudioLesson, selected, isPlaying);
    }

    static final class ViewHolder extends ExpandableViewHolder {

        @BindView(R.id.root_container) LinearLayout mRootLayout;
        @BindView(R.id.label_header) TextView mLabelHeader;
        @BindView(R.id.label_title) TextView mLabelTitle;
        @BindView(R.id.icon_play_pause) ImageView mIconPlayPause;

        private final int mSelectedItemBackgroundColor;
        private final int mItemBackgroundColor;
        private final int mMainCtaColor;

        private AudioLesson mAudioLesson;
        private AudioPlayerListAdapterCallback mAdapterCallback;

        /**
         * Constructs view holder for lesson item in audio player list.
         */
        ViewHolder(View view, FlexibleAdapter adapter, int mainCtaColor) {
            super(view, adapter, true);
            ButterKnife.bind(this, view);

            mMainCtaColor = mainCtaColor;
            mAdapterCallback = (AudioPlayerListAdapterCallback) adapter;
            view.setOnClickListener(v -> mAdapterCallback.onLessonItemClick(mAudioLesson));

            Context context = view.getContext();
            mSelectedItemBackgroundColor = ContextCompat.getColor(context, R.color.pale_grey);
            mItemBackgroundColor = ContextCompat.getColor(context, R.color.white);
        }

        public void bind(AudioLesson item, boolean selected, boolean isPlaying) {
            mAudioLesson = item;

            mLabelHeader.setText(item.getHeader());
            mLabelTitle.setText(item.getTitle());

            if (selected) {
                mRootLayout.setBackgroundColor(mSelectedItemBackgroundColor);
            } else {
                mRootLayout.setBackgroundColor(mItemBackgroundColor);
            }

            if (selected && isPlaying) {
                mIconPlayPause.setImageDrawable(DrawableHelper.setTint(ContextCompat.getDrawable(mIconPlayPause.getContext(), R.drawable.ic_pause), mMainCtaColor));
            } else {
                mIconPlayPause.setImageDrawable(DrawableHelper.setTint(ContextCompat.getDrawable(mIconPlayPause.getContext(), R.drawable.ic_play), mMainCtaColor));
            }
        }
    }
}
