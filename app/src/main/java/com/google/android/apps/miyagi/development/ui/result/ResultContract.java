package com.google.android.apps.miyagi.development.ui.result;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;

/**
 * Created by marcin on 15.01.2017.
 */

public interface ResultContract {

    interface View extends BaseView<ResultContract.Presenter> {

        void setupUi();

        void bindTopicContent(Lesson lesson);

        void renderImage(ImagesBaseModel imagesBaseModel, int backgroundColor);

        void renderFeedback(PracticeFeedback feedback, String instructionNextButtonText);

        void showLoader();

        void hideLoader();

        void setToolbarColor(int color);

        void goBackToLesson();

        void goBackToPractice();

        void goToReviewAnswers(Practice practiceData, PracticeFeedback practiceFeedbackData);

        void goToNextLesson();

        void goToDashboard();

        void goToAssessment();

        ResultType getResultType();

        void renderResultImage(int resId);

        void goToLesson();
    }

    interface Presenter extends BasePresenter<ResultContract.View> {

        void onMainSectionCtaButtonClick();

        void onNavigationNextButtonClick();

        void onNavigationHomeUpButtonClick();

        void onBackPressed();

        void onFeedbackLoaded();

        void onLessonSelected(int lessonId);
    }
}
