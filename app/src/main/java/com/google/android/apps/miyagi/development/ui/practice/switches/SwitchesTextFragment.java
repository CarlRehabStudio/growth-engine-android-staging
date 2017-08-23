package com.google.android.apps.miyagi.development.ui.practice.switches;

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
import com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext.SwitchesTextPracticeColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext.SwitchesTextPracticeDetails;
import com.google.android.apps.miyagi.development.ui.components.widget.SlideView;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.utils.Lh;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 22.11.2016.
 */
public class SwitchesTextFragment extends AbstractPracticeFragment<List<SlideView.State>> {

    @BindView(R.id.switches_selector_header) TextView mInstructionHeader;
    @BindView(R.id.switches_recycler_view) RecyclerView mRecyclerView;

    private SwitchesAdapter mSwitchesAdapter;
    private Unbinder mUnbinder;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener = on -> {
        if (on) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    };

    /**
     * Creates new SwitchesTextFragment instance with practice data.
     */
    public static SwitchesTextFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        SwitchesTextFragment fragment = new SwitchesTextFragment();
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
        return R.layout.practice_switches_text_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getSwitchesTextPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        SwitchesTextPracticeDetails details = mPracticeData.getSwitchesTextPracticeDetails();

        initHeader(mInstructionHeader, details.getQuestion());

        SwitchesTextPracticeColors colors = details.getColors();

        mSwitchesAdapter = SwitchesAdapter.create(
                details.getOptions(),
                details.getCorrectOptions(),
                colors.getLessonBackgroundColor());
        mSwitchesAdapter.setBtnSubmitUpdateListener(mBtnSubmitUpdateListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        FlexibleItemDecoration divider = new FlexibleItemDecoration(getContext());
        divider.withDrawOver(true);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mSwitchesAdapter);
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
        PracticeResult result = mSwitchesAdapter.verifyAnswers();
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
        List<SlideView.State> currentStates = new ArrayList<>(mSwitchesAdapter.getItemCount());
        for (int i = 0; i < mSwitchesAdapter.getItemCount(); i++) {
            currentStates.add(mSwitchesAdapter.getItem(i).getState());
        }
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(currentStates));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mSwitchesAdapter.setCurrentOptions(mPracticeData.getSwitchesTextPracticeDetails().getCorrectOptions());
        enableNavBtnSubmit();
    }

    @Override
    protected void recreateAnswers(List<SlideView.State> currentStates) {
        mSwitchesAdapter.setCurrentStates(currentStates);
        if (!currentStates.contains(SlideView.State.IDLE)) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    }
}
