package com.google.android.apps.miyagi.development.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.dashboard.LicenceType;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Paweł on 2017-02-18.
 */

public class LicenceActivity extends BaseActivity {

    protected Toolbar mToolbar;
    @BindView(R.id.licence_text) TextView mLicenceText;

    /**
     * Creates calling intent for this activity.
     */
    public static Intent createIntent(Context context, Pair<String, Integer> licence) {
        Intent intent = new Intent(context, LicenceActivity.class);
        intent.putExtra(ArgsKey.TITLE, licence.first);
        intent.putExtra(ArgsKey.TYPE, licence.second);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licence_activity);
        ButterKnife.bind(this);

        mToolbar = ToolbarHelper.setUpChildActivityToolbar(this);

        String title = getIntent().getStringExtra(ArgsKey.TITLE);
        int licenceType = getIntent().getIntExtra(ArgsKey.TYPE, LicenceType.APACHE2.getType());

        String licenceText = readLicence(LicenceType.fromType(licenceType).getLicenceId());

        mToolbar.setTitle(title);
        mLicenceText.setText(licenceText);
    }

    @Override
    public void injectSelf(Context context) {
        // empty
    }

    private String readLicence(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            int input = inputStream.read();
            while (input != -1) {
                byteArrayOutputStream.write(input);
                input = inputStream.read();
            }
            inputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return byteArrayOutputStream.toString();
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

    interface ArgsKey {
        String TITLE = "com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicenceActivity.TITLE";
        String TYPE = "com.google.android.apps.miyagi.development.ui.dashboard.navigation.LicenceActivity.TYPE";
    }
}
