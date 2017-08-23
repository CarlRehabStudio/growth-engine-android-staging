package com.google.android.apps.miyagi.development.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.Legal;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BaseListActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.LegalMenuAdapter;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 05.01.2017.
 */

public class LegalMenuActivity extends BaseListActivity {

    @Inject ConfigStorage mConfigStorage;
    @Inject Navigator mNavigator;

    private Legal mLegal;

    /**
     * Creates new instance of LegalMenuActivity.
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, LegalMenuActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLegalData();
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    private void loadLegalData() {
        mLegal = mConfigStorage.getCommonData().getMenu().getLegal();
        mToolbar.setTitle(mLegal.getPage().getTitle());

        LegalMenuAdapter adapter = new LegalMenuAdapter(this);
        adapter.setData(mLegal.getPage());
        adapter.setOnItemSelectedListener(item -> {
            // TODO: remove null check when response from api will be corrected (now licence link is null and privacy link is empty)
            switch (item.getType()) {
                case URL:
                    if (!TextUtils.isEmpty(item.getUrl())) {
                        mNavigator.navigateToBrowser(LegalMenuActivity.this, item.getUrl());
                    }
                    break;
                case LICENCE:
                    mNavigator.navigateToLicencesList(this, item.getTitle());
                    break;
                default:
                    break;
            }
        });

        mRecyclerView.setAdapter(adapter);
    }
}
