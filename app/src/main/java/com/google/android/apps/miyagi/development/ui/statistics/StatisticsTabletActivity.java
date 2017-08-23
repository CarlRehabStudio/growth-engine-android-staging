package com.google.android.apps.miyagi.development.ui.statistics;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.statistics.IBadgeItem;
import com.google.android.apps.miyagi.development.data.models.statistics.Page;
import com.google.android.apps.miyagi.development.data.models.statistics.PagesWrapper;
import com.google.android.apps.miyagi.development.data.models.statistics.Statistics;
import com.google.android.apps.miyagi.development.data.models.statistics.StatisticsPageType;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.ArcProgressBar;
import com.google.android.apps.miyagi.development.ui.statistics.items.BadgeTabletAdapter;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.android.apps.miyagi.development.data.models.statistics.IBadgeItem.TYPE_ITEM;
import static com.google.android.apps.miyagi.development.data.models.statistics.IBadgeItem.TYPE_SECTION;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 28.11.2016.
 */

public class StatisticsTabletActivity extends BaseActivity {

    private static final int COLUMNS_NUMBER = 4;
    private static final long ANIMATION_DURATION = 1500;
    private static final long ANIMATION_DELAY = 400;
    private static final long ANIMATION_BACKGROUND_DELAY = 900;

    @Inject AppColors mAppColors;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.progress_container) LinearLayout mLinearLayout;
    @BindView(R.id.statistics_recycler) RecyclerView mBadgeRecycler;
    @BindView(R.id.section_item) View mSectionView;
    @BindView(R.id.image_expand_collapse) ImageView mSectionExpandIcon;
    @BindView(R.id.label_title) TextView mSectionTitle;
    @BindView(R.id.divider_top) View mSectionDividerTop;

    private BadgeTabletAdapter mBadgeAdapter;

    private ArcProgressBar[] mArcProgressBarsArray;
    private int[] mCurrentBackgroundProgressArray;
    private int[] mCurrentProgressArray;

    /**
     * Creates new StatisticsActivity.
     */
    public static Intent createIntent(Context context, Statistics statisticsData) {
        Intent intent = new Intent(context, StatisticsTabletActivity.class);
        intent.putExtra(Statistics.ARG_KEY, Parcels.wrap(statisticsData));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        ButterKnife.bind(this);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_statistics));

        getStatisticsData();

        mSectionDividerTop.setVisibility(View.GONE);
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getStatisticsData() {
        Statistics statisticsData = Parcels.unwrap(getIntent().getParcelableExtra(Statistics.ARG_KEY));

        Toolbar toolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        toolbar.setTitle(statisticsData.getScreenTitle());

        PagesWrapper pages = statisticsData.getPages();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMNS_NUMBER);
        mBadgeRecycler.setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mBadgeAdapter.getItemViewType(position)) {
                    case TYPE_ITEM:
                        return 1;
                    case TYPE_SECTION:
                    default:
                        return COLUMNS_NUMBER;
                }
            }
        });

        List<IBadgeItem> items = new ArrayList<>();
        Page planPage = pages.getPageFromType(StatisticsPageType.PLAN);
        if (planPage != null && planPage.getBadges().size() > 0) {
            mSectionTitle.setText(planPage.getSectionHeaderTitle());
            mSectionView.setBackgroundColor(mAppColors.getSectionBackgroundColor());
            items.addAll(planPage.getBadges());
        }

        Page certificatePage = pages.getPageFromType(StatisticsPageType.CERTIFICATION);
        if (certificatePage != null && certificatePage.getBadges().size() > 0) {
            mSectionTitle.setText(certificatePage.getSectionHeaderTitle());
            mSectionView.setBackgroundColor(mAppColors.getSectionBackgroundColor());
            items.addAll(certificatePage.getBadges());
        }

        mBadgeAdapter = new BadgeTabletAdapter(this, items);
        mBadgeRecycler.setAdapter(mBadgeAdapter);

        mArcProgressBarsArray = new ArcProgressBar[pages.size()];
        mCurrentProgressArray = new int[pages.size()];
        mCurrentBackgroundProgressArray = new int[pages.size()];

        LayoutInflater inflater = LayoutInflater.from(this);
        if (pages.size() >= 1) {
            addProgressView(0, pages.get(0), inflater);
            animateProgressBar(0, pages.get(0));
            mLinearLayout.setBackgroundColor(pages.get(0).getBackgroundColor());
        }
        if (pages.size() >= 2) {
            addProgressView(1, pages.get(1), inflater);
            animateProgressBar(1, pages.get(1));
        }
    }

    private void addProgressView(int position, Page page, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.statistics_pager_item, mLinearLayout, false);

        view.setBackgroundColor(page.getBackgroundColor());

        ArcProgressBar progressBar = (ArcProgressBar) view.findViewById(R.id.statistics_progress_bar);
        progressBar.setBottomText(page.getProgressTitle());

        progressBar.setForegroundStrokeColor(page.getSpeedometerColorFull());
        progressBar.setBackgroundStrokeColor(page.getSpeedometerColorEmpty());

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (ViewUtils.isLandscape(this)) {
            layoutParams.height = 0;
            layoutParams.weight = 1;
        } else {
            layoutParams.width = 0;
            layoutParams.weight = 1;
        }
        view.setLayoutParams(layoutParams);
        mLinearLayout.addView(view);

        mArcProgressBarsArray[position] = progressBar;
    }

    public void animateProgressBar(int pageNumber, Page page) {
        ArcProgressBar progressBar = mArcProgressBarsArray[pageNumber];

        ValueAnimator progressAnimation = ObjectAnimator.ofInt(0, (int) progressBar.getFinalArcAngle());
        progressAnimation.setInterpolator(new FastOutSlowInInterpolator());
        progressAnimation.setDuration(ANIMATION_DURATION);
        progressAnimation.setStartDelay(ANIMATION_DELAY);
        progressAnimation.addUpdateListener(animation -> {
            mCurrentBackgroundProgressArray[pageNumber] = (int) animation.getAnimatedValue();
            progressBar.setArcAngle(mCurrentBackgroundProgressArray[pageNumber]);
            progressBar.invalidate();
        });
        progressAnimation.start();

        int progress = page.getProgress();

        ValueAnimator progressBackgroundAnimation = ObjectAnimator.ofInt(0, progress);
        progressBackgroundAnimation.setInterpolator(new FastOutSlowInInterpolator());
        progressBackgroundAnimation.setDuration(ANIMATION_DURATION);
        progressBackgroundAnimation.setStartDelay(ANIMATION_BACKGROUND_DELAY);
        progressBackgroundAnimation.addUpdateListener(animation -> {
            mCurrentProgressArray[pageNumber] = (int) animation.getAnimatedValue();
            progressBar.setProgress(mCurrentProgressArray[pageNumber]);
            progressBar.invalidate();
        });
        progressBackgroundAnimation.start();
    }
}
