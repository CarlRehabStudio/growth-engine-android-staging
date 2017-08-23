package com.google.android.apps.miyagi.development.ui.practice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.AdjustableImageView;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import org.parceler.Parcels;

public class InstructionFragment extends AbstractPracticeFragment {

    @Nullable
    @BindView(R.id.instruction_image_intro)
    AdjustableImageView mImageIntro;
    @BindView(R.id.instruction_label_header) TextView mLabelHeader;
    @BindView(R.id.instruction_label_description) TextView mLabelDescription;
    @BindView(R.id.instruction_label_question) TextView mLabelQuestion;

    private Practice mPracticeData;
    private Unbinder mUnbinder;

    public InstructionFragment() {
    }

    /**
     * Creates new Instruction fragment with practice data.
     */
    public static InstructionFragment newInstance(Practice practice) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));

        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPracticeData = Parcels.unwrap(arguments.getParcelable(Practice.ARG_DATA));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        initUi();

        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_instruction_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return ContextCompat.getColor(getContext(), R.color.white);
    }

    private void initUi() {
        bindLabels();
        bindImages();
        if (!ViewUtils.isTablet(getContext())) {
            mNavCallback.setViewPager(null);
        }
    }

    @Override
    protected void setNavigationCallback(PracticeNavCallback.Callback callback) {
        if (!ViewUtils.isTablet(getContext())) {
            super.setNavigationCallback(callback);
        }
    }

    @Override
    protected void initPractice() {
        mNavCallback.hideInfoButton();
        if (!ViewUtils.isTablet(getContext())) {
            mNavCallback.setNextButtonText(mPracticeData.getInstructionStartButtonText());
            mNavCallback.setNextButtonVisibility(View.VISIBLE);
            mNavCallback.setNextEnable(true);
            mNavCallback.setPrevButtonVisibility(View.INVISIBLE);
        }
    }

    private void bindImages() {
        if (mImageIntro != null) {
            mImageIntro.setBackgroundColor(mPracticeData.getLessonBackgroundColor());
            Glide.with(this)
                    .load(ImageUrlHelper.getUrlFor(getContext(), mPracticeData.getInstructionImageUrl()))
                    .into(mImageIntro);
        }
    }

    private void bindLabels() {
        mLabelHeader.setText(mPracticeData.getInstructionHeaderText());
        mLabelDescription.setText(HtmlHelper.fromHtmlWithoutBR(mPracticeData.getInstructionDescriptionText()));
        mLabelQuestion.setText(HtmlHelper.fromHtml(mPracticeData.getInstructionQuestionText()));
    }

    @Override
    protected void showInfoButton() {
        //do nothing for instruction
    }

    @Override
    public void onNextClick() {
        runPractice();
    }

    @Override
    public void onPrevClick() {
        mNavCallback.goBack();
    }

    private void runPractice() {
        mNavCallback.goToStepRqst(PracticeActivity.getPracticeStep(mPracticeData));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }
}
