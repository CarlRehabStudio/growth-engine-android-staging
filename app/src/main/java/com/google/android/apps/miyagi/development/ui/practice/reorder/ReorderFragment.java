package com.google.android.apps.miyagi.development.ui.practice.reorder;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeDetails;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.CURRENT_STATE;
import static com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment.BundleKey.SUCCESS;
import static com.google.android.apps.miyagi.development.ui.practice.reorder.ReorderFragment.BundleKey.SUBMITTABLE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 15.11.2016.
 */

public class ReorderFragment extends AbstractPracticeFragment<List<ReorderPracticeAnswerOption>> implements OnStartDragListener {

    @BindView(R.id.reorder_header) TextView mInstructionHeader;
    @BindView(R.id.reorder_recycler) RecyclerView mReorderRecycler;
    @BindView(R.id.reorder_nested_scroll) NestedScrollView mNestedScrollView;

    private ItemTouchHelper mItemTouchHelper;
    private ReorderAdapter mAdapter;

    private Unbinder mUnbinder;
    private boolean mSubmittable;

    public ReorderFragment() {
    }

    /**
     * Creates new ReorderFragment instance with practice data.
     */
    public static ReorderFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(SUCCESS, isSuccessful);

        ReorderFragment fragment = new ReorderFragment();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSubmittable = savedInstanceState.getBoolean(SUBMITTABLE, false);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_reorder_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getReorderPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        Context context = getContext();
        Resources resources = context.getResources();

        final int labelTextSize = (int) resources.getDimension(R.dimen.practice_reorder_position_label_text_size);
        final int labelXPadding = resources.getDimensionPixelOffset(R.dimen.practice_reorder_position_label_offset);

        ReorderPracticeDetails practiceDetails = mPracticeData.getReorderPracticeDetails();
        ReorderPracticeColors colors = practiceDetails.getColors();

        initHeader(mInstructionHeader, practiceDetails.getQuestion());
        mAdapter = new ReorderAdapter(colors, this);
        mAdapter.setSubmittableCallback(this::allowSubmit);
        mReorderRecycler.setLayoutManager(new LinearLayoutManager(context));
        PositionItemDecorator positionItemDecorator = new PositionItemDecorator(
                labelTextSize,
                colors.getPositionNumberColor(),
                labelXPadding
        );
        mReorderRecycler.addItemDecoration(positionItemDecorator);
        mReorderRecycler.setNestedScrollingEnabled(false);
        mReorderRecycler.setAdapter(mAdapter);
        mAdapter.setData(practiceDetails.getPracticeAnswers());

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mAdapter);
        callback.setNestedScrollView(mNestedScrollView);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mReorderRecycler);
    }

    private void allowSubmit(boolean submittable) {
        mSubmittable = submittable;
        if (submittable) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    }

    @Override
    public void onNextClick() {
        verifyAnswers();
    }

    @Override
    public void onPrevClick() {
        mNavCallback.goBack();
    }

    private void verifyAnswers() {
        ReorderPracticeDetails practiceDetails = mPracticeData.getReorderPracticeDetails();

        ReorderPracticeAnswerOption option;
        String correctAnswerId;
        List<String> correctAnswerIds = practiceDetails.getCorrectAnswers();
        int len = Math.min(correctAnswerIds.size(), mAdapter.getItemCount());
        int errorsNum = 0;
        for (int i = 0; i < len; ++i) {
            correctAnswerId = correctAnswerIds.get(i);
            option = mAdapter.getItemData(i);
            if (!correctAnswerId.contains(option.getId())) {
                ++errorsNum;
            }
        }

        if (errorsNum == len) {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
        } else if (errorsNum == 0) {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
        } else {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_ALMOST);
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
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mAdapter.getItems()));
        outState.putBoolean(SUBMITTABLE, mSubmittable);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        mAdapter.setData(getCorrectOptions());
        allowSubmit(true);
    }

    @Override
    protected void recreateAnswers(List<ReorderPracticeAnswerOption> currentStates) {
        mAdapter.setData(currentStates);
        allowSubmit(mSubmittable);
    }

    private List<ReorderPracticeAnswerOption> getCorrectOptions() {
        List<ReorderPracticeAnswerOption> correctOptions = new ArrayList<>();
        for (String option : mPracticeData.getReorderPracticeDetails().getCorrectAnswers()) {
            for (ReorderPracticeAnswerOption answetOption : mPracticeData.getReorderPracticeDetails().getPracticeAnswers()) {
                if (answetOption.getId().equals(option)) {
                    correctOptions.add(answetOption);
                    break;
                }
            }
        }
        return correctOptions;
    }

    public interface BundleKey {
        String SUBMITTABLE = "SUBMITTABLE";
    }

    @Override
    public void onStartDrag(ReorderPracticeViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
