package com.google.android.apps.miyagi.development.ui.practice.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud.TagCloudPracticeColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud.TagCloudPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud.TagCloudPracticeOption;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.ui.practice.tag.items.TagAdapter;
import com.google.android.apps.miyagi.development.ui.practice.tag.items.TagItem;
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
 * Created by lukaszweglinski on 14.11.2016.
 */

public class TagCloudFragment extends AbstractPracticeFragment<List<Integer>> implements FlexibleAdapter.OnItemClickListener {

    @BindView(R.id.tag_cloud_header) TextView mInstructionHeader;
    @BindView(R.id.tag_cloud_container) LinearLayout mBgLayout;
    @BindView(R.id.tag_cloud_recycler_view) RecyclerView mRecyclerView;

    private TagAdapter mTagAdapter;
    private List<String> mCorrectOptionIds;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener = on -> {
        if (on) {
            enableNavBtnSubmit();
        } else {
            disableNavBtnSubmit();
        }
    };

    private Unbinder mUnbinder;

    public TagCloudFragment() {
    }

    /**
     * Creates new TagCloudFragment instance with practice data.
     */
    public static TagCloudFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        TagCloudFragment fragment = new TagCloudFragment();
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
        return R.layout.practice_tag_cloud_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getTagCloudPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        TagCloudPracticeDetails details = mPracticeData.getTagCloudPracticeDetails();
        initHeader(mInstructionHeader, details.getQuestion());

        TagCloudPracticeColors colors = details.getColors();
        mBgLayout.setBackgroundColor(colors.getLessonBackgroundColor());

        int cardActiveColor = colors.getCardActiveColor();
        int cardInactiveColor = colors.getCardInactiveColor();
        int textActiveColor = colors.getTextActiveColor();
        int textInactiveColor = colors.getTextInactiveColor();

        mCorrectOptionIds = details.getCorrectOptions();

        List<TagCloudPracticeOption> options = details.getOptions();
        List<TagItem> items = new ArrayList<>();

        for (TagCloudPracticeOption option : options) {
            items.add(
                    new TagItem(
                            option,
                            cardActiveColor,
                            cardInactiveColor,
                            textActiveColor,
                            textInactiveColor));
        }

        mTagAdapter = new TagAdapter(items, this, true);
        mTagAdapter.setMode(FlexibleAdapter.Mode.MULTI);
        mTagAdapter.setBtnSubmitUpdateListener(mBtnSubmitUpdateListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.setAdapter(mTagAdapter);
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
        List<String> userAnswerIds = mTagAdapter.getSelectedIds();
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
    public boolean onItemClick(int position) {
        selectItem(position);
        return true;
    }

    private void selectItem(int position) {
        mTagAdapter.toggleSelection(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CURRENT_STATE, Parcels.wrap(mTagAdapter.getSelectedPositions()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void recreateAnswers() {
        for (Integer position : getCorrectOptionsPositions()) {
            selectItem(position);
        }
    }

    @Override
    protected void recreateAnswers(List<Integer> currentStates) {
        for (Integer position : currentStates) {
            selectItem(position);
        }
    }

    private List<Integer> getCorrectOptionsPositions() {
        List<Integer> correctOptionsPositions = new ArrayList<>();
        for (String option : mPracticeData.getTagCloudPracticeDetails().getCorrectOptions()) {
            for (int i = 0; i < mPracticeData.getTagCloudPracticeDetails().getOptions().size(); i++) {
                if (mPracticeData.getTagCloudPracticeDetails().getOptions().get(i).getId().equals(option)) {
                    correctOptionsPositions.add(i);
                    break;
                }
            }
        }
        return correctOptionsPositions;
    }
}
