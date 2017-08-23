package com.google.android.apps.miyagi.development.ui.practice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.InkPageIndicator;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.practice.booleanselector.BooleanSelectorFragment;
import com.google.android.apps.miyagi.development.ui.practice.clock.ClockPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeResult;
import com.google.android.apps.miyagi.development.ui.practice.fortunewheel.FortuneWheelFragment;
import com.google.android.apps.miyagi.development.ui.practice.large.SelectLargeFragment;
import com.google.android.apps.miyagi.development.ui.practice.reorder.ReorderFragment;
import com.google.android.apps.miyagi.development.ui.practice.rightwrong.SelectRightFragment;
import com.google.android.apps.miyagi.development.ui.practice.strike.StrikeThroughFragment;
import com.google.android.apps.miyagi.development.ui.practice.swipe.SwipeSelectorFragment;
import com.google.android.apps.miyagi.development.ui.practice.switches.SwitchesTextFragment;
import com.google.android.apps.miyagi.development.ui.practice.tag.TagCloudFragment;
import com.google.android.apps.miyagi.development.ui.practice.twitter.TwitterFragment;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.NetworkUtils;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.parceler.Parcels;

import javax.inject.Inject;

public class PracticeActivity extends BaseActivity implements PracticeNavCallback {

    public static final String PRACTICE_FRAGMENT_TAG = "PRACTICE";
    public static final String INSTRUCTION_FRAGMENT_TAG = "INSTRUCTION";

    @Inject AppColors mAppColors;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject Navigator mNavigator;

    @Nullable @BindView(R.id.practice_container) NestedScrollView mMainContainer;
    @BindView(R.id.bottom_navigation_practice) View mBottomNav;
    @BindView(R.id.content) View mPracticeContent;
    private NavigationButton mNavBtnSubmit;
    private NavigationButton mNavBtnPrev;
    private InkPageIndicator mPageIndicator;
    private View mBottomNavInstruction;

    private Toolbar mToolbar;
    private Menu mMenu;
    private boolean mIsSuccessful;
    private Practice mPracticeData;
    private PracticeNavCallback.Callback mNavigationCallback;
    private boolean mIsTopicCOmpleted;
    private boolean mShouldShowMenu;

    /**
     * Creates new PracticeActivity with practice data.
     */
    public static Intent createIntent(Context context, Practice practiceData, PracticeFeedback practiceFeedback) {
        return createIntent(context, practiceData, practiceFeedback, PracticeResult.FAIL);
    }

    /**
     * Creates new PracticeActivity with practice data and is successful status.
     */
    public static Intent createIntent(Context context, Practice practiceData, PracticeFeedback practiceFeedback, PracticeResult practiceResult) {
        Bundle extras = new Bundle();
        extras.putParcelable(Practice.ARG_DATA, Parcels.wrap(practiceData));
        extras.putParcelable(PracticeFeedback.ARG_KEY, Parcels.wrap(practiceFeedback));
        extras.putBoolean(AbstractPracticeFragment.BundleKey.SUCCESS, practiceResult == PracticeResult.SUCCESSFUL || practiceResult == PracticeResult.TOPIC_COMPLETED);
        extras.putBoolean(AbstractPracticeFragment.BundleKey.TOPIC_COMPLETED, practiceResult == PracticeResult.TOPIC_COMPLETED);

        Intent intent = new Intent(context, PracticeActivity.class);
        intent.putExtras(extras);

        return intent;
    }

    /**
     * Gets practice step by activity type.
     *
     * @param practice the practice.
     *
     * @return practice step Practice.Type.
     */
    public static PracticeNavCallback.Step getPracticeStep(Practice practice) {
        String activityTypeName = practice.getActivityType();
        switch (activityTypeName) {
            case Practice.Type.SWIPE_SELECTOR:
                return PracticeNavCallback.Step.SWIPE_SELECTOR;
            case Practice.Type.TEXT_DRAWER:
                return PracticeNavCallback.Step.FORTUNE_WHEEL;
            case Practice.Type.STRIKE_THROUGH:
                return PracticeNavCallback.Step.STRIKE_SELECTOR;
            case Practice.Type.TAG_CLOUD:
                return PracticeNavCallback.Step.TAG_SELECTOR;
            case Practice.Type.TWITTER_DRAGANDDROP:
                return PracticeNavCallback.Step.TWITTER;
            case Practice.Type.SWITCHES_TEXT:
                return PracticeNavCallback.Step.SWITCHES;
            case Practice.Type.SELECT_LARGE:
                return PracticeNavCallback.Step.LARGE;
            case Practice.Type.IMAGE_SLIDER:
                return PracticeNavCallback.Step.CLOCK;
            case Practice.Type.REORDER:
                return PracticeNavCallback.Step.REORDER;
            case Practice.Type.SELECT_RIGHT:
                return PracticeNavCallback.Step.SELECT_RIGHT;
            case Practice.Type.BOOLEAN_SELECTOR:
                return PracticeNavCallback.Step.BOOLEAN_SELECTOR;
            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_activity);
        ButterKnife.bind(this);

        if (mCurrentSessionCache.getLessonResponse() == null) {
            mNavigator.navigateToDashboard(this);
            return;
        }

        Bundle extras = getIntent().getExtras();
        mPracticeData = Parcels.unwrap(extras.getParcelable(Practice.ARG_DATA));
        mIsSuccessful = extras.getBoolean(AbstractPracticeFragment.BundleKey.SUCCESS);
        mIsTopicCOmpleted = extras.getBoolean(AbstractPracticeFragment.BundleKey.TOPIC_COMPLETED);

        int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
        mAnalyticsHelper.trackScreen(String.format(getString(R.string.screen_lesson_activity), lessonId));

        initViews();
        initToolbar();
        initBottomNavigation();

        if (savedInstanceState == null) {
            goToFirstStep(mIsSuccessful);
        }

    }

    private void initViews() {
        mNavBtnPrev = (NavigationButton) mBottomNav.findViewById(R.id.button_prev);
        mNavBtnSubmit = (NavigationButton) mBottomNav.findViewById(R.id.button_next);
        mPageIndicator = (InkPageIndicator) mBottomNav.findViewById(R.id.bottom_nav_dot_pager_indicator);

        if (ViewUtils.isLandTablet(this)) {
            mBottomNavInstruction = findViewById(R.id.bottom_navigation_instruction);
            mBottomNavInstruction.findViewById(R.id.button_next).setVisibility(View.INVISIBLE);
        }
    }

    private void initToolbar() {
        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setTitle(mPracticeData.getInstructionTitleText());
        ToolbarHelper.setColorForStatusAndToolbar(this, mCurrentSessionCache.getLessonToolbarColor());
    }

    private void initBottomNavigation() {
        mBottomNav.setBackgroundColor(mAppColors.getMainBackgroundColor());
        if (mBottomNavInstruction != null) {
            mBottomNavInstruction.setBackgroundColor(mAppColors.getMainBackgroundColor());
        }
        mNavBtnSubmit.setDisabledOnClickListener(v ->
                showInfoSnackbar(findViewById(R.id.practice_coordinator),
                        mPracticeData.getSubmitSnackbarText()));
        mNavBtnSubmit.setOnClickListener(view -> mNavigationCallback.onNextClick());
        mNavBtnPrev.setOnClickListener(view -> mNavigationCallback.onPrevClick());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.put_into_practice_menu, menu);
        if (mShouldShowMenu) {
            showInfoButton();
        }
        return true;
    }

    private void goToFirstStep(boolean isSuccessful) {
        if (!ViewUtils.isTablet(this)) {
            if (!isSuccessful) {
                goToInstruction();
            } else {
                goToStepRqst(getPracticeStep(mPracticeData), isSuccessful);
            }
        } else {
            goToInstruction();
            goToStepRqst(getPracticeStep(mPracticeData), isSuccessful);
        }
    }

    private void goToInstruction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment instructionFragment = fragmentManager.findFragmentByTag(INSTRUCTION_FRAGMENT_TAG);
        if (instructionFragment != null) {
            fragmentManager.beginTransaction().remove(instructionFragment).commit();
        }
        instructionFragment = InstructionFragment.newInstance(mPracticeData);
        int container;
        boolean addToBackStack;
        if (!ViewUtils.isTablet(this)) {
            container = R.id.content;
            addToBackStack = true;
        } else {
            container = R.id.instruction_content;
            addToBackStack = false;
        }
        transitFragment(instructionFragment, container, addToBackStack, INSTRUCTION_FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Return to lesson activity
                finish();
                return true;
            case R.id.action_practice_info:
                showInstructionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInstructionDialog() {
        int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();
        mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson_activity), lessonId), getString(R.string.event_category_navigation), getString(R.string.event_action_button), getString(R.string.event_label_info));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(mPracticeData.getInstructionDialogTitle())
                .setMessage(HtmlHelper.fromHtml(mPracticeData.getInstructionDialogText()))
                .setCancelable(true)
                .setPositiveButton(mPracticeData.getInstructionDialogOk(), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showInfoButton() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_practice_info).setVisible(true);
            mShouldShowMenu = false;
        } else {
            mShouldShowMenu = true;
        }
    }

    @Override
    public void hideInfoButton() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_practice_info).setVisible(false);
        }
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void goToStepRqst(Step step) {
        goToStepRqst(step, false);
    }

    @Override
    public void goToStepRqst(Step step, boolean isSuccessful) {
        if (NetworkUtils.isOffline(this)) {
            mNavigator.navigateToDashboard(this);
            return;
        }

        int lessonId = mCurrentSessionCache.getLessonResponse().getResponseData().getLesson().getLessonId();

        switch (step) {
            case RESULT_CORRECT:
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson_activity), lessonId), getString(R.string.event_category_activity), getString(R.string.event_action_feedback), getString(R.string.event_label_true));
                if (mIsTopicCOmpleted) {
                    mNavigator.launchResultRightTopicCompleted(this);
                } else {
                    mNavigator.launchResultRight(this);
                }
                finish();
                break;
            case RESULT_ALMOST:
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson_activity), lessonId), getString(R.string.event_category_activity), getString(R.string.event_action_feedback), getString(R.string.event_label_partial));
                mNavigator.launchResultAlmost(this);
                break;
            case RESULT_INCORRECT:
                mAnalyticsHelper.trackEvent(String.format(getString(R.string.screen_lesson_activity), lessonId), getString(R.string.event_category_activity), getString(R.string.event_action_feedback), getString(R.string.event_label_false));
                mNavigator.launchResultWrong(this);
                break;
            default:
                goToPracticeStep(step, isSuccessful);
        }
    }

    private void goToPracticeStep(Step step, boolean isSuccessful) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(PRACTICE_FRAGMENT_TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        switch (step) {
            case SWIPE_SELECTOR:
                fragment = SwipeSelectorFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case STRIKE_SELECTOR:
                fragment = StrikeThroughFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case TAG_SELECTOR:
                fragment = TagCloudFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case TWITTER:
                fragment = TwitterFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case SWITCHES:
                fragment = SwitchesTextFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case LARGE:
                fragment = SelectLargeFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case FORTUNE_WHEEL:
                fragment = FortuneWheelFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case CLOCK:
                fragment = ClockPracticeFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case BOOLEAN_SELECTOR:
                fragment = BooleanSelectorFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case SELECT_RIGHT:
                fragment = SelectRightFragment.newInstance(mPracticeData, isSuccessful);
                break;
            case REORDER:
                fragment = ReorderFragment.newInstance(mPracticeData, isSuccessful);
                break;
            default:
                Lh.e(this, "Unknown practice step!");
                return;
        }
        transitFragment(fragment, PRACTICE_FRAGMENT_TAG);
    }

    private void transitFragment(Fragment fragment, String tag) {
        transitFragment(fragment, R.id.content, !ViewUtils.isTablet(this), tag);
    }

    private void transitFragment(Fragment fragment, int container, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
        transaction.replace(container, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.toString());
        }
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            fragmentManager.popBackStack();
            if (backStackEntryCount == 1) {
                hideInfoButton();
            }
        } else {
            finish();
        }
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mPracticeContent.setBackgroundColor(backgroundColor);
        if (mMainContainer != null) {
            mMainContainer.setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public void setNavigationCallback(Callback callback) {
        mNavigationCallback = callback;
    }

    @Override
    public void setNextEnable(boolean enable) {
        if (mNavBtnSubmit != null) {
            mNavBtnSubmit.setEnabled(enable);
        }
    }

    @Override
    public void setPrevEnable(boolean enable) {
        if (mNavBtnPrev != null) {
            mNavBtnPrev.setEnabled(enable);
        }
    }

    @Override
    public void setNextButtonVisibility(int visibility) {
        if (mNavBtnSubmit != null) {
            mNavBtnSubmit.setVisibility(visibility);
        }
    }

    @Override
    public void setPrevButtonVisibility(int visibility) {
        if (mNavBtnSubmit != null) {
            mNavBtnPrev.setVisibility(visibility);
        }
    }

    @Override
    public void setNextButtonText(String text) {
        if (mNavBtnSubmit != null) {
            mNavBtnSubmit.setText(text);
        }
    }

    @Override
    public void setNavInstructionVisibility(int visibility) {
        if (mBottomNavInstruction != null) {
            mBottomNavInstruction.setVisibility(visibility);
        }
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (mPageIndicator != null) {
            if (viewPager != null) {
                mPageIndicator.setVisibility(View.VISIBLE);
                mPageIndicator.setViewPager(viewPager);
            } else {
                mPageIndicator.setVisibility(View.GONE);
            }
        }
    }
}
