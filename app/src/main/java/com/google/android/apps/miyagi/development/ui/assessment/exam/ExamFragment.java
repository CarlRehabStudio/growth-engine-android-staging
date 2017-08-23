package com.google.android.apps.miyagi.development.ui.assessment.exam;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.data.models.assessment.Question;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.assessment.common.AssessmentAbstractFragment;
import com.google.android.apps.miyagi.development.ui.assessment.common.IAssessmentBottomNav;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marcin on 16.12.2016.
 */

public class ExamFragment extends AssessmentAbstractFragment implements IAssessmentBottomNav.Callback {

    @BindView(R.id.label_question) TextView mLabelQuestion;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.exam_container) View mExamContainer;

    private int mCurrentQuestionIndex = 0;
    private ExamAdapter mExamAdapter;
    private CopyInstructions mCopyInstructions;
    private List<QuestionModel> mQuestionList;
    private boolean mResultMode;
    private Map<String, Integer> mUserAnswers;
    private List<Integer> mDisplayIndex;
    private int mTopicColor;
    private int mMainBackgroundColor;
    private MenuItem mMenuItem;

    private IAssessmentBottomNav mAssessmentBottomCallback = Navigation.EMPTY;
    private Unbinder mUnbinder;

    public ExamFragment() {
    }

    /**
     * Creates new Assessment Test fragment with questions data.
     */
    public static ExamFragment newInstance(CopyInstructions copy, int topicColor, List<Question> questions, boolean isResultMode, Map<String, Integer> answers, int mainBackgroundColor) {
        Bundle args = new Bundle();
        args.putParcelable(ArgsKey.COPY_INSTRUCTIONS, Parcels.wrap(copy));
        args.putParcelable(ArgsKey.QUESTION_LIST, Parcels.wrap(questions));
        args.putBoolean(ArgsKey.RESULT_MODE, isResultMode);
        args.putParcelable(ArgsKey.USER_ANSWERS, Parcels.wrap(answers));
        args.putInt(ArgsKey.TOPIC_COLOR, topicColor);
        args.putInt(ArgsKey.MAIN_BACKGROUND_COLOR, mainBackgroundColor);

        ExamFragment fragment = new ExamFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IAssessmentBottomNav) {
            mAssessmentBottomCallback = (IAssessmentBottomNav) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "Activity must implement mAssessmentBottomCallback.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ViewUtils.isTablet(getContext())) {
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assessment_test_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            mCurrentQuestionIndex = savedInstanceState.getInt(BundleKey.CURRENT_QUESTION_INDEX);
            mResultMode = savedInstanceState.getBoolean(BundleKey.RESULT_MODE);
            mQuestionList = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.QUESTION_LIST));
            mCopyInstructions = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.COPY));
            mDisplayIndex = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.DISPLAY_INDEX));
            mTopicColor = savedInstanceState.getInt(BundleKey.TOPIC_COLOR);
            mMainBackgroundColor = savedInstanceState.getInt(BundleKey.MAIN_BACKGROUND_COLOR);
        } else {
            Bundle args = getArguments();
            mCopyInstructions = Parcels.unwrap(args.getParcelable(ArgsKey.COPY_INSTRUCTIONS));
            mUserAnswers = Parcels.unwrap(args.getParcelable(ArgsKey.USER_ANSWERS));
            mResultMode = args.getBoolean(ArgsKey.RESULT_MODE);
            mTopicColor = args.getInt(ArgsKey.TOPIC_COLOR);
            mMainBackgroundColor = args.getInt(ArgsKey.MAIN_BACKGROUND_COLOR);
            bindData();
        }

        setupUi();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mAssessmentBottomCallback = Navigation.EMPTY;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assessment_test, menu);

        mMenuItem = menu.getItem(0);
        if (mCopyInstructions != null) {
            mMenuItem.setTitle(mCopyInstructions.getDialogTitle());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                showInstructionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUi() {
        if (ViewUtils.isTablet(getContext())) {
            mAssessmentBottomCallback.setPrevEnable(false);
        } else {
            mAssessmentBottomCallback.setPrevEnable(true);
        }
        mAssessmentBottomCallback.setNextEnable(true);
        mAssessmentBottomCallback.setPrevButtonVisibility(View.VISIBLE);
        mAssessmentBottomCallback.setLabelVisibility(View.VISIBLE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setNestedScrollingEnabled(false);
        mExamContainer.setBackgroundColor(mCopyInstructions.getImageBackgroundColor());
        mAssessmentBottomCallback.setBackgroundColor(mMainBackgroundColor);

        bindCurrentQuestion();
    }

    private void bindData() {
        Bundle args = getArguments();
        List<Question> questions = Parcels.unwrap(args.getParcelable(ArgsKey.QUESTION_LIST));

        mQuestionList = new ArrayList<>();
        mDisplayIndex = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuestionModel model = new QuestionModel(questions.get(i), mResultMode);
            if (mResultMode == true) {
                model.setUserChoiceIndex(mUserAnswers.get(model.getInstanceId()));
            }
            mQuestionList.add(model);
            if (!model.isCorrect()) {
                mDisplayIndex.add(i);
            }
        }

        if (mDisplayIndex.size() == 0) {
            // TODO: invalid input data
        }

        if (mMenuItem != null) {
            mMenuItem.setTitle(mCopyInstructions.getDialogTitle());
        }

        bindCurrentQuestion();
    }

    private void bindCurrentQuestion() {
        if (mCurrentQuestionIndex >= 0 && mCurrentQuestionIndex < mDisplayIndex.size()) {
            QuestionModel currentQuestion = mQuestionList.get(mDisplayIndex.get(mCurrentQuestionIndex));
            mLabelQuestion.setText(currentQuestion.getQuestion());
            mAssessmentBottomCallback.setLabelText(currentQuestion.getIndexLabel());
            if (mCurrentQuestionIndex == mDisplayIndex.size() - 1) {
                mAssessmentBottomCallback.setNextButtonText(mCopyInstructions.getSubmitCta());
            } else {
                mAssessmentBottomCallback.setNextButtonText(mCopyInstructions.getNextQuestionCta());
            }

            mAssessmentBottomCallback.setNextEnable(currentQuestion.isUserAnswered());

            mLabelQuestion.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right));

            setAdapter();
        }

        if (!ViewUtils.isTablet(getContext()) || mCurrentQuestionIndex > 0) {
            mAssessmentBottomCallback.setPrevEnable(true);
        } else {
            mAssessmentBottomCallback.setPrevEnable(false);
        }
    }

    private void setAdapter() {
        QuestionModel currentQuestion = mQuestionList.get(mDisplayIndex.get(mCurrentQuestionIndex));
        List<ExamItem> items = new ArrayList<>();
        for (int i = 0; i < currentQuestion.getChoices().size(); i++) {
            items.add(new ExamItem.Builder()
                    .withTestTitle(currentQuestion.getChoices().get(i))
                    .withTestChar(getCharForNumber(i + 1))
                    .withIsSelected(i == currentQuestion.getUserChoiceIndex())
                    .withIsAnswered(currentQuestion.isResultMode())
                    .withIsAnsweredCorrectly(currentQuestion.isCorrect())
                    .build());
        }

        mExamAdapter = new ExamAdapter(getContext(), items, this::onItemSelected);
        mRecyclerView.setAdapter(mExamAdapter);
    }

    private String getCharForNumber(int index) {
        return index > 0 && index < 27 ? String.valueOf((char) (index + 64)) : "";
    }

    private void submitAnswers() {
        Map<String, Integer> answers = new HashMap<>();
        for (QuestionModel question : mQuestionList) {
            String instanceId = question.getInstanceId();
            Integer choiceIndex = question.getUserChoiceIndex();
            answers.put(instanceId, choiceIndex);
        }
        mNavigation.submitAnswers(answers);
    }

    @Override
    public void onNextClick() {
        if (mCurrentQuestionIndex == mDisplayIndex.size() - 1) {
            submitAnswers();
        } else {
            ++mCurrentQuestionIndex;
            bindCurrentQuestion();
        }
    }

    @Override
    public void onPrevClick() {
        if (mCurrentQuestionIndex == 0) {
            mNavigation.goBack();
        } else {
            --mCurrentQuestionIndex;
            bindCurrentQuestion();
        }
    }

    protected void showInstructionDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setTitle(mCopyInstructions.getDialogTitle())
                .setMessage(mCopyInstructions.getInstructionsText())
                .setCancelable(true)
                .setPositiveButton(mCopyInstructions.getDialogOk(), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onItemSelected(ExamItem item) {
        QuestionModel currentQuestion = mQuestionList.get(mDisplayIndex.get(mCurrentQuestionIndex));
        for (int i = 0; i < mExamAdapter.getItems().size(); i++) {
            ExamItem exam = mExamAdapter.getItem(i);
            if (exam.equals(item)) {
                exam.setSelected(true);
                currentQuestion.setUserChoiceIndex(i);
                mExamAdapter.notifyItemChanged(i);
            } else {
                if (exam.isSelected()) {
                    exam.setSelected(false);
                    mExamAdapter.notifyItemChanged(i);
                }
            }
            exam.setAnswered(false);
            exam.setAnsweredCorrectly(false);
        }
        currentQuestion.setUserAnswered(true);
        mAssessmentBottomCallback.setNextEnable(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BundleKey.CURRENT_QUESTION_INDEX, mCurrentQuestionIndex);
        outState.putBoolean(BundleKey.RESULT_MODE, mResultMode);
        outState.putParcelable(BundleKey.QUESTION_LIST, Parcels.wrap(mQuestionList));
        outState.putParcelable(BundleKey.COPY, Parcels.wrap(mCopyInstructions));
        outState.putParcelable(BundleKey.DISPLAY_INDEX, Parcels.wrap(mDisplayIndex));
        outState.putInt(BundleKey.TOPIC_COLOR, mTopicColor);
        outState.putInt(BundleKey.MAIN_BACKGROUND_COLOR, mMainBackgroundColor);
    }

    interface ArgsKey {
        String COPY_INSTRUCTIONS = CopyInstructions.class.getCanonicalName();
        String QUESTION_LIST = Question.class.getCanonicalName();
        String RESULT_MODE = "EXAM_FRAGMENT_RESULT_MODE";
        String USER_ANSWERS = "USER_ANSWERS";
        String TOPIC_COLOR = "TOPIC_COLOR";
        String MAIN_BACKGROUND_COLOR = "MAIN_BACKGROUND_COLOR";
    }

    interface BundleKey {
        String CURRENT_QUESTION_INDEX = "CURRENT_QUESTION_INDEX";
        String QUESTION_LIST = "QUESTION_LIST";
        String RESULT_MODE = "RESULT_MODE";
        String COPY = "COPY";
        String DISPLAY_INDEX = "DISPLAY_INDEX";
        String TOPIC_COLOR = "TOPIC_COLOR";
        String MAIN_BACKGROUND_COLOR = "MAIN_BACKGROUND_COLOR";
    }
}
