package com.google.android.apps.miyagi.development.ui.practice.twitter;

import android.animation.ArgbEvaluator;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter.TwitterPracticeDetails;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.practice.common.AbstractPracticeFragment;
import com.google.android.apps.miyagi.development.ui.practice.common.PracticeNavCallback;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import org.parceler.Parcels;

/**
 * Created by marcinarciszew on 17.11.2016.
 */

public class TwitterFragment extends AbstractPracticeFragment {

    private static final int NONE_PAGE_SELECTED = 0;

    private static final int STROKE_WIDTH_DP = 2;
    private static final int STROKE_DASH_WIDTH_DP = 8;
    private static final int STROKE_DASH_GAP_DP = 4;
    private static final int CARD_ELEVATION_DP = 2;

    @BindView(R.id.twitter_label_question) TextView mLabelQuestion;
    @BindView(R.id.twitter_image_logo) ImageView mImageLogo;
    @BindView(R.id.twitter_label_logo_title) TextView mLabelLogoTitle;
    @BindView(R.id.twitter_label_logo_subtitle) TextView mLabelLogoSubtitle;
    @BindView(R.id.twitter_image_instruction_bg) ImageView mImageInstructionBg;
    @BindView(R.id.twitter_label_instruction) TextView mLabelInstruction;
    @BindView(R.id.twitter_view_pager) ViewPager mViewPager;

    private TwitterAdapter mAdapter;

    private int mCurrentOption = NONE_PAGE_SELECTED;

    private int mCardActiveColor;
    private int mCardInactiveColor;
    private String mCorrectOptionId;
    private int mTextActiveColor;
    private int mTextInactiveColor;

    private Unbinder mUnbinder;

    public TwitterFragment() {
    }

    /**
     * Constructs new instance of TwitterFragment.
     */
    public static TwitterFragment newInstance(Practice practice, boolean isSuccessful) {
        Bundle args = new Bundle();
        args.putParcelable(Practice.ARG_DATA, Parcels.wrap(practice));
        args.putBoolean(BundleKey.SUCCESS, isSuccessful);

        TwitterFragment fragment = new TwitterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPracticeData = Parcels.unwrap(arguments.getParcelable(Practice.ARG_DATA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        initUi();
        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.practice_twitter_fragment;
    }

    @Override
    protected int getBackgroundColor() {
        return mPracticeData.getTwitterPracticeDetails().getColors().getLessonBackgroundColor();
    }

    private void initUi() {
        TwitterPracticeDetails details = mPracticeData.getTwitterPracticeDetails();
        mCorrectOptionId = details.getCorrectOption();
        mCardActiveColor = details.getColors().getCardActiveColor();
        mCardInactiveColor = details.getColors().getCardInactiveColor();
        mTextActiveColor = details.getColors().getTextActiveColor();
        mTextInactiveColor = details.getColors().getTextInactiveColor();

        initHeader(mLabelQuestion, details.getQuestion());

        ImagesBaseModel imageModel = details.getInfo().getImages();
        // image model can be null -> nothing to display in mImageLogo
        if (imageModel != null) {
            Glide.with(getActivity())
                    .load(ImageUrlHelper.getUrlFor(getContext(), imageModel))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .dontAnimate()
                    .into(mImageLogo);
        }

        mLabelLogoTitle.setText(details.getInfo().getName());
        mLabelLogoSubtitle.setText(details.getInfo().getHandle());

        GradientDrawable borderRect = (GradientDrawable) mImageInstructionBg.getDrawable();
        borderRect.setStroke(
                ViewUtils.dp2px(getContext(), STROKE_WIDTH_DP),
                details.getColors().getCardActiveColor(),
                ViewUtils.dp2px(getContext(), STROKE_DASH_WIDTH_DP),
                ViewUtils.dp2px(getContext(), STROKE_DASH_GAP_DP));
        mLabelInstruction.setText(HtmlHelper.fromHtml(details.getQuestion()));

        mAdapter = new TwitterAdapter(details.getOptions());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TwitterOnPageChangeListener());
        mViewPager.setPageTransformer(false, new TwitterPageTransformer());
        if (ViewUtils.isTablet(getContext())) {
            mViewPager.post(() -> {
                int maxContentWidth = (int) getResources().getDimension(R.dimen.twitter_page_width);
                int paddingHorizontal = (int) ((mViewPager.getMeasuredWidth() - maxContentWidth) / 2f);
                mViewPager.setPadding(mViewPager.getPaddingLeft() + paddingHorizontal, mViewPager.getPaddingTop(), mViewPager.getPaddingRight() + paddingHorizontal, mViewPager.getPaddingBottom());
                mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.small_margin));
                mViewPager.invalidate();
            });
        }
    }

    @Override
    public void onNextClick() {
        onButtonSubmitClick();
    }

    @Override
    public void onPrevClick() {
        mNavCallback.goBack();
    }

    private void onButtonSubmitClick() {
        if (verifyAnswers()) {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_CORRECT);
        } else {
            mNavCallback.goToStepRqst(PracticeNavCallback.Step.RESULT_INCORRECT);
        }
    }

    private boolean verifyAnswers() {
        return mCorrectOptionId.equals(mAdapter.getOptionId(mCurrentOption));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    @Override
    protected void recreateAnswers() {
        mViewPager.setCurrentItem(getCorrectAnswerIndex() + 1);
    }

    private int getCorrectAnswerIndex() {
        for (int i = 0; i < mPracticeData.getTwitterPracticeDetails().getOptions().size(); i++) {
            if (mPracticeData.getTwitterPracticeDetails().getOptions().get(i).getId().equals(mPracticeData.getTwitterPracticeDetails().getCorrectOption())) {
                return i;
            }
        }
        return 0;
    }

    private class TwitterPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int paddingLeft = mViewPager.getPaddingLeft();

            int pageWidth = page.getWidth();
            float positionInParent = position * page.getWidth();

            final float normalizedPosition = Math.abs((positionInParent - paddingLeft) / pageWidth);

            CardView cardView = (CardView) page.findViewById(R.id.twitter_card_view);
            TextView textView = (TextView) page.findViewById(R.id.twitter_card_text);

            if (normalizedPosition <= 1) {
                ArgbEvaluator colorEvaluator = new ArgbEvaluator();
                cardView.setCardBackgroundColor(
                        (Integer) colorEvaluator.evaluate(normalizedPosition, mCardActiveColor, mCardInactiveColor));

                if (normalizedPosition > -1 && normalizedPosition < 0.2) {
                    textView.setTextColor(mTextActiveColor);
                } else {
                    textView.setTextColor(mTextInactiveColor);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(0);
                }
            } else {
                cardView.setCardBackgroundColor(mCardInactiveColor);
                textView.setTextColor(mTextInactiveColor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(ViewUtils.dp2px(getContext(), CARD_ELEVATION_DP));
                }
            }
        }
    }

    private class TwitterOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentOption = position;
            if (position > NONE_PAGE_SELECTED) {
                enableNavBtnSubmit();
            } else {
                disableNavBtnSubmit();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
