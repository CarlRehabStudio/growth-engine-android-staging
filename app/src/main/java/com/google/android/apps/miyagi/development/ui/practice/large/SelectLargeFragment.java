package com.google.android.apps.miyagi.development.ui.practice.large;

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
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeOption;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 23.11.2016.
 */

public class SelectLargeFragment extends AbstractPracticeFragment<List<Integer>> {

    @BindView(R.id.large_selector_header) TextView mInstructionHeader;
    @BindView(R.id.select_large_recycler) RecyclerView mRecyclerView;

    private SelectLargeAdapter mLargeAdapter;
    private Unbinder mUnbinder;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener = on -> {
        if (on) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    };

    public SelectLargeFragment() {
    }

    /**
     * Creates new SelectLargeFragment instance with practice data.
     */
    public static SelectLargeFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        SelectLargeFragment fragment = new SelectLargeFragment();
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
        return R.layout.practice_select_large_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getSelectLargePracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        SelectLargePracticeDetails details = mPracticeData.getSelectLargePracticeDetails();
        initHeader(mInstructionHeader, details.getQuestion());

        List<SelectLargePracticeOption> options = details.getOptions();
        List<SelectLargeItem> items = new ArrayList<>();
        for (SelectLargePracticeOption option : options) {
            items.add(new SelectLargeItem(option));
        }

        mLargeAdapter = new SelectLargeAdapter(items, details.getCorrectOptions());
        mLargeAdapter.setMode(FlexibleAdapter.Mode.MULTI);
        mLargeAdapter.setBtnSubmitUpdateListener(mBtnSubmitUpdateListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mLargeAdapter);
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
        PracticeResult result = mLargeAdapter.verifyAnswers();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mLargeAdapter.getSelectedPositions()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mLargeAdapter.selectItem(getCorrectPosition());
    }

    @Override
    protected void recreateAnswers(List<Integer> currentStates) {
        for (int i : currentStates) {
            mLargeAdapter.selectItem(i);
        }
    }

    private int getCorrectPosition() {
        for (int i = 0; i < mPracticeData.getSelectLargePracticeDetails().getOptions().size(); i++) {
            SelectLargePracticeOption option = mPracticeData.getSelectLargePracticeDetails().getOptions().get(i);
            if (option.getId().equals(mPracticeData.getSelectLargePracticeDetails().getCorrectOptions())) {
                return i;
            }
        }
        return 0;
    }
}
