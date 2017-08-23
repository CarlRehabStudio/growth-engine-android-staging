package com.google.android.apps.miyagi.development.ui.assessment.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyItem;
import com.google.android.apps.miyagi.development.data.models.assessment.CopySocial;
import com.google.android.apps.miyagi.development.data.models.assessment.ExamPassedAction;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponseData;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.assessment.AssessmentActivity;
import com.google.android.apps.miyagi.development.ui.assessment.common.AssessmentAbstractFragment;
import com.google.android.apps.miyagi.development.ui.components.widget.GifImageView;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import javax.inject.Inject;

/**
 * Created by marcin on 18.12.2016.
 */

public class BadgeFragment extends AssessmentAbstractFragment {

    @Inject Navigator mNavigator;
    @Inject CurrentSessionCache mCurrentSessionCache;

    @BindView(R.id.root_container) LinearLayout mRootContainer;
    @BindView(R.id.label_header) TextView mLabelHeader;
    @BindView(R.id.label_description) TextView mLabelDescription;
    @BindView(R.id.gif_badge) GifImageView mGifBadge;
    @BindView(R.id.button_share) FloatingActionButton mButtonShare;
    @BindView(R.id.badge_label_share) TextView mLabelShare;
    @BindView(R.id.button_next) NavigationButton mButtonNext;
    @BindView(R.id.assessment_bottom_content) View mAssesmentBottomNavContent;

    private int mMainBackgroundColor;
    private Unbinder mUnbinder;

    public BadgeFragment() {
    }

    /**
     * Creates new Badge fragment.
     */
    public static BadgeFragment newInstance(Copy copy, int mainBackgroundColor) {
        Bundle args = new Bundle();
        args.putParcelable(ArgsKey.COPY, Parcels.wrap(copy));
        args.putInt(AssessmentActivity.ArgsKey.MAIN_BACKGROUND_COLOR, mainBackgroundColor);

        BadgeFragment fragment = new BadgeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMainBackgroundColor = args.getInt(AssessmentActivity.ArgsKey.MAIN_BACKGROUND_COLOR);
        if (!ViewUtils.isTablet(getContext())) {
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assessment_badge_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);

        setupUi();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    private void setupUi() {
        mRootContainer.setBackgroundColor(mMainBackgroundColor);
        mAssesmentBottomNavContent.setBackgroundColor(mMainBackgroundColor);

        mButtonNext.enable();
    }

    private void bindData() {
        if (mCurrentSessionCache.getAssessmentResponse() == null) {
            mNavigator.navigateToDashboard(getContext());
            return;
        }

        AssessmentResponseData responseData = mCurrentSessionCache.getAssessmentResponse().getResponseData();
        Copy copy = responseData.getCopy();
        CopySocial copySocial = copy.getCopySocial();

        ExamPassedAction action = mCurrentSessionCache.getAssessmentPassedAction();
        if (action != null) {
            CopyItem passedItem = null;
            switch (action.getType()) {
                case 1: // go to topic
                    passedItem = copy.getPassedTopic();
                    mButtonNext.setOnClickListener(view -> {
                        mCurrentSessionCache.setLessonId(action.getLessonId());
                        mCurrentSessionCache.setTopicId(action.getTopicId());
                        mNavigator.navigateToLesson(getContext(), false, false);
                        getActivity().finish();
                    });
                    break;
                case 2: // go to assessment
                    passedItem = copy.getPassedTopic();
                    mButtonNext.setOnClickListener(view -> {
                        mCurrentSessionCache.setLessonId(action.getLessonId());
                        mCurrentSessionCache.setTopicId(action.getTopicId());
                        mNavigator.navigateToAssessment(getContext());
                    });
                    break;
                case 3: // go to certification assessment
                    passedItem = copy.getPassedCert();
                    mButtonNext.setOnClickListener(view -> {
                        mCurrentSessionCache.setLessonId(action.getLessonId());
                        mCurrentSessionCache.setTopicId(action.getTopicId());
                        mNavigator.navigateToCertificationAssessment(getContext());
                    });
                    break;
                case 5: // repeat current assessment
                    // empty - handled in ExamFragment
                    break;
                default:
                    break;
            }

            if (passedItem != null) {
                mLabelHeader.setText(passedItem.getTitle());
                mLabelDescription.setText(passedItem.getSubhead());
                mLabelShare.setText(copySocial.getTitle());
                ColorHelper.tintForeground(mButtonShare.getDrawable(), mMainBackgroundColor);
                mButtonNext.setText(passedItem.getCta());
            }
        } else {
            // final certification
            CopyItem passedCertification = copy.getPassedCert();
            mButtonNext.setOnClickListener(view -> mNavigator.navigateToDashboard(getContext()));
            mLabelHeader.setText(passedCertification.getTitle());
            if (passedCertification.getSubhead() != null) {
                mLabelDescription.setText(passedCertification.getSubhead());
            } else {
                mLabelDescription.setVisibility(View.GONE);
            }
            mButtonNext.setText(passedCertification.getCta());
        }

        if (copySocial != null) {
            mButtonShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, copySocial.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, copySocial.getShareUrl());
                startActivity(Intent.createChooser(shareIntent, copySocial.getAriaLabel()));
            });

        } else {
            mButtonShare.setVisibility(View.GONE);
            mLabelShare.setVisibility(View.GONE);
        }

        mGifBadge.setGifImageResource(R.drawable.badge_bg);
    }

    interface ArgsKey {
        String COPY = "BADGE_FRAGMENT_COPY";
    }
}
