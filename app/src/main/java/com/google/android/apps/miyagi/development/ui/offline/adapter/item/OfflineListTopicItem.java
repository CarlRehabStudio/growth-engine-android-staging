package com.google.android.apps.miyagi.development.ui.offline.adapter.item;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.helpers.DrawableHelper;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.FileActionCallback;
import com.google.android.apps.miyagi.development.ui.offline.OfflineAdapterAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Offline List Topic item class
 */

public class OfflineListTopicItem extends AbstractFlexibleItem<OfflineListTopicItem.ViewHolder>
        implements ISectionable<OfflineListTopicItem.ViewHolder, IHeader> {

    private final AudioResponseData mAudioResponseData;
    private final int mMainCtaColor;
    private IHeader mHeader;

    public OfflineListTopicItem(AudioResponseData audioMetaData, int mainCtaColor) {
        mAudioResponseData = audioMetaData;
        mMainCtaColor = mainCtaColor;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent, View view) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(mAudioResponseData, mMainCtaColor);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.offline_list_topic_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public IHeader getHeader() {
        return mHeader;
    }

    @Override
    public void setHeader(IHeader header) {
        mHeader = header;
    }

    static class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.label_title) TextView mLabelTitle;
        @BindView(R.id.label_description) TextView mLabelDescription;
        @BindView(R.id.label_caption) TextView mLabelCaption;
        @BindView(R.id.button_download_eject_file) ImageView mFileActionButton;
        @BindView(R.id.button_play_pause) ImageView mButtonPlayPause;
        @BindView(R.id.image_more) ImageView mImageMore;

        private AudioResponseData mAudioResponseData;
        private OfflineAdapterAction.Callback mOfflineAdapterCallback = OfflineAdapterAction.Callback.EMPTY;
        private FileActionCallback mAudioDownloadCallback = FileActionCallback.EMPTY;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            mOfflineAdapterCallback = (OfflineAdapterAction.Callback) adapter;
            mAudioDownloadCallback = (FileActionCallback) adapter;

            view.setOnClickListener(v -> mOfflineAdapterCallback.onTopicClick(mAudioResponseData.getTopicId()));
            mImageMore.setOnClickListener(v -> mOfflineAdapterCallback.onMenuClick(v, mAudioResponseData.getTopicId(), getAdapterPosition()));
            mFileActionButton.setOnClickListener(v -> mAudioDownloadCallback.onFileActionClick(getAdapterPosition(), mAudioResponseData.getTopicId()));
        }

        public void bind(AudioResponseData audioResponseData, int mainCtaColor) {
            mAudioResponseData = audioResponseData;
            mLabelTitle.setText(audioResponseData.getTitle());
            mLabelDescription.setText(audioResponseData.getDescription());
            mLabelCaption.setText(audioResponseData.getLessonCount());
            mFileActionButton.setImageResource(R.drawable.ic_eject);
            mButtonPlayPause.setImageDrawable(
                    DrawableHelper.setTint(ContextCompat.getDrawable(mButtonPlayPause.getContext(), R.drawable.ic_play), mainCtaColor));
        }
    }
}
