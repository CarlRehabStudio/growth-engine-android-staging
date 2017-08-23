package com.google.android.apps.miyagi.development.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerBaseActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.Mode;
import com.google.android.apps.miyagi.development.ui.audio.transcript.AudioTranscriptActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.common.UpNextActionType;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicenceActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicencesListActivity;
import com.google.android.apps.miyagi.development.ui.diagnostics.DiagnosticsActivity;
import com.google.android.apps.miyagi.development.ui.diagnostics.loading.DiagnosticsLoadingActivity;
import com.google.android.apps.miyagi.development.ui.lesson.LessonBaseActivity;
import com.google.android.apps.miyagi.development.ui.practice.PracticeActivity;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.ui.result.ResultActivity;
import com.google.android.apps.miyagi.development.ui.result.ResultType;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

    @Inject CurrentSessionCache mCurrentSessionCache;

    @Inject
    public Navigator() {
        // empty
    }

    /**
     * Navigates to current lesson screen.
     */
    public void navigateToLesson(Context context, boolean isTopicCompleted, boolean isLessonsCompleted) {
        if (context != null) {
            Intent intentToLaunch = LessonBaseActivity.getCallingIntent(context, isTopicCompleted, isLessonsCompleted);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates back to current lesson screen.
     */
    public void navigateBackToLesson(Context context, boolean isTopicCompleted, boolean isLessonsCompleted) {
        if (context != null) {
            Intent intentToLaunch = LessonBaseActivity.getCallingIntent(context, isTopicCompleted, isLessonsCompleted);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to topic assessment screen.
     */
    public void navigateToAssessment(Context context) {
        if (context != null) {
            Intent intentToLaunch = AssessmentActivity.createIntent(context, mCurrentSessionCache.getTopicId(), UpNextActionType.TOPIC_ASSESMENT);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to certification assessment screen.
     */
    public void navigateToCertificationAssessment(Context context) {
        if (context != null) {
            Intent intentToLaunch = AssessmentActivity.createIntent(context, mCurrentSessionCache.getTopicId(), UpNextActionType.CERTIFICATION_ASSESMENT);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to browser screen.
     */
    public void navigateToBrowser(Context context, String url) {
        if (context != null) {
            Intent intentToLaunch = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intentToLaunch.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intentToLaunch);
            }
        }
    }

    /**
     * Navigates to dashboard screen.
     */
    public void navigateToDashboard(Context context) {
        if (context != null) {
            Intent intentToLaunch = DashboardActivity.createIntent(context, false);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to audio player screen.
     */
    public void navigateToAudioPlayer(Context context) {
        if (context != null) {
            Intent intentToLaunch = AudioPlayerBaseActivity.getCallingIntent(context, Mode.ONLINE);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to audio transcript screen.
     */
    public void navigateToAudioTranscript(Context context, int currLessonId) {
        if (context != null) {
            Intent intentToLaunch = AudioTranscriptActivity.getCallingIntent(context, currLessonId);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to diagnostics screen.
     */
    public void navigateToDiagnostics(Context context) {
        if (context != null) {
            Intent intentToLaunch = DiagnosticsActivity.createIntent(context);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to diagnostics loading screen launched from skip button.
     */
    public void navigateToLoadingWithSkip(Context context) {
        if (context != null) {
            Intent intentToLaunch = DiagnosticsLoadingActivity.createIntentWithSkip(context);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to diagnostics loading screen launched from submit button with selected goals.
     */
    public void navigateToLoadingWithSubmit(Context context, String persona, List<Integer> goals) {
        if (context != null) {
            Intent intentToLaunch = DiagnosticsLoadingActivity.createIntentWithSubmit(context, persona, goals);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to diagnostics loading screen launched from submit button with certification selected.
     */
    public void navigateToLoadingWithSubmitCertification(Context context) {
        if (context != null) {
            Intent intentToLaunch = DiagnosticsLoadingActivity.createIntentWithSubmitCertification(context);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to screen with list of licences.
     */
    public void navigateToLicencesList(Context context, String title) {
        if (context != null) {
            Intent intentToLaunch = LicencesListActivity.createIntent(context, title);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to license screen.
     */
    public void navigateToLicence(Context context, Pair<String, Integer> licence) {
        if (context != null) {
            Intent intentToLaunch = LicenceActivity.createIntent(context, licence);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to practice screen.
     */
    public void launchPractice(Context context, Practice data, PracticeFeedback feedback, PracticeResult practiceResult) {
        if (context != null) {
            Intent intentToLaunch = PracticeActivity.createIntent(context, data, feedback, practiceResult);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Navigates to result right screen.
     */
    public void launchResultRight(Activity context) {
        if (context != null) {
            Intent intentToLaunch = ResultActivity.createIntent(context, ResultType.RIGHT);
            context.startActivity(intentToLaunch);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    /**
     * Navigates to result right screen, but return to dashboard on next click.
     */
    public void launchResultRightTopicCompleted(Activity context) {
        if (context != null) {
            Intent intentToLaunch = ResultActivity.createIntent(context, ResultType.TOPIC_COMPLETED);
            context.startActivity(intentToLaunch);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    /**
     * Navigates to result right screen, but go to Assessment on next click.
     */
    public void launchResultRightLessonsCompleted(Activity context) {
        if (context != null) {
            Intent intentToLaunch = ResultActivity.createIntent(context, ResultType.LESSONS_COMPLETED);
            context.startActivity(intentToLaunch);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    /**
     * Navigates to result wrong screen.
     */
    public void launchResultWrong(Activity context) {
        if (context != null) {
            Intent intentToLaunch = ResultActivity.createIntent(context, ResultType.WRONG);
            context.startActivity(intentToLaunch);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    /**
     * Navigates to result almost screen.
     */
    public void launchResultAlmost(Activity context) {
        if (context != null) {
            Intent intentToLaunch = ResultActivity.createIntent(context, ResultType.ALMOST);
            context.startActivity(intentToLaunch);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    /**
     * Navigates back to practice.
     */
    public void goBackToPractice(Context context) {
        if (context != null && context instanceof BaseActivity) {
            ((BaseActivity) context).finish();
            ((BaseActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    /**
     * Navigates back to practice.
     */
    public void goToReviewAnswers(Context context, Practice practiceData, PracticeFeedback practiceFeedbackData, boolean isTopicCompleted) {
        if (context != null && context instanceof BaseActivity) {
            launchPractice(context, practiceData, practiceFeedbackData, isTopicCompleted ? PracticeResult.TOPIC_COMPLETED : PracticeResult.SUCCESSFUL);
            ((BaseActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            ((BaseActivity) context).finish();
        }
    }

    /**
     * Navigates to screen with next lesson from current.
     */
    public void goNextToLesson(Context context) {
        if (context != null) {
            int nextLessonId = mCurrentSessionCache.getNextLessonId();
            if (nextLessonId == Lesson.ASSESSMENT) {
                navigateToAssessment(context);
            } else {
                // start next lesson
                mCurrentSessionCache.setLessonId(nextLessonId);
                navigateToLesson(context, false, false);
            }
        }
    }
}
