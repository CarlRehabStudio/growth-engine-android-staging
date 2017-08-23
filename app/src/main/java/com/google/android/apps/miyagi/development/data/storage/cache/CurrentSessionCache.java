package com.google.android.apps.miyagi.development.data.storage.cache;

import com.google.android.apps.miyagi.development.data.models.assessment.ExamPassedAction;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.TopicLesson;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileUpdateRequestData;
import com.google.android.apps.miyagi.development.ui.audio.player.Mode;
import com.google.android.apps.miyagi.development.ui.lesson.LessonState;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by marcin on 15.01.2017.
 */

@Singleton
public class CurrentSessionCache {

    private int mLessonId;
    private int mTopicId = -1;
    private OnboardingUserType mOnboardingUserType = OnboardingUserType.RETURNING_USER;

    private AssessmentResponse mAssessmentResponse;
    private ExamPassedAction mAssessmentPassedAction;
    private LessonResponse mLessonResponse;
    private DashboardResponse mDashboardResponse;
    private AudioResponseData mAudioResponseData;
    private Mode mAudioMode;
    private ProfileResponseData mProfileResponseData;
    private ProfileUpdateRequestData mProfileUpdateRequestData;
    private DiagnosticsResponseData mDiagnosticsResponseData;


    @Inject
    public CurrentSessionCache() {
    }

    /**
     * Removes all responses stored in CurrentSessionCache.
     */
    public void clearAllData() {
        mAssessmentResponse = null;
        mLessonResponse = null;
        mAudioResponseData = null;
        mAssessmentPassedAction = null;
        mAudioMode = null;
        mProfileResponseData = null;
        mProfileUpdateRequestData = null;
        mDiagnosticsResponseData = null;
        mDashboardResponse = null;
    }

    public DiagnosticsResponseData getDiagnosticsResponseData() {
        return mDiagnosticsResponseData;
    }

    public void setDiagnosticsResponseData(DiagnosticsResponseData diagnosticsResponseData) {
        mDiagnosticsResponseData = diagnosticsResponseData;
    }

    public ExamPassedAction getAssessmentPassedAction() {
        return mAssessmentPassedAction;
    }

    public void setAssessmentPassedAction(ExamPassedAction action) {
        mAssessmentPassedAction = action;
    }

    public DashboardResponse getDashboardResponse() {
        return mDashboardResponse;
    }

    public void setDashboardResponse(DashboardResponse dashboardResponse) {
        mDashboardResponse = dashboardResponse;
    }

    public OnboardingUserType getOnboardingUserType() {
        return mOnboardingUserType;
    }

    public void setOnboardingUserType(OnboardingUserType type) {
        mOnboardingUserType = type;
    }

    public LessonResponse getLessonResponse() {
        return mLessonResponse;
    }

    public void setLessonResponse(LessonResponse response) {
        mLessonResponse = response;
    }

    public int getAssementToolbarColor() {
        return mAssessmentResponse.getResponseData().getCopy().getTopicColor();
    }

    public AssessmentResponse getAssessmentResponse() {
        return mAssessmentResponse;
    }

    public void setAssessmentResponse(AssessmentResponse assessmentResponse) {
        mAssessmentResponse = assessmentResponse;
    }

    public int getLessonToolbarColor() {
        return mLessonResponse.getResponseData().getLesson().getTopicColor();
    }

    public int getLessonId() {
        return mLessonId;
    }

    public void setLessonId(int id) {
        mLessonId = id;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public void setTopicId(int id) {
        mTopicId = id;
    }

    public AudioResponseData getAudioResponseData() {
        return mAudioResponseData;
    }

    public void setAudioResponseData(AudioResponseData response, Mode mode) {
        mAudioResponseData = response;
        mAudioMode = mode;
    }

    public Mode getAudioMode() {
        return mAudioMode;
    }

    /**
     * Gets next lesson id.
     */
    public int getNextLessonId() {
        List<TopicLesson> topicLessons = mLessonResponse.getResponseData().getLesson().getTopicLessons();
        for (TopicLesson topic : topicLessons) {
            if (topic.getLessonState() != LessonState.COMPLETED.getValue()
                    && topic.getLessonId() != mLessonId) {
                return topic.getLessonId();
            }
        }

        return Lesson.ASSESSMENT;
    }

    public XsrfToken getLessonXsrfToken() {
        return mLessonResponse.getResponseData().getLessonXsrfToken();
    }

    public ProfileResponseData getProfileResponseData() {
        return mProfileResponseData;
    }

    public void setProfileResponseData(ProfileResponseData profileResponseData) {
        mProfileResponseData = profileResponseData;
    }

    public ProfileUpdateRequestData getProfileUpdateRequestData() {
        return mProfileUpdateRequestData;
    }

    public void setProfileUpdateRequestData(ProfileUpdateRequestData updateData) {
        mProfileUpdateRequestData = updateData;
    }

    public void removeAudioCache() {
        mAudioMode = null;
        mAudioResponseData = null;
    }

    public enum OnboardingUserType {
        RETURNING_USER, NEW_USER
    }

    public interface ArgKey {
        String LESSON_ID = "LESSON_ID";
    }
}
