package com.google.android.apps.miyagi.development.ui.practice.booleanselector;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeOption;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 30.11.2016.
 */

class Card extends BooleanSelectorPracticeOption {
    private CardAnswer mCardAnswer = CardAnswer.NONE;

    Card(BooleanSelectorPracticeOption booleanSelectorPracticeOption) {
        mId = booleanSelectorPracticeOption.getId();
        mText = booleanSelectorPracticeOption.getText();
    }

    CardAnswer getCardAnswer() {
        return mCardAnswer;
    }

    Card setCardAnswer(CardAnswer cardAnswer) {
        mCardAnswer = cardAnswer;
        return this;
    }

    @Parcel
    enum CardAnswer {
        YES(0), NO(1), NONE(-1);
        public int value;

        CardAnswer() {} //for Parcel

        CardAnswer(int i) {
            value = i;
        }

        public static CardAnswer fromValue(int value) {
            switch (value) {
                case 0:
                    return YES;
                case 1:
                    return NO;
                case -1:
                default:
                    return NONE;
            }
        }
    }
}