package com.google.android.apps.miyagi.development.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.BaseListActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.items.LicencesAdapter;
import com.google.android.apps.miyagi.development.ui.navigation.Navigator;

import dagger.Lazy;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Paweł on 2017-02-18.
 */

public class LicencesListActivity extends BaseListActivity {

    @Inject Lazy<Navigator> mNavigator;

    /**
     * Create LicencesListActivity intent.
     *
     * @param context the context.
     * @param title   the title.
     * @return the  LicencesListActivity intent.
     */
    public static Intent createIntent(Context context, String title) {
        Intent intent = new Intent(context, LicencesListActivity.class);
        intent.putExtra(ArgsKey.TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) this.getApplication()).getAppComponent().inject(this);
        mToolbar.setTitle(getIntent().getStringExtra(ArgsKey.TITLE));
        loadLicencesData();
    }

    @Override
    public void injectSelf(Context context) {
        // empty
    }

    private void loadLicencesData() {
        final String[] licenceTitles = getResources().getStringArray(R.array.licence_titles);
        final int[] licenceTexts = getResources().getIntArray(R.array.licence_texts);
        List<Pair<String, Integer>> items = new ArrayList<>();
        for (int i = 0; i < licenceTexts.length; i++) {
            items.add(new Pair<>(licenceTitles[i], licenceTexts[i]));
        }
        LicencesAdapter adapter = new LicencesAdapter(this, items);
        adapter.setOnItemSelectedListener(item -> mNavigator.get().navigateToLicence(this, item));
        mRecyclerView.setAdapter(adapter);
    }

    interface ArgsKey {
        String TITLE = "com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicencesListActivity.TITLE";
    }
}
