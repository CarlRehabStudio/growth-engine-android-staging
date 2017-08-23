package com.google.android.apps.miyagi.development.ui.assessment.result;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.Copy;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyItem;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.assessment.common.AssessmentAbstractFragment;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation;
import com.google.android.apps.miyagi.development.ui.components.widget.AdjustableImageView;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.utils.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;
import rx.android.schedulers.AndroidSchedulers;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by marcin on 18.12.2016.
 */

public class ResultFragment extends AssessmentAbstractFragment {

    @Inject Navigator mNavigator;

    @BindView(R.id.result_container) View mResultContainer;
    @Nullable @BindView(R.id.assessment_result_image) AdjustableImageView mResultImage;
    @BindView(R.id.assessment_result_image_icon) ImageView mResultIcon;
    @BindView(R.id.assessment_result_label_header) TextView mLabelHeader;
    @BindView(R.id.assessment_result_label_description) TextView mLabelDescription;
    @BindView(R.id.button_next) NavigationButton mButtonNext;
    @BindView(R.id.assessment_bottom_content) View mAssesmentBottomNavContent;

    private Copy mCopy;
    private boolean mNoAttemptsLeft;
    private int mMainBackgroundColor;
    private Unbinder mUnbinder;

    public ResultFragment() {
    }

    /**
     * Creates new Result fragment with assessment result data.
     */
    public static ResultFragment newInstance(Copy copy, boolean noAttemptsLeft, int topColor, int mainBackgroundColor) {
        Bundle args = new Bundle();
        args.putParcelable(ArgsKey.COPY, Parcels.wrap(copy));
        args.putBoolean(ArgsKey.NO_ATTEMPTS_LEFT, noAttemptsLeft);
        args.putInt(ArgsKey.MAIN_BACKGROUND_COLOR, mainBackgroundColor);
        args.putInt(ArgsKey.TOP_COLOR, topColor);

        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
        if (!ViewUtils.isTablet(getContext())) {
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assessment_result_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

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
        mButtonNext.enable();
    }

    private void bindData() {
        Bundle args = getArguments();
        mCopy = Parcels.unwrap(args.getParcelable(ArgsKey.COPY));
        mNoAttemptsLeft = args.getBoolean(ArgsKey.NO_ATTEMPTS_LEFT);
        mMainBackgroundColor = args.getInt(ArgsKey.MAIN_BACKGROUND_COLOR);
        CopyInstructions copyInstructions = mCopy.getInstructions();
        CopyItem copyFailed = mCopy.getFailed();
        if (mNoAttemptsLeft) {
            copyFailed = mCopy.getFailedNoAttempts();
        }

        mAssesmentBottomNavContent.setBackgroundColor(mMainBackgroundColor);
        mButtonNext.setText(copyFailed.getCta());
        mButtonNext.setOnClickListener(view -> {
            if (mNoAttemptsLeft) {
                mNavigator.navigateToDashboard(getContext());
            } else {
                mNavigation.goTo(Navigation.Step.TEST);
            }
        });

        mLabelHeader.setText(copyFailed.getTitle());
        mLabelDescription.setText(copyFailed.getSubhead());

        mResultIcon.setImageResource(R.drawable.animation_result_x);

        if (ViewUtils.isTablet(getContext())) {
            mResultContainer.setBackgroundColor(mCopy.getInstructions().getImageBackgroundColor());
            animateResultIcon();
        } else {
            mResultImage.setBackgroundColor(copyInstructions.getImageBackgroundColor());
            ImageUtils.glideObservable(getContext(), ImageUrlHelper.getUrlFor(getContext(), copyInstructions.getImageUrl()), mResultImage)
                    .delay(PreloaderHelper.LOADER_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(successful -> {
                        if (successful) {
                            Animation alphaAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.result_image);
                            mResultImage.startAnimation(alphaAnimation);
                        }
                        animateResultIcon();
                    }, throwable -> {

                    });
        }
    }

    private void animateResultIcon() {
        AnimationDrawable animation = (AnimationDrawable) mResultIcon.getDrawable();
        animation.start();
    }

    interface ArgsKey {
        String COPY = "RESULT_FRAGMENT_COPY";
        String NO_ATTEMPTS_LEFT = "NO_ATTEMPTS_LEFT";
        String MAIN_BACKGROUND_COLOR = "MAIN_BACKGROUND_COLOR";
        String TOP_COLOR = "TOP_COLOR";
    }
}
