package com.google.android.apps.miyagi.development.ui.assessment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.data.models.assessment.ExamPassedAction;
import com.google.android.apps.miyagi.development.data.models.assessment.Question;
import com.google.android.apps.miyagi.development.data.models.commondata.errors.Errors;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponseData;
import com.google.android.apps.miyagi.development.data.net.services.AssessmentService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.NavigatorMobile;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.NavigatorTablet;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.dashboard.common.UpNextActionType;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity.ArgsKey.ACTION_TYPE;
import static com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity.ArgsKey.TOPIC_ID;
import static com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation.Step.TEST;
import org.parceler.Parcels;
import rx.Observable;
import rx.Subscription;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public class AssessmentActivity extends AssesmentBaseActivity implements Navigation {

    @Inject AssessmentService mAssessmentService;
    @Inject ConfigStorage mConfigStorage;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.button_next) NavigationButton mButtonNext;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;
    @BindView(R.id.label_position) TextView mBottomNavLabel;
    @BindView(R.id.assessment_bottom_content) View mAssesmentBottomNavContent;

    private Navigator mNavigator;
    private Subscription mApiSubscription;
    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;

    // assessment data
    private String mXsrfToken;
    private Copy mCopy;
    private List<Question> mQuestions;
    private boolean mResultMode;
    private Map<String, Integer> mUserAnswers;
    private int mActionType;
    private int mTopicId;
    private int mMainBackgroundColor;

    /**
     * Creates new instance of AssessmentActivity.
     */
    public static Intent createIntent(Context context, int topicId, int actionType) {
        Intent intent = new Intent(context, AssessmentActivity.class);
        intent.putExtra(TOPIC_ID, topicId);
        intent.putExtra(ACTION_TYPE, actionType);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ViewUtils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.assessment_activity);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Bundle args = getIntent().getExtras();
        mActionType = args.getInt(ACTION_TYPE);
        mTopicId = args.getInt(TOPIC_ID);

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.assessment_preloader));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.assessment_error));
        mErrorHelper.setOnActionClickListener(this::getAssessmentData);
        mErrorHelper.setOnNavigationClickListener(this::onBackPressed);

        mMainBackgroundColor = mConfigStorage.getCommonData().getColors().getMainBackgroundColor();

        if (ViewUtils.isTablet(this)) {
            mNavigator = new NavigatorTablet(getSupportFragmentManager(), this);
        } else {
            mNavigator = new NavigatorMobile(getSupportFragmentManager(), this);
        }

        mButtonNext.setOnClickListener(v -> mNavigator.onNextClick());
        mButtonPrev.setOnClickListener(v -> mNavigator.onPrevClick());

        if (findViewById(R.id.container) != null) {
            if (savedInstanceState == null) {
                getAssessmentData();
            } else {
                mXsrfToken = savedInstanceState.getString(ArgsKey.XSRF_TOKEN);
                if (mXsrfToken == null) {
                    getAssessmentData();
                } else {
                    mCopy = Parcels.unwrap(savedInstanceState.getParcelable(ArgsKey.COPY));
                    mQuestions = Parcels.unwrap(savedInstanceState.getParcelable(ArgsKey.QUESTIONS));
                    mResultMode = savedInstanceState.getBoolean(ArgsKey.RESULT_MODE);
                    mUserAnswers = Parcels.unwrap(savedInstanceState.getParcelable(ArgsKey.USER_ANSWERS));
                    mNavigator.setCurrentStep(Parcels.unwrap(savedInstanceState.getParcelable(ArgsKey.CURRENT_STEP)));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mNavigator != null) {
            setupUiByStep(mNavigator.getCurrentStep());
        }
    }

    public void setupUiByStep(Step step) {
        if (mCopy != null) {
            int toolbarColor = mCopy.getTopicColor();
            if (mActionType == UpNextActionType.CERTIFICATION_ASSESMENT) {
                toolbarColor = mMainBackgroundColor;
            }

            CopyInstructions copyInstructions = mCopy.getInstructions();
            switch (step) {
                case INSTRUCTION:
                    mAssesmentBottomNavContent.setBackgroundColor(mMainBackgroundColor);
                    bindActionBar(copyInstructions.getToolbarText(), toolbarColor, true);
                    if (!ViewUtils.isTablet(this)) {
                        setNextButtonText(copyInstructions.getStartCta());
                        setNextEnable(true);
                    }
                    break;
                case TEST:
                case RESULT:
                    bindActionBar(copyInstructions.getToolbarText(), toolbarColor, true);
                    break;
                case BADGE:
                    bindActionBar("", mMainBackgroundColor, true);
                    break;
                default:
                    Lh.e(this, "Unknown assessment step!");
            }
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void getAssessmentData() {
        mErrorHelper.setOnActionClickListener(this::getAssessmentData);
        Observable<AssessmentResponse> assessmentResponseObservable;

        if (mActionType == UpNextActionType.TOPIC_ASSESMENT) {
            mAnalyticsHelper.trackScreen(String.format(getString(R.string.screen_topic_assessment), mTopicId));
            assessmentResponseObservable = mAssessmentService.getTopicData(mTopicId);
        } else {
            mAnalyticsHelper.trackScreen(getString(R.string.screen_certification_assessment));
            assessmentResponseObservable = mAssessmentService.getCertificationData();
        }

        mCurrentSessionCache.setAssessmentResponse(null);
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = assessmentResponseObservable
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onDataAssessmentReceived, this::onDataError);
    }

    private void sendUserAnswers(int topicId, Map<String, Integer> userAnswers) {
        mErrorHelper.setOnActionClickListener(() -> sendUserAnswers(mTopicId, mUserAnswers));
        Observable<AssessmentResponse> assessmentResponseObservable;

        JsonObject jsonAnswers = new JsonObject();
        for (Map.Entry<String, Integer> entry : userAnswers.entrySet()) {
            jsonAnswers.addProperty(entry.getKey(), entry.getValue());
        }

        jsonAnswers.addProperty("xsrf_token", mXsrfToken);

        if (mActionType == UpNextActionType.TOPIC_ASSESMENT) {
            assessmentResponseObservable = mAssessmentService.sendTopicAnswers(topicId, jsonAnswers);
        } else {
            assessmentResponseObservable = mAssessmentService.sendCertificationAnswers(jsonAnswers);
        }

        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = assessmentResponseObservable
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .doOnCompleted(() -> mConfigStorage.saveShouldUpdateDashboard(true))
                .subscribe(this::onDataAssessmentReceived, this::onDataError);
    }

    private void onDataAssessmentReceived(AssessmentResponse response) {
        if (mCurrentSessionCache.getAssessmentResponse() == null) {
            mCurrentSessionCache.setAssessmentResponse(response);
        }
        AssessmentResponseData responseData = response.getResponseData();
        int statusCode = response.getStatusCode();
        if (statusCode == 1) {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_certification_assessment), getString(R.string.event_category_assessment), getString(R.string.event_action_feedback), getString(R.string.event_label_true));
        } else {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_certification_assessment), getString(R.string.event_category_assessment), getString(R.string.event_action_feedback), getString(R.string.event_label_false));
        }

        ExamPassedAction passedAction = null;
        mCurrentSessionCache.setAssessmentPassedAction(null);
        if (responseData != null) {
            passedAction = responseData.getExamPassedAction();
        }
        if (statusCode == 1) {
            if (passedAction != null) {
                // send-assessment: assessment passed
                mCurrentSessionCache.setAssessmentPassedAction(passedAction);
                mAnalyticsHelper.trackScreen(String.format(getString(R.string.screen_topic_badge), mTopicId));
                mNavigator.launchBadge(mCopy, mMainBackgroundColor);
            } else if (responseData == null) {
                if (mActionType == UpNextActionType.CERTIFICATION_ASSESMENT) {
                    mNavigator.launchBadge(mCopy, mMainBackgroundColor);
                }
            } else if (responseData.getCopy() != null) {
                // get assessment
                mXsrfToken = responseData.getXsrfToken();
                mQuestions = responseData.getQuestions();
                mResultMode = false;
                mCopy = responseData.getCopy();
                goToFirstStep();
            }
        } else if (statusCode == 5) {
            // send-assessment: repeat current assessment
            if (mActionType == UpNextActionType.CERTIFICATION_ASSESMENT) {
                // no attempts left
                if (mCopy != null) {
                    mNavigator.launchResultFailed(mCopy, true, mMainBackgroundColor, mMainBackgroundColor);
                } else {
                    // user failed all attempts in web
                    finish();
                }
            } else {
                mNavigator.launchResultFailed(mCopy, false, mCopy.getTopicColor(), mMainBackgroundColor);
                mXsrfToken = responseData.getXsrfToken();
                mQuestions = responseData.getQuestions();
                mResultMode = true;
            }
        } else if (statusCode == 6) {
            // send certification assessment: repeat
            int attemptsLeft = Integer.MAX_VALUE;
            if (mActionType == UpNextActionType.CERTIFICATION_ASSESMENT) {
                // get attempts
                attemptsLeft = responseData.getAttemptsLeft();
            }

            mNavigator.launchResultFailed(mCopy, 0 == attemptsLeft, mMainBackgroundColor, mMainBackgroundColor);
            mXsrfToken = responseData.getXsrfToken();
            mQuestions = responseData.getQuestions();
            mResultMode = true;

        } else {
            mErrorHelper.show();
            mErrorHelper.setOnActionClickListener(this::onTryAgainButtonClick);
            if (mConfigStorage.getCommonData() != null && mConfigStorage.getCommonData().getErrors() != null) {
                Errors errors = mConfigStorage.getCommonData().getErrors();
                mErrorHelper.setMessage(errors.getInternalError().getText());
                mErrorHelper.setButton(errors.getInternalError().getCta());
            }
        }

        setupUiByStep(mNavigator.getCurrentStep());
    }

    private void onTryAgainButtonClick() {
        getAssessmentData();
    }

    private void onDataError(Throwable throwable) {
        mErrorHelper.setErrorForLoggedIn(throwable);
        mErrorHelper.show();
    }

    private void onTerminate() {
        mPreloaderHelper.hide();
    }

    private void onSubscribe() {
        mErrorHelper.hide();
        mPreloaderHelper.show();
    }

    private void goToFirstStep() {
        goTo(Step.INSTRUCTION);
    }

    @Override
    public void goTo(Step step) {
        int toolbarColor = mCopy.getTopicColor();

        CopyInstructions copyInstructions = mCopy.getInstructions();
        switch (step) {
            case INSTRUCTION:
                mNavigator.launchInstruction(copyInstructions, toolbarColor, mMainBackgroundColor);
                if (ViewUtils.isTablet(this)) {
                    goTo(TEST);
                }
                break;
            case TEST:
                mNavigator.launchTest(mCopy.getInstructions(), toolbarColor, mQuestions, mResultMode, mUserAnswers, mMainBackgroundColor);
                break;
            case BADGE:
                mNavigator.launchBadge(mCopy, mMainBackgroundColor);
                break;
            default:
                Lh.e(this, "Unknown assessment step!");
        }
        setupUiByStep(step);
    }

    @Override
    public void goBack() {
        mNavigator.navigateBack(this);
    }

    @Override
    public void submitAnswers(Map<String, Integer> answers) {
        mUserAnswers = answers;
        sendUserAnswers(mTopicId, mUserAnswers);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mXsrfToken != null) {
            outState.putString(ArgsKey.XSRF_TOKEN, mXsrfToken);
        }
        if (mCopy != null) {
            outState.putParcelable(ArgsKey.COPY, Parcels.wrap(mCopy));
        }
        if (mQuestions != null) {
            outState.putParcelable(ArgsKey.QUESTIONS, Parcels.wrap(mQuestions));
        }
        if (mUserAnswers != null) {
            outState.putParcelable(ArgsKey.USER_ANSWERS, Parcels.wrap(mUserAnswers));
        }
        outState.putBoolean(ArgsKey.RESULT_MODE, mResultMode);
        outState.putParcelable(ArgsKey.CURRENT_STEP, Parcels.wrap(mNavigator.getCurrentStep()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    @Override
    public void setNextButtonText(String text) {
        mButtonNext.setText(text);
    }

    @Override
    public void setPrevButtonText(String text) {
        mButtonPrev.setText(text);
    }

    @Override
    public void setBackgroundColor(int color) {
        mAssesmentBottomNavContent.setBackgroundColor(color);
    }

    @Override
    public void setNextEnable(boolean enable) {
        mButtonNext.setEnabled(enable);
    }

    @Override
    public void setPrevEnable(boolean enable) {
        mButtonPrev.setEnabled(enable);
    }

    @Override
    public void setLabelText(String text) {
        mBottomNavLabel.setText(text);
    }

    @Override
    public void setNextButtonVisibility(int visibility) {
        mButtonNext.setVisibility(visibility);
    }

    @Override
    public void setPrevButtonVisibility(int visibility) {
        mButtonPrev.setVisibility(visibility);
    }

    @Override
    public void setLabelVisibility(int visibility) {
        mBottomNavLabel.setVisibility(visibility);
    }

    public interface ArgsKey {
        String XSRF_TOKEN = "XSRF_TOKEN";
        String COPY = "COPY";
        String QUESTIONS = "QUESTIONS";
        String RESULT_MODE = "RESULT_MODE";
        String USER_ANSWERS = "USER_ANSWERS";

        String TOPIC_ID = "TOPIC_ID";
        String ACTION_TYPE = "ACTION_TYPE";
        String CURRENT_STEP = "CURRENT_STEP";

        String MAIN_BACKGROUND_COLOR = "MAIN_BACKGROUND_COLOR";
    }
}
