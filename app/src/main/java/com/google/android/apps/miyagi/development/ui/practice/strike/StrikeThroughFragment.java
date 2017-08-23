package com.google.android.apps.miyagi.development.ui.practice.strike;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.strikethrough.StrikeThroughPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.strikethrough.StrikeThroughPracticeOption;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 09.11.2016.
 */

public class StrikeThroughFragment extends AbstractPracticeFragment<List<String>> {

    @BindView(R.id.strike_selector_header) TextView mInstructionHeader;
    @BindView(R.id.strike_through_recycler) RecyclerView mRecyclerView;

    private List<String> mCorrectOptionIds;
    private StrikeThroughAdapter mStrikeThroughAdapter;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener = on -> {
        if (on) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    };

    private Unbinder mUnbinder;

    /**
     * Constructs new instance of StrikeThroughFragment with practice data.
     */
    public static StrikeThroughFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        StrikeThroughFragment fragment = new StrikeThroughFragment();
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

        initUi();
        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_strike_through_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getStrikeThroughPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        StrikeThroughPracticeDetails details = mPracticeData.getStrikeThroughPracticeDetails();
        initHeader(mInstructionHeader, details.getQuestion());

        mCorrectOptionIds = details.getCorrectOptions();

        List<StrikeThroughPracticeOption> options = details.getOptions();
        List<StrikeThroughItem> items = new ArrayList<>();

        for (StrikeThroughPracticeOption option : options) {
            items.add(new StrikeThroughItem(option));
        }

        mStrikeThroughAdapter = new StrikeThroughAdapter(items, true);
        mStrikeThroughAdapter.setMode(FlexibleAdapter.Mode.MULTI);
        mStrikeThroughAdapter.setBtnSubmitUpdateListener(mBtnSubmitUpdateListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        FlexibleItemDecoration divider = new FlexibleItemDecoration(getContext());
        divider.withDrawOver(true);
        mRecyclerView.addItemDecoration(divider);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.setAdapter(mStrikeThroughAdapter);
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
        PracticeResult result = verifyAnswers();
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

    private PracticeResult verifyAnswers() {
        List<String> userAnswerIds = mStrikeThroughAdapter.getSelectedIds();
        int correctCount = 0;
        for (String answerId : userAnswerIds) {
            if (mCorrectOptionIds.contains(answerId)) {
                correctCount++;
            }
        }
        if (userAnswerIds.size() == correctCount && mCorrectOptionIds.size() == correctCount) {
            return PracticeResult.SUCCESSFUL;
        } else {
            if (correctCount > 0) {
                return PracticeResult.ALMOST;
            } else {
                return PracticeResult.FAIL;
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
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mStrikeThroughAdapter.getSelectedIds()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mStrikeThroughAdapter.setCurrentOptions(mCorrectOptionIds);
    }

    @Override
    protected void recreateAnswers(List<String> currentStates) {
        mStrikeThroughAdapter.setCurrentOptions(currentStates);
    }
}

