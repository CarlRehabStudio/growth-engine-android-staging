package com.google.android.apps.miyagi.development.ui.lesson;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.TopicLesson;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.BaseAdapter;
import com.google.android.apps.miyagi.development.ui.BaseViewHolder;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TopicLessonAdapter extends BaseAdapter<TopicLesson> {

    private final RequestManager mRequestManager;
    private LayoutInflater mInflater;
    private int mCurLessonId;

    public TopicLessonAdapter(Context context, List<TopicLesson> items, int curLessonId, OnItemSelectedListener<TopicLesson> listener) {
        super(context, items, listener);
        mCurLessonId = curLessonId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRequestManager = Glide.with(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.lesson_item_topic_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position));
    }

    public class ViewHolder extends BaseViewHolder<TopicLesson> {

        @BindView(R.id.header) TextView mHeaderTv;
        @BindView(R.id.title) TextView mTitleTv;
        @BindView(R.id.apla) ImageView mAplaIv;
        @BindView(R.id.bg) View mBackgroundIv;
        @BindView(R.id.thumbnail) ImageView mThumbnailIv;
        @BindView(R.id.thumbnail_state) ImageView mThumbnailStateIv;

        private TopicLesson mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                if (mItem.getLessonId() != mCurLessonId) {
                    mOnItemClickListener.onItemSelected(mItem);
                }
            });
        }

        @Override
        protected BaseAdapter<TopicLesson> getAdapter() {
            return TopicLessonAdapter.this;
        }

        @Override
        protected void bind(TopicLesson item) {
            super.bind(item);
            this.mItem = item;

            resetState();
            mHeaderTv.setText(item.getLessonHeader());
            mTitleTv.setText(item.getLessonTitle());
            mRequestManager
                    .load(ImageUrlHelper.getUrlFor(GoogleApplication.getInstance().getBaseContext(), item.getLessonImageUrl()))
                    .into(mThumbnailIv);
            if (item.getLessonState() == LessonState.WATCHED.getValue()) {
                setWatched();
            } else if (item.getLessonState() == LessonState.COMPLETED.getValue()) {
                setCompleted();
            }
            if (item.getLessonId() == mCurLessonId) {
                setSelected();
            }

        }

        private void resetState() {
            mAplaIv.setVisibility(View.INVISIBLE);
            mThumbnailStateIv.setVisibility(View.INVISIBLE);
            mBackgroundIv.setVisibility(View.INVISIBLE);
        }

        private void setCompleted() {
            mAplaIv.setVisibility(View.VISIBLE);
            mThumbnailStateIv.setVisibility(View.VISIBLE);
            mThumbnailStateIv.setImageResource(R.drawable.ic_lesson_check);
        }

        private void setWatched() {
            mAplaIv.setVisibility(View.VISIBLE);
            mThumbnailStateIv.setVisibility(View.VISIBLE);
            mThumbnailStateIv.setImageResource(R.drawable.ic_watched);
        }

        private void setSelected() {
            mBackgroundIv.setVisibility(View.VISIBLE);
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
        }
    }
}