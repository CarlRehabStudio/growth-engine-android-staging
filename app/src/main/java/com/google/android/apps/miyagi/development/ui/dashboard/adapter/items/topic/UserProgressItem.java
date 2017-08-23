package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.AbstractDashboardItem;
import com.google.android.apps.miyagi.development.ui.dashboard.view.TopicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Show user progress class
 */

public class UserProgressItem extends AbstractDashboardItem<UserProgressItem.ViewHolder>
        implements ISectionable<UserProgressItem.ViewHolder, IHeader> {

    IHeader mHeader;
    private int mProgress;
    private int mProgressColor;
    private String mDescription;
    private View.OnClickListener mOnClickListener = null;

    /**
     * Instantiates a new UserProgress item.
     *
     * @param progress      user progress.
     * @param description   description of progress.
     * @param progressColor progress color.
     */
    public UserProgressItem(int progress, String description, int progressColor) {
        mProgress = progress;
        mDescription = description;
        mProgressColor = progressColor;
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
        return R.layout.dashboard_item_user_progress;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(mProgress, mDescription, mProgressColor, mOnClickListener);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.label_progress) TextView mLabelProgress;
        @BindView(R.id.view_progress_bar) TopicProgressBar mViewProgressBar;
        @BindView(R.id.label_description) TextView mLabelDescription;

        View.OnClickListener mOnClickListener = null;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        public void bind(int progress, String description, int color, View.OnClickListener listener) {
            mLabelProgress.setText(String.format("%d%%", progress));
            mLabelDescription.setText(description);
            mViewProgressBar.setProgress(progress);
            mViewProgressBar.setMainColor(color);
            mOnClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(view);
            }
            super.onClick(view);
        }
    }
}
