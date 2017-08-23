package com.google.android.apps.miyagi.development.ui.practice.rightwrong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.SelectRightView;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.ui.practice.rightwrong.items.SelectRightAdapter;
import com.google.android.apps.miyagi.development.ui.practice.rightwrong.items.SelectRightItem;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.SpaceItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 13.12.2016.
 */
public class SelectRightFragment extends AbstractPracticeFragment<List<SelectRightView.State>> {

    @BindView(R.id.select_right_header) TextView mInstructionHeader;
    @BindView(R.id.select_right_recycler) RecyclerView mRecyclerView;

    private SelectRightAdapter mSelectRightAdapter;
    private List<Integer> mCorrectOptionIds;

    private Unbinder mUnbinder;

    /**
     * Creates new SelectRightFragment instance with practice data.
     */
    public static SelectRightFragment newInstance(Practice practiceData, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practiceData));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        SelectRightFragment fragment = new SelectRightFragment();
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
        return R.layout.practice_select_right_fragment;
    }

    private void initUi() {
        SelectRightPracticeDetails details = mPracticeData.getSelectRightPracticeDetails();
        initHeader(mInstructionHeader, details.getQuestion());

        List<SelectRightPracticeOption> options = details.getOptions();
        List<SelectRightItem> items = new ArrayList<>();

        for (SelectRightPracticeOption option : options) {
            items.add(new SelectRightItem(option, mPracticeData.getLessonBackgroundColor()));
        }
        mSelectRightAdapter = new SelectRightAdapter(items);
        mSelectRightAdapter.setBtnSubmitUpdateListener(on -> {
            if (on) {
                enableNavBtnSubmit();
            } else {
                disableNavBtnSubmit();
            }
        });

        mCorrectOptionIds = details.getCorrectOptions();
        mSelectRightAdapter.setCorrectOptions(mCorrectOptionIds);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(
                new SpaceItemDecoration((int) getResources().getDimension(R.dimen.medium_margin)));
        mRecyclerView.setAdapter(mSelectRightAdapter);
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getSelectRightPracticeDetails().getColors().getLessonBackgroundColor();
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
        PracticeResult result = mSelectRightAdapter.verifyAnswers();
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
        List<SelectRightView.State> currentStates = new ArrayList<>(mSelectRightAdapter.getItemCount());
        for (int i = 0; i < mSelectRightAdapter.getItemCount(); i++) {
            currentStates.add(mSelectRightAdapter.getItem(i).getState());
        }
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(currentStates));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mSelectRightAdapter.setCurrentOptions(mCorrectOptionIds);
        enableNavBtnSubmit();
    }

    @Override
    protected void recreateAnswers(List<SelectRightView.State> currentStates) {
        mSelectRightAdapter.setCurrentStates(currentStates);
        if (!currentStates.contains(SelectRightView.State.IDLE)) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    }
}
