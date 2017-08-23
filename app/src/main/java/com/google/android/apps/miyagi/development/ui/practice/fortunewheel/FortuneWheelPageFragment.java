package com.google.android.apps.miyagi.development.ui.practice.fortunewheel;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelPracticeColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelQuestionPage;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.practice.fortunewheel.widget.FortuneWheel;
import com.google.android.apps.miyagi.development.ui.practice.fortunewheel.widget.OnFortuneWheelSelected;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

/**
 * Created by jerzyw on 23.11.2016.
 */

public class FortuneWheelPageFragment extends Fragment {

    @BindView(R.id.root_container) LinearLayout mRootLayout;
    @BindView(R.id.practice_fortune_wheel_page_main_widget) FortuneWheel mFortuneWheelWidget;
    @BindView(R.id.practice_fortune_wheel_page_question) TextView mQuestionTv;
    @BindView(R.id.practice_fortune_wheel_page_text) TextView mPageTextTv;
    @BindView(R.id.practice_fortune_wheel_page_widget_instruction_section) View mInstructionSection;
    @BindView(R.id.practice_fortune_wheel_page_widget_instruction_text) TextView mInstructionTextTv;
    @BindView(R.id.practice_fortune_wheel_page_selected_option_info_section) View mSelectedOptionSection;
    @BindView(R.id.practice_fortune_wheel_page_selected_option_label) TextView mSelectedOptionLabelTv;
    @BindView(R.id.practice_fortune_wheel_page_selected_option_text) TextView mSelectedOptionTextTv;
    private int mPageIndex;
    private Unbinder mUnbinder;
    private State mCurrentState;
    private FortuneWheelAnswerOption mCurrentItem;
    private OnFortuneWheelSelected mExternalSelectionListener;

    /**
     * @param pageData             - fortune wheel question page data.
     * @param colorSet             - colors for single page.
     * @param questionText         - practice question text.
     * @param wheelInstructionText - fortune wheel instruction for user.
     *
     * @return new instance of FortuneWheelPageFragment with passed parameters.
     */
    public static FortuneWheelPageFragment newInstance(int pageIndex,
                                                       FortuneWheelQuestionPage pageData,
                                                       FortuneWheelPracticeColors colorSet,
                                                       String questionText,
                                                       String wheelInstructionText,
                                                       boolean isSuccessfull) {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgsKey.PAGE_INDEX, pageIndex);
        bundle.putParcelable(ArgsKey.PAGE_DATA, Parcels.wrap(pageData));
        bundle.putParcelable(ArgsKey.COLOR_SET, Parcels.wrap(colorSet));
        bundle.putString(ArgsKey.PRACTICE_QUESTION, questionText);
        bundle.putString(ArgsKey.WHEEL_INSTRUCTION, wheelInstructionText);
        bundle.putString(ArgsKey.WHEEL_INSTRUCTION, wheelInstructionText);
        bundle.putBoolean(ArgsKey.IS_SUCCESSFUL, isSuccessfull);

        FortuneWheelPageFragment fragment = new FortuneWheelPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.practice_fragment_fortune_wheel_question_page, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mFortuneWheelWidget.setOnSelectionChangeListener(this::onWheelItemSelected);
        //read arguments
        Bundle arguments = getArguments();
        mPageIndex = arguments.getInt(ArgsKey.PAGE_INDEX);
        FortuneWheelQuestionPage pageData = Parcels.unwrap(arguments.getParcelable(ArgsKey.PAGE_DATA));
        FortuneWheelPracticeColors colorSet = Parcels.unwrap(arguments.getParcelable(ArgsKey.COLOR_SET));
        String questionText = arguments.getString(ArgsKey.PRACTICE_QUESTION);
        String wheelWidgetInstruction = arguments.getString(ArgsKey.WHEEL_INSTRUCTION);
        boolean isSuccessful = arguments.getBoolean(ArgsKey.IS_SUCCESSFUL);
        //pass data to view
        setupUi(pageData, colorSet, questionText, wheelWidgetInstruction);

        setWidgetInstructionState();

        if (savedInstanceState != null) {
            mCurrentItem = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.CURRENT_ITEM));
            mPageIndex = savedInstanceState.getInt(BundleKey.PAGE_INDEX);
            mFortuneWheelWidget.setSelection(mCurrentItem);
        } else if (isSuccessful) {
            mCurrentItem = getCorrectItem(pageData);
            mFortuneWheelWidget.setSelection(mCurrentItem);
        }

        return view;
    }

    private void setupUi(FortuneWheelQuestionPage pageData,
                         FortuneWheelPracticeColors colorSet,
                         String questionText,
                         String wheelWidgetInstruction) {

        if (ViewUtils.isTablet(getContext())) {
            mQuestionTv.setVisibility(View.GONE);
        }

        mPageTextTv.setText(pageData.getText());
        mQuestionTv.setText(HtmlHelper.fromHtml(questionText));
        mInstructionTextTv.setText(wheelWidgetInstruction);

        // setup colors
        mRootLayout.setBackgroundColor(colorSet.getLessonBackgroundColor());
        mFortuneWheelWidget.setMarkerTintColor(colorSet.getMarkerColor());
        mFortuneWheelWidget.setSectors(pageData.getOptions());
    }

    public void setOnSelectionChangeListener(OnFortuneWheelSelected listener) {
        mExternalSelectionListener = listener;
    }

    /**
     * On answer selected callback.
     *
     * @param item selected item.
     */
    private void onWheelItemSelected(FortuneWheelAnswerOption item) {
        setShowSelectedOptionState();
        mSelectedOptionLabelTv.setText(item.getLabel());
        mSelectedOptionTextTv.setText(item.getAnswer());

        GradientDrawable oval = (GradientDrawable) mSelectedOptionLabelTv.getBackground();
        oval.setColor(item.getOptionColor());

        if (mExternalSelectionListener != null) {
            mExternalSelectionListener.onItemSelected(item, mPageIndex);
        }

        mCurrentItem = item;
    }

    private void setWidgetInstructionState() {
        if (mCurrentState != State.INSTRUCTION) {
            mCurrentState = State.INSTRUCTION;
            mInstructionSection.setVisibility(View.VISIBLE);
            mSelectedOptionSection.setVisibility(View.GONE);
        }
    }

    private void setShowSelectedOptionState() {
        if (mCurrentState != State.SELECTED_OPTION) {
            mCurrentState = State.SELECTED_OPTION;
            mInstructionSection.setVisibility(View.GONE);
            mSelectedOptionSection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mCurrentItem != null) {
            onWheelItemSelected(mCurrentItem);
        }
    }

    private FortuneWheelAnswerOption getCorrectItem(FortuneWheelQuestionPage pageData) {
        String correctOption = pageData.getCorrectOption();
        for (FortuneWheelAnswerOption option : pageData.getOptions()) {
            if (option.getId().equals(correctOption)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BundleKey.PAGE_INDEX, mPageIndex);
        outState.putParcelable(BundleKey.CURRENT_ITEM, Parcels.wrap(mCurrentItem));
    }

    private enum State {
        INSTRUCTION, SELECTED_OPTION
    }

    interface ArgsKey {
        String PAGE_INDEX = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.PAGE_INDEX";
        String PAGE_DATA = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.PAGE_DATA";
        String PRACTICE_QUESTION = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.PRACTICE_QUESTION";
        String WHEEL_INSTRUCTION = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.WHEEL_INSTRUCTION";
        String COLOR_SET = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.COLOR_SET";
        String IS_SUCCESSFUL = "com.google.android.apps.miyagi.development.ui.practice.fortunewheel.IS_SUCCESSFUL";
    }

    interface BundleKey {
        String CURRENT_ITEM = "CURRENT_ITEM";
        String PAGE_INDEX = "PAGE_INDEX";
    }
}
