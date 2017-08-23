package com.google.android.apps.miyagi.development.ui.audio.transcript;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.audio.transcript.items.AudioTranscriptListAdapter;
import com.google.android.apps.miyagi.development.ui.audio.transcript.items.AudioTranscriptListHeaderItem;
import com.google.android.apps.miyagi.development.ui.audio.transcript.items.AudioTranscriptListItem;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcin on 21.01.2017.
 */

public class AudioTranscriptActivity extends BaseActivity {

    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject ConfigStorage mConfigStorage;
    @Inject Navigator mNavigator;

    @BindView(R.id.audio_transcript_container) CoordinatorLayout mRootContainer;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private Toolbar mToolbar;
    private AudioResponseData mResponseData;

    private int mExpandedItemIndex;
    private AudioTranscriptListAdapter mListAdapter;
    private int mCurrLessonId;

    /**
     * Creates new instance of AudioTranscriptActivity.
     */
    public static Intent getCallingIntent(Context context, int currLessonId) {
        Intent intent = new Intent(context, AudioTranscriptActivity.class);
        intent.putExtra(ExtraKey.CURRENT_LESSON_ID, currLessonId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_transcript_activity);

        ButterKnife.bind(this);

        mCurrLessonId = getIntent().getIntExtra(ExtraKey.CURRENT_LESSON_ID, 0);

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mToolbar.setNavigationIcon(R.drawable.ic_close_black);

        initializeRecyclerView();
        bindData();
    }

    private void initializeRecyclerView() {
        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        FlexibleItemDecoration divider = new FlexibleItemDecoration(getApplicationContext());
        divider.withDrawOver(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(divider);
    }

    private void bindData() {
        mResponseData = mCurrentSessionCache.getAudioResponseData();
        if (mResponseData == null) {
            mNavigator.navigateToDashboard(this);
            return;
        }

        List<AudioLesson> audioLessons = mResponseData.getLessons();
        updateExpandedItemPosition(audioLessons);

        mToolbar.setTitle(mResponseData.getTitle());

        int sectionBgColor = mConfigStorage.getCommonData().getColors().getSectionBackgroundColor();
        List<AbstractFlexibleItem> items = new ArrayList<>();
        for (AudioLesson audioLesson : audioLessons) {
            AudioTranscriptListHeaderItem headerItem
                    = new AudioTranscriptListHeaderItem(audioLesson.getTitle(), sectionBgColor);

            AudioTranscriptListItem item = new AudioTranscriptListItem(audioLesson);
            item.setHeader(headerItem);
            headerItem.addSubItem(item);

            items.add(headerItem);
        }

        if (mListAdapter == null) {
            createAdapter(items);
        }
    }

    private void updateExpandedItemPosition(List<AudioLesson> lessons) {
        for (int i = 0; i < lessons.size(); i++) {
            if (mCurrLessonId == lessons.get(i).getLessonId()) {
                mExpandedItemIndex = i;
                break;
            }
        }
    }

    private void createAdapter(List<AbstractFlexibleItem> items) {
        mListAdapter = new AudioTranscriptListAdapter(items, null);
        mListAdapter
                .setStickyHeaders(true)
                .setAutoCollapseOnExpand(false)
                .setAutoScrollOnExpand(true);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.expand(mExpandedItemIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private interface ExtraKey {
        String CURRENT_LESSON_ID = "CURRENT_LESSON_ID";
    }
}
