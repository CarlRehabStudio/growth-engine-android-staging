package com.google.android.apps.miyagi.development.ui.result;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.AdjustableImageView;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.lesson.TopicLessonAdapter;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.ImageUtils;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.parceler.Parcels;

import javax.inject.Inject;

/**
 * Created by marcin on 15.01.2017.
 */

public class ResultActivity extends BaseActivity<ResultContract.Presenter> implements ResultContract.View {

    @Inject ResultPresenter mPresenter;
    @Inject AppColors mAppColors;
    @Inject Navigator mNavigator;

    @BindView(R.id.result_image_container) View mImageContainer;
    @BindView(R.id.result_image_intro) AdjustableImageView mImageIntro;
    @BindView(R.id.result_image_icon) ImageView mImageIcon;
    @BindView(R.id.result_label_header) TextView mLabelHeader;
    @BindView(R.id.result_label_next_header) TextView mLabelNextHeader;
    @BindView(R.id.result_label_next_description) TextView mLabelNextDescription;
    @BindView(R.id.result_label_links_header) TextView mLabelLinksHeader;
    @BindView(R.id.result_links) RecyclerView mRecyclerViewLinks;
    @BindView(R.id.result_web_view_description) WebView mWebViewDescription;
    @BindView(R.id.result_button_cta) TextView mMainSectionCtaButton;
    @BindView(R.id.button_next) NavigationButton mNavigationNextButton;
    @BindView(R.id.result_extras) LinearLayout mViewResultExtras;
    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;
    @BindView(R.id.topic_extras) View mLessonsContainer;
    @BindView(R.id.lesson_label_all_lessons_title) TextView mLabelAllLessonsTitle;
    @BindView(R.id.lesson_list_topic_lesson) RecyclerView mListTopicLesson;
    @Nullable
    @BindView(R.id.lessons_scroll_view)
    NestedScrollView mListTopicLessonsScrollView;

    private Toolbar mToolbar;

    private ResultType mResultType;
    private PreloaderHelper mPreloaderHelper;

    /**
     * Creates an intent to display result screen.
     */
    public static Intent createIntent(Context context, ResultType type) {
        Bundle extras = new Bundle();
        extras.putParcelable(ResultType.KEY, Parcels.wrap(type));

        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtras(extras);

        return intent;
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ViewUtils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void setupUi() {
        setContentView(R.layout.result_activity);
        ButterKnife.bind(this);

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.result_preloader));

        mBottomNav.setBackgroundColor(mAppColors.getMainBackgroundColor());

        Bundle extras = getIntent().getExtras();
        mResultType = Parcels.unwrap(extras.getParcelable(ResultType.KEY));

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mToolbar.setNavigationIcon(R.drawable.ic_close);

        mMainSectionCtaButton.setOnClickListener(v -> mPresenter.onMainSectionCtaButtonClick());
        mNavigationNextButton.setOnClickListener(v -> mPresenter.onNavigationNextButtonClick());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                mPresenter.onNavigationHomeUpButtonClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setToolbarColor(int color) {
        ToolbarHelper.setColorForStatusAndToolbar(this, color);
    }

    @Override
    public void renderImage(ImagesBaseModel imagesBaseModel, int backgroundColor) {
        mImageContainer.setBackgroundColor(backgroundColor);

        ImageUtils.glideObservable(this, ImageUrlHelper.getUrlFor(this, imagesBaseModel), mImageIntro)
                .subscribe(successful -> {
                    if (successful) {
                        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.result_image);
                        mImageIntro.startAnimation(alphaAnimation);
                    }
                }, throwable -> {

                });
    }

    @Override
    public void renderFeedback(PracticeFeedback feedback, String instructionNextButtonText) {
        setWebViewClient();
        switch (mResultType) {
            case RIGHT:
            case TOPIC_COMPLETED:
            case LESSONS_COMPLETED:
                bindFeedbackRightResult(feedback, instructionNextButtonText);
                break;
            case ALMOST:
                bindFeedbackAlmostResult(feedback);
                break;
            case WRONG:
                bindFeedbackWrongResult(feedback);
                break;
            default:
                break;
        }
    }

    private void setWebViewClient() {
        mWebViewDescription.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mPresenter.onFeedbackLoaded();
            }
        });
    }

    @Override
    public void showLoader() {
        mPreloaderHelper.show();
    }

    @Override
    public void hideLoader() {
        mPreloaderHelper.hideWithoutDelay();
    }

    @Override
    public void renderResultImage(int resId) {
        showResultAnimation(resId);
    }

    private void bindFeedbackWrongResult(PracticeFeedback feedback) {
        mToolbar.setTitle(feedback.getResultHeader());

        mLabelHeader.setText(feedback.getResultHeaderWrong());
        loadFeedbackInWebView(feedback.getResultDescriptionWrong());

        mMainSectionCtaButton.setText(feedback.getResultWatchVideo());
        mNavigationNextButton.setText(feedback.getResultTryAgain());
    }

    private void bindFeedbackAlmostResult(PracticeFeedback feedback) {
        mToolbar.setTitle(feedback.getResultHeader());

        mLabelHeader.setText(feedback.getResultHeaderAlmost());
        loadFeedbackInWebView(feedback.getResultDescriptionAlmost());

        mMainSectionCtaButton.setText(feedback.getResultWatchVideo());
        mNavigationNextButton.setText(feedback.getResultTryAgain());
    }

    private void bindFeedbackRightResult(PracticeFeedback feedback, String instructionNextButtonText) {
        mToolbar.setTitle(feedback.getResultHeader());

        mLabelHeader.setText(feedback.getResultHeaderRight());
        loadFeedbackInWebView(feedback.getResultDescriptionRight());

        mMainSectionCtaButton.setText(feedback.getResultReviewAnswer());
        mNavigationNextButton.setText(instructionNextButtonText);

        mViewResultExtras.setVisibility(View.VISIBLE);

        mLabelNextHeader.setText(feedback.getResultNextStepsTitle());
        mLabelNextDescription.setText(HtmlHelper.fromHtmlWithoutBR(feedback.getResultNextStepsDescription()));

        mLabelLinksHeader.setText(feedback.getAdditionalLinksTitle());

        mRecyclerViewLinks.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLinks.setAdapter(ResultLinkAdapter.create(feedback.getAdditionalLinks()));
    }

    private void loadFeedbackInWebView(String feedbackText) {
        HtmlHelper.bindWebviewWithText(mWebViewDescription, feedbackText);
    }

    public void goBackToLesson() {
        boolean isTopicCompleted = (getResultType().equals(ResultType.TOPIC_COMPLETED));
        boolean isLessonsCompleted = (getResultType().equals(ResultType.LESSONS_COMPLETED));
        mNavigator.navigateBackToLesson(this, isTopicCompleted, isLessonsCompleted);
    }

    public void goBackToPractice() {
        mNavigator.goBackToPractice(this);
    }

    @Override
    public void goToReviewAnswers(Practice practiceData, PracticeFeedback practiceFeedbackData) {
        boolean isTopicCompleted = (getResultType().equals(ResultType.TOPIC_COMPLETED));
        mNavigator.goToReviewAnswers(this, practiceData, practiceFeedbackData, isTopicCompleted);
    }

    public void goToNextLesson() {
        mNavigator.goNextToLesson(this);
    }

    @Override
    public void goToDashboard() {
        mNavigator.navigateToDashboard(this);
    }

    @Override
    public void goToAssessment() {
        mNavigator.navigateToAssessment(this);
    }

    @Override
    public ResultType getResultType() {
        return mResultType;
    }

    @Override
    public void bindTopicContent(final Lesson lesson) {
        mLessonsContainer.setVisibility(View.VISIBLE);
        mLabelAllLessonsTitle.setText(HtmlHelper.fromHtmlToSpanned(lesson.getAllLessonsTitle()));
        TopicLessonAdapter adapter = new TopicLessonAdapter(this, lesson.getTopicLessons(), lesson.getLessonId(), selectedLesson -> {
            mPresenter.onLessonSelected(selectedLesson.getLessonId());
        });
        mListTopicLesson.setLayoutManager(new LinearLayoutManager(this));
        mListTopicLesson.setAdapter(adapter);
    }

    @Override
    public void goToLesson() {
        mNavigator.navigateToLesson(this, getResultType() == ResultType.TOPIC_COMPLETED, getResultType() == ResultType.LESSONS_COMPLETED);
    }

    private void showResultAnimation(int resId) {
        mImageIcon.setImageDrawable(ContextCompat.getDrawable(this, resId));
        mImageIcon.post(() -> {
            Drawable drawable = mImageIcon.getDrawable();
            ((AnimationDrawable) drawable).start();
        });
    }

    @Override
    public ResultPresenter getPresenter() {
        return mPresenter;
    }
}
