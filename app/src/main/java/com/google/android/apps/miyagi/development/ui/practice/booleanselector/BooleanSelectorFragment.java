package com.google.android.apps.miyagi.development.ui.practice.booleanselector;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector.BooleanSelectorPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.cards.CardsSelector;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 30.11.2016.
 */

public class BooleanSelectorFragment extends AbstractPracticeFragment<List<Card.CardAnswer>> {

    @BindView(R.id.card_stack_view) CardsSelector mSwipeCardView;
    @BindView(R.id.boolean_selector_header) TextView mInstructionHeader;

    private CardsAdapter mAdapter;

    private Unbinder mUnbinder;
    private List<Card.CardAnswer> mCurrentStates;
    private int mFirstCardIndex = -1;

    /**
     * New instance of BooleanSelector fragment.
     *
     * @param practiceData the practice data.
     *
     * @return BooleanSelectorFragment.
     */
    public static BooleanSelectorFragment newInstance(Practice practiceData, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practiceData));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        BooleanSelectorFragment fragment = new BooleanSelectorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPracticeData = Parcels.unwrap(arguments.getParcelable(Practice.ARG_DATA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_boolean_selector_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mFirstCardIndex = savedInstanceState.getInt(ArgKey.FIRST_CARD_INDEX, -1);
        }

        initUi();
    }

    private void initUi() {
        BooleanSelectorPracticeDetails details = mPracticeData.getBooleanPracticeDetails();
        initHeader(mInstructionHeader, details.getQuestion());

        List<Card> cards = new ArrayList<>();
        for (BooleanSelectorPracticeOption option : details.getOptions()) {
            cards.add(new Card(option));
        }
        if (mCurrentStates != null) {
            boolean allAnswered = true;
            for (int i = 0; i < cards.size(); ++i) {
                Card.CardAnswer cardAnswer = mCurrentStates.get(i);
                Card card = cards.get(i);
                card.setCardAnswer(cardAnswer);
                if (cardAnswer == Card.CardAnswer.NONE) {
                    allAnswered = false;
                }
            }
            if (allAnswered) {
                enableNavBtnSubmit();
            }
        } else if (mIsSuccessful) {
            for (int i = 0; i < cards.size(); ++i) {
                Card card = cards.get(i);
                BooleanSelectorPracticeOption practiceOption = details.getOptions().get(i);
                for (int j = 0; j < practiceOption.getBooleanSelectorAnswerOption().size(); j++) {
                    if (practiceOption.getCorrectOption().equals(practiceOption.getBooleanSelectorAnswerOption().get(j).getId())) {
                        card.setCardAnswer(Card.CardAnswer.fromValue(j));
                        break;
                    }
                }
            }
        }

        if (mFirstCardIndex > 0) {
            mSwipeCardView.setFirstIndex(mFirstCardIndex);
        }

        mAdapter = new CardsAdapter(getContext(), cards, details);
        mAdapter.setOnItemSelectedListener(item -> onItemSelected(item));
        mSwipeCardView.setAdapter(mAdapter);
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getBooleanPracticeDetails().getColors().getLessonBackgroundColor();
    }

    @Nullable
    @Override
    protected List<Card.CardAnswer> getStatesFromSavedInstance(@Nullable Bundle savedInstanceState) {
        mCurrentStates = super.getStatesFromSavedInstance(savedInstanceState);
        return mCurrentStates;
    }

    @Override
    public void onNextClick() {
        onButtonSubmitClick();
    }

    @Override
    public void onPrevClick() {
        mNavCallback.goBack();
    }

    private void onButtonSubmitClick() {
        goToResultScreen();
    }

    private void goToResultScreen() {
        PracticeResult result = mAdapter.verifyAnswers();
        switch (result) {
            case FAIL:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
                break;
            case SUCCESSFUL:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
                break;
            case ALMOST:
                mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_ALMOST);
                break;
            default:
                Lh.e(this, "Unknown PracticeResult: " + result);
        }
    }

    private void onItemSelected(Card item) {
        if (mAdapter.getItem(mSwipeCardView.getAdapterPositionOfItem(0)).equals(item)) {
            mSwipeCardView.sendFrontToBack();
            if (mAdapter.userAnsweredAllQuestions()) {
                enableNavBtnSubmit();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        List<Card.CardAnswer> currentStates = new ArrayList<>(mAdapter.getCount());
        for (int i = 0; i < mAdapter.getCount(); i++) {
            currentStates.add(mAdapter.getItem(i).getCardAnswer());
        }
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(currentStates));
        outState.putInt(ArgKey.FIRST_CARD_INDEX, mSwipeCardView.getAdapterIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        //handled in initUI
    }

    @Override
    protected void recreateAnswers(List<Card.CardAnswer> currentStates) {
        //handled in initUI
    }

    interface ArgKey {
        String FIRST_CARD_INDEX = "FIRST_CARD_INDEX";
    }
}
