package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.MyDashboardAdapter;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.AbstractDashboardItem;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.FileActionCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.DashboardAdapterCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.MenuClickCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.view.TopicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.AUDIO_STATUS_PAYLOAD;

import java.util.List;

class TopicItem extends AbstractDashboardItem<TopicItem.TopicViewHolder>
        implements ISectionable<TopicItem.TopicViewHolder, IHeader> {

    IHeader mHeader;
    private Topics mTopic;
    private boolean mIsLastItem;

    /**
     * Construcs new instance of item for topic in dashboard list.
     */
    TopicItem(Topics topic, boolean isLastItem) {
        mTopic = topic;
        mIsLastItem = isLastItem;
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
        return R.layout.dashboard_item_topic;
    }

    @Override
    public TopicViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    public TopicViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TopicViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, TopicViewHolder holder, int position, List payloads) {
        AudioDownloadStatus status = ((MyDashboardAdapter) adapter).getAudioStatusForId(mTopic.getId());
        if (payloads.size() == 0) {
            holder.bind(mTopic, status, mIsLastItem);
        } else if (payloads.contains(AUDIO_STATUS_PAYLOAD)) {
            holder.updateAudioFileStatus(status);
        }
    }

    static final class TopicViewHolder extends FlexibleViewHolder {

        @BindView(R.id.label_title) TextView mLabelTitle;
        @BindView(R.id.button_action_file) ImageView mButtonActionFile;
        @BindView(R.id.image_more) ImageView mImageMore;
        @BindView(R.id.label_description) TextView mLabelDescription;
        @BindView(R.id.view_progress_bar) TopicProgressBar mViewProgressBar;
        @BindView(R.id.progress_label) TextView mProgressLabel;
        @BindView(R.id.label_continue) TextView mLabelContinue;
        @BindView(R.id.audio_download_progress) ProgressBar mAudioProgress;
        @BindView(R.id.divider_bottom) View mDividerBottom;

        private Topics mTopic;
        private AudioDownloadStatus mAudioStatus;
        private boolean mIsLastItem;

        private DashboardAdapterCallback mDashboardAdapterCallback;
        private FileActionCallback mAudioDownloadCallback;
        private MenuClickCallback mMenuClickCallback;

        /**
         * Constructs view holder for topic item on dashboard list.
         */
        TopicViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);

            mDashboardAdapterCallback = (DashboardAdapterCallback) adapter;
            mAudioDownloadCallback = (FileActionCallback) adapter;
            mMenuClickCallback = (MenuClickCallback) adapter;
            view.setOnClickListener(v -> mDashboardAdapterCallback.onTopicClick(mTopic));
            mImageMore.setOnClickListener(v -> mMenuClickCallback.onMenuClick(v, mTopic, getAdapterPosition(), mAudioStatus));
            mButtonActionFile.setOnClickListener(v -> mAudioDownloadCallback.onFileActionClick(getAdapterPosition(), mTopic.getId()));
        }

        public void bind(Topics topic, AudioDownloadStatus status, boolean isLastItem) {
            mTopic = topic;
            mIsLastItem = isLastItem;

            mLabelTitle.setText(topic.getTitle());
            mLabelDescription.setText(topic.getDescription());
            mProgressLabel.setText(topic.getProgressLabel());

            mViewProgressBar.setProgress(topic.getPercentageCompleted());
            mViewProgressBar.setMainColor(topic.getColor());

            if (topic.isStarted()) {
                mLabelContinue.setVisibility(View.VISIBLE);
                mLabelContinue.setText(topic.getCta());
            } else {
                mLabelContinue.setVisibility(View.GONE);
                mLabelContinue.setText(null);
            }

            if (mIsLastItem) {
                mDividerBottom.setVisibility(View.GONE);
            } else {
                mDividerBottom.setVisibility(View.VISIBLE);
            }

            updateAudioFileStatus(status);
        }

        void updateAudioFileStatus(AudioDownloadStatus status) {
            mAudioStatus = status;
            if (status == AudioDownloadStatus.DOWNLOADING) {
                mAudioProgress.setVisibility(View.VISIBLE);
                mButtonActionFile.setVisibility(View.GONE);
            } else {
                if (status == AudioDownloadStatus.DOWNLOAD) {
                    mButtonActionFile.setImageResource(R.drawable.ic_file_download);
                } else if (status == AudioDownloadStatus.DELETE) {
                    mButtonActionFile.setImageResource(R.drawable.ic_eject);
                }
                mAudioProgress.setVisibility(View.GONE);
                mButtonActionFile.setVisibility(View.VISIBLE);
            }
        }
    }
}
