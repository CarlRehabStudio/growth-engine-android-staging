package com.google.android.apps.miyagi.development.ui.statistics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.statistics.Page;
import com.google.android.apps.miyagi.development.data.models.statistics.PagesWrapper;
import com.google.android.apps.miyagi.development.data.models.statistics.Statistics;
import com.google.android.apps.miyagi.development.data.models.statistics.StatisticsPageType;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.components.widget.InkPageIndicator;
import com.google.android.apps.miyagi.development.ui.statistics.items.BadgeAdapter;
import com.google.android.apps.miyagi.development.ui.statistics.items.StatisticsPager;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.parceler.Parcels;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import java.lang.reflect.Field;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 28.11.2016.
 */

public class StatisticsActivity extends BaseActivity {

    private static final int VIEW_PAGER_FLING_DISTANCE = 15;
    private static final int COLUMNS_NUMBER = 3;
    private static final long ADAPTER_UPDATE_DELAY = 500;

    @Inject AppColors mAppColors;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.statistics_pager) ViewPager mViewPager;
    @BindView(R.id.statistics_pager_indicator) InkPageIndicator mPageIndicator;
    @BindView(R.id.statistics_badge_title) TextView mBadgeTitle;
    @BindView(R.id.statistics_recycler) RecyclerView mBadgeRecycler;

    private StatisticsPager mPager;

    private BadgeAdapter mBadgeAdapter;

    private boolean mInitialAnimation;

    /**
     * Creates new StatisticsActivity.
     */
    public static Intent createIntent(Context context, Statistics statisticsData) {
        Intent intent = new Intent(context, StatisticsActivity.class);
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
        Page planPage = pages.getPageFromType(pages.size() == 2 ? StatisticsPageType.PLAN : StatisticsPageType.CERTIFICATION);
        mBadgeTitle.setText(planPage.getSectionHeaderTitle());
        mBadgeTitle.setBackgroundColor(mAppColors.getSectionBackgroundColor());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMNS_NUMBER);
        mBadgeRecycler.setLayoutManager(gridLayoutManager);
        mBadgeRecycler.setNestedScrollingEnabled(false);

        mBadgeAdapter = new BadgeAdapter(StatisticsActivity.this, planPage.getBadges());
        mBadgeRecycler.setAdapter(mBadgeAdapter);

        mPager = new StatisticsPager(this, pages);

        try {
            Field flingDistance;
            flingDistance = ViewPager.class.getDeclaredField("mFlingDistance");
            flingDistance.setAccessible(true);
            final float density = getApplicationContext().getResources().getDisplayMetrics().density;
            // Set custom value:
            flingDistance.set(mViewPager, (int) (VIEW_PAGER_FLING_DISTANCE * density));
        } catch (NoSuchFieldException exception) {
            Lh.e(this, "ViewPager api has changed.");
        } catch (IllegalAccessException exception) {
            Lh.e(this, "Can't change ViewPager mFlingDistance.");
        }

        mViewPager.setAdapter(mPager);
        if (pages.size() > 1) {
            mPageIndicator.setViewPager(mViewPager);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!mInitialAnimation) {
                    mPager.animateProgressBar(position);
                    mInitialAnimation = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Page newPage = pages.getPageFromType(StatisticsPageType.getTypeFromPosition(position));
                mBadgeTitle.setText(newPage.getSectionHeaderTitle());
                mPager.animateProgressBar(position);

                Observable.timer(ADAPTER_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            mBadgeAdapter.updateItemsList(newPage.getBadges());
                            mBadgeAdapter.notifyDataSetChanged();
                        });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
