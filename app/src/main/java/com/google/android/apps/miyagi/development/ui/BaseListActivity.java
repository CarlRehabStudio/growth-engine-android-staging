package com.google.android.apps.miyagi.development.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;


/**
 * Created by Paweł on 2017-02-18.
 */

public abstract class BaseListActivity extends BaseActivity {

    protected Toolbar mToolbar;
    protected RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        initRecycler();
    }

    protected void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FlexibleItemDecoration divider = new FlexibleItemDecoration(getApplicationContext());
        divider.withDrawOver(true);
        mRecyclerView.addItemDecoration(divider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
