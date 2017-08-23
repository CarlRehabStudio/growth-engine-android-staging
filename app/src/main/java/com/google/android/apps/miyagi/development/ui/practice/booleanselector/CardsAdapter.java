package com.google.android.apps.miyagi.development.ui.practice.booleanselector;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.ColorizeAnimator;
import com.google.android.apps.miyagi.development.ui.components.widget.StrokeButton;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.Date;
import java.util.List;

/**
 * Created by lukaszweglinski on 30.11.2016.
 */

class CardsAdapter extends ArrayAdapter<Card> {

    public static final long MIN_SELECT_DELAY = 220;

    private final LayoutInflater mLayoutInflater;
    private final BooleanSelectorPracticeDetails mBooleanDetails;
    private long mLastSelectTime;

    @Nullable
    private OnItemSelectedListener<Card> mOnItemSelectedExternalListener;
    private final OnCardSelectedListener mOnItemSelectedInternalListener = new OnCardSelectedListener() {

        @Override
        public void onCardSelected(Card item) {
            if (mOnItemSelectedExternalListener != null) {
                mOnItemSelectedExternalListener.onItemSelected(item);
            }
            mLastSelectTime = new Date().getTime();
        }

        @Override
        public boolean canSelectCard() {
            long currentTime = new Date().getTime();
            if (currentTime - mLastSelectTime > MIN_SELECT_DELAY) {
                return true;
            }
            return false;
        }
    };

    CardsAdapter(Context context, List<Card> cards, BooleanSelectorPracticeDetails details) {
        super(context, -1, cards);
        mLayoutInflater = LayoutInflater.from(context);
        mBooleanDetails = details;
    }

    void setOnItemSelectedListener(OnItemSelectedListener<Card> onItemSelectedListener) {
        mOnItemSelectedExternalListener = onItemSelectedListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CardViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.practice_boolean_selector_item, parent, false);

            viewHolder = new CardViewHolder(convertView, mBooleanDetails, getCount());
            viewHolder.setAnswerListener(mOnItemSelectedInternalListener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder) convertView.getTag();
        }

        Card item = getItem(position);
        if (item != null) {
            viewHolder.populateData(item, position);
        }

        return convertView;
    }

    boolean userAnsweredAllQuestions() {
        int answeredCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getCardAnswer() != Card.CardAnswer.NONE) {
                ++answeredCount;
            }
        }
        return answeredCount == getCount();
    }

    public PracticeResult verifyAnswers() {
        int correctCount = 0;

        for (int i = 0; i < getCount(); ++i) {
            int userAnswerOptionIndex = getItem(i).getCardAnswer().value;
            BooleanSelectorPracticeOption option = mBooleanDetails.getOptions().get(i);
            BooleanSelectorAnswerOption userAnswer = option.getBooleanSelectorAnswerOption().get(userAnswerOptionIndex);

            if (userAnswer.getId().equals(option.getCorrectOption())) {
                ++correctCount;
            }
        }

        if (correctCount == getCount()) {
            return PracticeResult.SUCCESSFUL;
        } else if (correctCount > 0) {
            return PracticeResult.ALMOST;
        }
        return PracticeResult.FAIL;
    }

    private static class CardViewHolder {

        private final int mCount;
        private TextView mQuestionText;
        private TextView mItemIndicator;
        private CardView mCardView;
        private StrokeButton mPositiveButton;
        private StrokeButton mNegativeButton;
        private Card mCardData;
        private BooleanSelectorPracticeDetails mBooleanSelectorPracticeDetails;
        private OnCardSelectedListener mOnItemSelectedListener;

        CardViewHolder(View itemView, BooleanSelectorPracticeDetails booleanSelectorPracticeDetails, int count) {
            mQuestionText = (TextView) itemView.findViewById(R.id.boolean_selector_item_text);
            mItemIndicator = (TextView) itemView.findViewById(R.id.boolean_selector_item_indicator);
            mCardView = (CardView) itemView.findViewById(R.id.boolean_selector_item_card);
            mPositiveButton = (StrokeButton) itemView.findViewById(R.id.boolean_selector_item_positive);
            mNegativeButton = (StrokeButton) itemView.findViewById(R.id.boolean_selector_item_negative);

            mBooleanSelectorPracticeDetails = booleanSelectorPracticeDetails;
            mCount = count;
        }

        void setAnswerListener(OnCardSelectedListener onItemSelectedListener) {
            mOnItemSelectedListener = onItemSelectedListener;

            mPositiveButton.setOnClickListener(v -> {
                if (mOnItemSelectedListener.canSelectCard()) {
                    if (mCardData.getCardAnswer() == Card.CardAnswer.NONE) {
                        ColorizeAnimator.animateBetweenColors(mCardView, mBooleanSelectorPracticeDetails.getColors().getCardActiveColor());
                    }
                    mCardData.setCardAnswer(Card.CardAnswer.YES);
                    refreshColors();
                    mOnItemSelectedListener.onCardSelected(mCardData);
                }
            });

            mNegativeButton.setOnClickListener(v -> {
                if (mOnItemSelectedListener.canSelectCard()) {
                    if (mCardData.getCardAnswer() == Card.CardAnswer.NONE) {
                        ColorizeAnimator.animateBetweenColors(mCardView, mBooleanSelectorPracticeDetails.getColors().getCardActiveColor());
                    }
                    mCardData.setCardAnswer(Card.CardAnswer.NO);
                    refreshColors();
                    mOnItemSelectedListener.onCardSelected(mCardData);
                }
            });
        }

        void populateData(Card card, int position) {
            mCardData = card;
            mItemIndicator.setText((position + 1) + "/" + (mCount));

            BooleanSelectorPracticeOption options = mBooleanSelectorPracticeDetails.getOptions().get(position);
            mQuestionText.setText(options.getText());
            List<BooleanSelectorAnswerOption> answerOption = options.getBooleanSelectorAnswerOption();

            mPositiveButton.setText(answerOption.get(Card.CardAnswer.YES.value).getText());
            mNegativeButton.setText(answerOption.get(Card.CardAnswer.NO.value).getText());

            refreshColors();
        }

        void refreshColors() {
            BooleanSelectorColors colors = mBooleanSelectorPracticeDetails.getColors();
            if (mCardData.getCardAnswer() == Card.CardAnswer.NONE) {
                mItemIndicator.setTextColor(colors.getTextInactiveColor());
                mQuestionText.setTextColor(colors.getTextInactiveColor());

                mPositiveButton.setTextColor(colors.getCardActiveColor());
                mPositiveButton.setStrokeColor(colors.getCardActiveColor());
                mPositiveButton.setFillColor(colors.getCardInactiveColor());

                mNegativeButton.setTextColor(colors.getCardActiveColor());
                mNegativeButton.setStrokeColor(colors.getCardActiveColor());
                mNegativeButton.setFillColor(colors.getCardInactiveColor());
            } else {
                mCardView.setCardBackgroundColor(mBooleanSelectorPracticeDetails.getColors().getCardActiveColor());
                mItemIndicator.setTextColor(colors.getTextActiveColor());
                mQuestionText.setTextColor(colors.getTextActiveColor());

                mPositiveButton.setTextColor(colors.getCardInactiveColor());
                mPositiveButton.setStrokeColor(colors.getCardInactiveColor());
                mPositiveButton.setFillColor(Color.TRANSPARENT);

                mNegativeButton.setTextColor(colors.getCardInactiveColor());
                mNegativeButton.setStrokeColor(colors.getCardInactiveColor());
                mNegativeButton.setFillColor(Color.TRANSPARENT);

                if (mCardData.getCardAnswer() == Card.CardAnswer.YES) {
                    mPositiveButton.setFillColor(colors.getCardInactiveColor());
                    mPositiveButton.setTextColor(colors.getCardActiveColor());
                } else {
                    mNegativeButton.setFillColor(colors.getCardInactiveColor());
                    mNegativeButton.setTextColor(colors.getCardActiveColor());
                }
            }
        }
    }

    private interface OnCardSelectedListener {

        boolean canSelectCard();

        void onCardSelected(Card item);

    }
}