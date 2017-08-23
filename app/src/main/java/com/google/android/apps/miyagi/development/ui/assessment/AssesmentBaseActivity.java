package com.google.android.apps.miyagi.development.ui.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;

import butterknife.BindView;

/**
 * Created by lukaszweglinski on 14.03.2017.
 */

public class AssesmentBaseActivity extends BaseActivity {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;

    @Override
    public void injectSelf(Context context) {

    }

    protected void bindActionBar(String title, int color, boolean addCloseIcon) {
        setupActionBar(true, title);
        setToolbarColor(color);
        if (addCloseIcon) {
            setToolbarCloseIcon();
        }
    }

    private void setupActionBar(boolean showHomeButton, String title) {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(showHomeButton);
        supportActionBar.setDisplayShowTitleEnabled(true);
        supportActionBar.setTitle(title);
    }

    private void setToolbarColor(int color) {
        mToolbar.setBackgroundColor(color);

        // for API >= LOLLIPOP set the same color for status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    private void setToolbarCloseIcon() {
        mToolbar.setNavigationIcon(R.drawable.ic_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intentToLaunch = DashboardActivity.createIntent(this, false);
        intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentToLaunch);
    }

}
