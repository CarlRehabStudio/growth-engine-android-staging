package com.google.android.apps.miyagi.development.ui.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.onboarding.OnboardingStep;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 19.12.2016.
 */

public class OnboardingBaseFragment extends Fragment {

    @BindView(R.id.content_container) LinearLayout mContentContainer;
    @BindView(R.id.onboarding_image) ImageView mImage;
    @BindView(R.id.onboarding_header) TextView mHeader;
    @BindView(R.id.onboarding_sub_header) TextView mSubheader;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;

    private OnboardingStep mStepData;

    private Unbinder mUnbinder;

    public OnboardingBaseFragment() {
    }

    /**
     * New instance of OnboardingBase fragment.
     *
     * @param onboardingStep - OnboardingStep data.
     * @return OnboardingBaseFragment.
     */
    public static OnboardingBaseFragment newInstance(OnboardingStep onboardingStep) {
        Bundle args = new Bundle();
        args.putParcelable(OnboardingStep.ARG_KEY, Parcels.wrap(onboardingStep));
        OnboardingBaseFragment fragment = new OnboardingBaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mStepData = Parcels.unwrap(args.getParcelable(OnboardingStep.ARG_KEY));
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initUi();
        return view;
    }

    void initUi() {
        mHeader.setText(mStepData.getTitle());
        mSubheader.setText(mStepData.getDescription());

        initAnimation();
        animateScreen();
    }

    public void initAnimation() {
        mScreenAnimationHelper.addAsyncImageView(ImageUrlHelper.getUrlFor(getContext(), mStepData.getImages()), mImage);
        mScreenAnimationHelper.addFlatView(mContentContainer);
    }

    public void animateScreen() {
        mScreenAnimationHelper.animateScreen();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    public void hideContent() {
        mContentContainer.setVisibility(View.INVISIBLE);
        mImage.setVisibility(View.INVISIBLE);
    }
}
