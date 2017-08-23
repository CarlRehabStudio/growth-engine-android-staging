package com.google.android.apps.miyagi.development.ui.offline;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioMetaData;
import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerBaseActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.Mode;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;
import com.google.android.apps.miyagi.development.ui.offline.adapter.OfflineDashboardAdapter;
import com.google.android.apps.miyagi.development.utils.NetworkUtils;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 07.02.2017.
 */

public class OfflineDashboardActivity extends BaseActivity implements OfflineAdapterAction.Callback {

    @Inject AudioMetaDataDatabase mAudioMetaDataDatabase;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject AppColors mAppColors;
    @Inject ConfigStorage mConfigStorage;
    @Inject Navigator mNavigator;

    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.offline_dashboard_recycler) RecyclerView mRecyclerView;

    private PreloaderHelper mLoaderScreenHelper;
    private ErrorScreenHelper mErrorScreenHelper;
    private PopupMenu mPopupMenu;

    private OfflineDashboardAdapter mOfflineDashboardAdapter;
    private Subscription mDataSubscription;
    private List<AudioMetaData> mAudioMetaDataResponse;
    private MenuItem mMenuItem;

    /**
     * Creates calling intent for OfflineDashboardActivity.
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, OfflineDashboardActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.offline_dashboard_activity);
        ButterKnife.bind(this);

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mToolbar.setNavigationIcon(R.drawable.ic_close_black);

        mLoaderScreenHelper = new PreloaderHelper(findViewById(R.id.loader_screen));
        mErrorScreenHelper = new ErrorScreenHelper(findViewById(R.id.error_screen));
        mErrorScreenHelper.setOnActionClickListener(this::loadData);
        mErrorScreenHelper.showNavigationButton(false);

        initUi();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void initUi() {
        mToolbar.setTitle(mConfigStorage.getCommonData().getCopy().getOfflineHeader());
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        FlexibleItemDecoration divider = new FlexibleItemDecoration(getApplicationContext());
        divider.withDrawOver(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(divider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.offline_dashboard, menu);
        mMenuItem = menu.getItem(0);
        if (mConfigStorage.getCommonData() != null) {
            mMenuItem.setTitle(mConfigStorage.getCommonData().getCopy().getOfflineInfo());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_info:
                showInstructionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showInstructionDialog() {
        if (mConfigStorage.getCommonData() != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage(mConfigStorage.getCommonData().getCopy().getOfflineInfo())
                    .setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void loadData() {
        SubscriptionHelper.unsubscribe(mDataSubscription);
        mDataSubscription = mAudioMetaDataDatabase.getSavedTopics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onDataReceived, this::onDataError);
    }

    private void onDataReceived(List<AudioMetaData> audioMetaDatas) {
        mAudioMetaDataResponse = audioMetaDatas;
        setAdapter(audioMetaDatas);
    }

    private void setAdapter(List<AudioMetaData> audioMetaDatas) {
        mOfflineDashboardAdapter = OfflineDashboardAdapter.create(audioMetaDatas, mAppColors.getMainCtaColor());
        mOfflineDashboardAdapter.setOfflineAdapterCallback(this);
        mRecyclerView.setAdapter(mOfflineDashboardAdapter);
        setErrorIfAdapterEmpty();
    }

    private void onDataError(Throwable throwable) {
        mErrorScreenHelper.setErrorForLoggedIn(throwable);
        mErrorScreenHelper.show();
    }

    private void onSubscribe() {
        mErrorScreenHelper.hide();
        mLoaderScreenHelper.show();
    }

    private void onTerminate() {
        mLoaderScreenHelper.hide();
    }

    @Override
    public void onTopicClick(int topicId) {
        mCurrentSessionCache.setTopicId(topicId);
        startActivity(AudioPlayerBaseActivity.getCallingIntent(this, Mode.OFFLINE));
    }

    @Override
    public void onMenuClick(View view, int topicId, int position) {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
        mPopupMenu = new PopupMenu(view.getContext(), view);
        mPopupMenu.inflate(R.menu.offline_dashboard_topic_context);

        MenuItem menuPlayAudio = mPopupMenu.getMenu().findItem(R.id.action_play_audio);
        MenuItem menuFileAction = mPopupMenu.getMenu().findItem(R.id.action_download_eject);

        AudioResponseData audioResponseData = mAudioMetaDataResponse.get(0).getAudioResponseData();
        menuPlayAudio.setTitle(audioResponseData.getPlayAudioLabel());
        menuFileAction.setTitle(audioResponseData.getDeleteAudioLabel());

        mPopupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_play_audio:
                    mCurrentSessionCache.setTopicId(topicId);
                    startActivity(AudioPlayerBaseActivity.getCallingIntent(this, Mode.OFFLINE));
                    return true;
                case R.id.action_download_eject:
                    mOfflineDashboardAdapter.onFileActionClick(position, topicId);
                    return true;
                default:
                    break;
            }
            return false;
        });
        mPopupMenu.show();
    }

    @Override
    public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        mConfigStorage.saveShouldUpdateDashboard(true);
        if (audioDownloadStatus == AudioDownloadStatus.DELETED) {
            mAudioMetaDataResponse.remove(position);
            showInfoSnackbar(mCoordinatorLayout, mConfigStorage.getCommonData().getCopy().getFileDeletingMessage());
        }
        setErrorIfAdapterEmpty();
    }

    private void setErrorIfAdapterEmpty() {
        if (mOfflineDashboardAdapter.getItemCount() == 0) {
            mErrorScreenHelper.setNetworkError();
            mErrorScreenHelper.setOnActionClickListener(this::onBackPressed);
            mErrorScreenHelper.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mDataSubscription);
        mDataSubscription = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
        initView();
        if (mAudioMetaDataResponse != null) {
            setAdapter(mAudioMetaDataResponse);
        } else {
            loadData();
        }
    }

    @Override
    public void onBackPressed() {
        if (NetworkUtils.isOnline(this)) {
            mConfigStorage.saveShouldUpdateDashboard(true);
        }
        mNavigator.navigateToDashboard(this);
    }
}
