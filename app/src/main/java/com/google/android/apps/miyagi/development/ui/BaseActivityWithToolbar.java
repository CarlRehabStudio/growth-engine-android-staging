package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.google.android.apps.miyagi.development.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivityWithToolbar extends BaseActivity {

    @BindView(R.id.base_toolbar) protected Toolbar mToolbar;
    @BindView(R.id.base_content) protected FrameLayout mContentLayout;

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(R.layout.child_base_activity);
        ButterKnife.bind(this);

        getLayoutInflater().inflate(layoutResId, mContentLayout);

        setupToolbar();
    }

    protected void setToolbarColor(int color) {
        mToolbar.setBackgroundColor(color);

        // for API >= LOLLIPOP set the same color for status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    protected void addToolbarCloseIcon() {
        mToolbar.setNavigationIcon(R.drawable.ic_close);
    }

    private void addUpNavigationToActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        addUpNavigationToActionBar();
    }

    @Override
    public void injectSelf(Context context) {
        // empty
    }
}
