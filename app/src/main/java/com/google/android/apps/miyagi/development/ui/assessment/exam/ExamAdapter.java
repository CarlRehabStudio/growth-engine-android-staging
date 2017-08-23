package com.google.android.apps.miyagi.development.ui.assessment.exam;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.BaseAdapter;
import com.google.android.apps.miyagi.development.ui.BaseViewHolder;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;

/**
 * Created by Paweł on 2017-03-06.
 */

class ExamAdapter extends BaseAdapter<ExamItem> {

    ExamAdapter(Context context, List<ExamItem> items, OnItemSelectedListener<ExamItem> listener) {
        super(context, items, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.assessment_test_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position));
    }

    public class ViewHolder extends BaseViewHolder<ExamItem> {

        private final int mSelectedAnswerColor;
        private final int mWrongAnswerColor;
        private final int mRightAnswerColor;
        private final int mDefaultAnswerColor;
        private final int mSelectedTextColor;
        private final int mDefaultTextColor;

        @BindView(R.id.label_letter) TextView mTestChar;
        @BindView(R.id.label_text) TextView mTestQuestion;
        @BindView(R.id.answer_background) FrameLayout mTestCharImage;

        private ExamItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Context context = itemView.getContext();
            mSelectedAnswerColor = ContextCompat.getColor(context, R.color.yellow);
            mWrongAnswerColor = ContextCompat.getColor(context, R.color.red);
            mRightAnswerColor = ContextCompat.getColor(context, R.color.green_right);
            mDefaultAnswerColor = ContextCompat.getColor(context, R.color.french_blue);
            mSelectedTextColor = ContextCompat.getColor(context, R.color.black_87);
            mDefaultTextColor = ContextCompat.getColor(context, R.color.black_54);

            itemView.setOnClickListener(view -> {
                if (areAllAnimated()) {
                    mOnItemClickListener.onItemSelected(mItem);
                }
            });
        }

        @Override
        protected BaseAdapter<ExamItem> getAdapter() {
            return ExamAdapter.this;
        }

        @Override
        protected void bind(ExamItem item) {
            super.bind(item);
            this.mItem = item;

            bindTexts(item);
            bindColors(item);
        }

        private void bindTexts(ExamItem item) {
            mTestChar.setText(item.getTestChar());
            mTestQuestion.setText(item.getTestTitle());
        }

        private void bindColors(ExamItem item) {
            if (item.isSelected()) {
                mTestQuestion.setTextColor(mSelectedTextColor);
                if (item.isAnswered()) {
                    if (item.isAnsweredCorrectly()) {
                        mTestChar.setTextColor(Color.WHITE);
                        mTestCharImage.setBackgroundColor(mRightAnswerColor);
                    } else {
                        mTestChar.setTextColor(Color.WHITE);
                        mTestCharImage.setBackgroundColor(mWrongAnswerColor);
                    }
                } else {
                    mTestChar.setTextColor(Color.BLACK);
                    mTestCharImage.setBackgroundColor(mSelectedAnswerColor);
                }
            } else {
                mTestChar.setTextColor(Color.WHITE);
                mTestQuestion.setTextColor(mDefaultTextColor);
                mTestCharImage.setBackgroundColor(mDefaultAnswerColor);
            }
        }

        @Override
        protected void onAnimated() {
            ViewCompat.setAlpha(itemView, 0.0f);
        }

        @Override
        protected void scrollAnimators(@NonNull List<Animator> animators) {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 0.0f, 1.0f);
            ObjectAnimator slideInFromBottomAnimator = ObjectAnimator.ofFloat(itemView, "translationY", 250f, 0.0f);

            int animationDuration = itemView.getContext().getResources().getInteger(R.integer.animation_duration);
            alphaAnimator.setDuration(animationDuration);
            slideInFromBottomAnimator.setDuration(animationDuration);

            alphaAnimator.setInterpolator(new AccelerateInterpolator(2f));
            slideInFromBottomAnimator.setInterpolator(new DecelerateInterpolator());

            animators.add(alphaAnimator);
            animators.add(slideInFromBottomAnimator);
        }
    }
}
