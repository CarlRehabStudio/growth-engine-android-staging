package com.google.android.apps.miyagi.development.ui.result;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponseData;
import com.google.android.apps.miyagi.development.data.net.services.LessonService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import dagger.internal.Preconditions;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

import java.lang.ref.WeakReference;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Presenter that controls communication between views and models of the result screen.
 */
@Singleton
public class ResultPresenter extends BasePresenterImpl<ResultContract.View> implements ResultContract.Presenter {

    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject LessonService mLessonService;
    @Inject ConfigStorage mConfigStorage;

    private Practice mPracticeData;
    private PracticeFeedback mPracticeFeedbackData;
    private int mTopicColor;

    private Subscription mApiSubscription;

    @Inject
    public ResultPresenter() {
    }

    @Override
    public void injectSelf(Context context) {
    }

    @Override
    public void onAttachView(@NonNull ResultContract.View view) {
        Preconditions.checkNotNull(view);
        mView = new WeakReference<>(view);

        setupUi();
        loadResultData();
        markLessonAsCompleted();
    }

    @Override
    public void onMainSectionCtaButtonClick() {
        if (checkViewNotNull()) {
            ResultContract.View resultView = getView();
            switch (resultView.getResultType()) {
                case RIGHT:
                case TOPIC_COMPLETED:
                case LESSONS_COMPLETED:
                    // review answers
                    resultView.goToReviewAnswers(mPracticeData, mPracticeFeedbackData);
                    break;
                case ALMOST:
                case WRONG:
                    // watch video again
                    resultView.goBackToLesson();
                    break;
                default:
                    break;
            }
        }
        cleanup();
    }

    @Override
    public void onNavigationNextButtonClick() {
        if (checkViewNotNull()) {
            ResultContract.View resultView = getView();
            switch (resultView.getResultType()) {
                case RIGHT:
                    // next lesson
                    resultView.goToNextLesson();
                    break;
                case TOPIC_COMPLETED:
                    resultView.goToDashboard();
                    break;
                case LESSONS_COMPLETED:
                    resultView.goToAssessment();
                    break;
                case ALMOST:
                case WRONG:
                    // try again
                    resultView.goBackToPractice();
                    break;
                default:
                    break;
            }
        }
        cleanup();
    }

    @Override
    public void onNavigationHomeUpButtonClick() {
        if (checkViewNotNull()) {
            getView().goBackToLesson();
        }
        cleanup();
    }

    @Override
    public void onBackPressed() {
        cleanup();
    }

    private void setupUi() {
        if (checkViewNotNull()) {
            ResultContract.View resultView = getView();
            resultView.setupUi();

            if (mIsTablet) {
                LessonResponse response = mCurrentSessionCache.getLessonResponse();
                final LessonResponseData responseData = response.getResponseData();
                final Lesson lesson = responseData.getLesson();
                resultView.bindTopicContent(lesson);
            }
        }
    }

    private void loadResultData() {
        if (mCurrentSessionCache.getLessonResponse() == null) {
            getView().goToDashboard();
            return;
        }
        LessonResponseData responseData = mCurrentSessionCache.getLessonResponse().getResponseData();
        mPracticeData = responseData.getPractice();
        mPracticeFeedbackData = responseData.getFeedback();
        mTopicColor = mCurrentSessionCache.getLessonToolbarColor();
        if (checkViewNotNull()) {
            ResultContract.View resultView = getView();
            resultView.showLoader();
            resultView.renderFeedback(mPracticeFeedbackData, mPracticeData.getInstructionNextButtonText());
        }
    }

    private boolean checkViewNotNull() {
        return getView() != null;
    }

    private void cleanup() {
        mPracticeData = null;
        mPracticeFeedbackData = null;
        mTopicColor = 0;
    }

    private void markLessonAsCompleted() {
        if (mCurrentSessionCache.getLessonResponse() == null) {
            getView().goToDashboard();
            return;
        }
        if (getView().getResultType() == ResultType.RIGHT) {
            SubscriptionHelper.unsubscribe(mApiSubscription);
            mApiSubscription = mLessonService.markLessonAsComplete(
                    mCurrentSessionCache.getLessonId(),
                    mCurrentSessionCache.getLessonXsrfToken())
                    .doOnCompleted(() -> mConfigStorage.saveShouldUpdateDashboard(true))
                    .subscribe(Actions.empty(), Actions.empty());
        }
    }

    @Override
    public void onFeedbackLoaded() {
        Observable.timer(PreloaderHelper.LOADER_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(this::onDelayedFeedbackLoaded)
                .subscribe();
    }

    private void onDelayedFeedbackLoaded() {
        if (checkViewNotNull()) {
            ResultContract.View resultView = getView();
            resultView.hideLoader();
            resultView.setToolbarColor(mTopicColor);
            resultView.renderImage(mPracticeData.getInstructionImageUrl(), mPracticeData.getLessonBackgroundColor());

            switch (resultView.getResultType()) {
                case RIGHT:
                case TOPIC_COMPLETED:
                case LESSONS_COMPLETED:
                    resultView.renderResultImage(R.drawable.animation_result_ok);
                    break;
                case ALMOST:
                    resultView.renderResultImage(R.drawable.animation_result_x);
                    break;
                case WRONG:
                    resultView.renderResultImage(R.drawable.animation_result_x);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLessonSelected(int lessonId) {
        if (lessonId != mCurrentSessionCache.getLessonId()) {
            mCurrentSessionCache.setLessonId(lessonId);
            if (checkViewNotNull()) {
                ResultContract.View resultView = getView();
                resultView.goToLesson();
            }
        }
    }

    @Override
    public void onDetachView() {
        mView.clear();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    @Override
    public void onDestroy() {
        // empty
    }
}
