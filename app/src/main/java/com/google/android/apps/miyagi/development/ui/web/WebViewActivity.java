package com.google.android.apps.miyagi.development.ui.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class WebViewActivity extends BaseActivity {

    @Inject ConfigStorage mConfigStorage;
    private WebView mWebView;
    private Map<String, String> mQueryParams;

    /**
     * @param context     - context.
     * @param url         - web page to show.
     * @param appBarTitle - title on toolbar.
     * @return - intent to call this activity with arguments.
     */
    public static Intent createIntent(Context context, String url, String appBarTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ArgsKeys.WEB_URL, url);
        intent.putExtra(ArgsKeys.APP_BAR_TITLE, appBarTitle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        Toolbar toolbar = ToolbarHelper.setUpChildActivityToolbar(this);
        toolbar.setTitle(getIntent().getStringExtra(ArgsKeys.APP_BAR_TITLE));
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = buildURI(url, mQueryParams);

                if (isBaseUrl(Uri.parse(url))) {
                    mWebView.loadUrl(uri.toString());
                } else {
                    startExternalWebActivity(uri);
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = buildURI(request.getUrl().toString(), mQueryParams);

                if (isBaseUrl(uri)) {
                    mWebView.loadUrl(uri.toString());
                } else {
                    startExternalWebActivity(uri);
                }
                return true;
            }
        });

        Uri url = Uri.parse(getIntent().getStringExtra(ArgsKeys.WEB_URL));
        mQueryParams = getQueryParameter(url);

        if (isBaseUrl(url)) {
            mWebView.loadUrl(url.toString());
        } else {
            startExternalWebActivity(url);
        }
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

    private Uri buildURI(String url, Map<String, String> params) {
        // build url with parameters.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /**
     * Return a map of argument->value from a query in a URI
     *
     * @param uri The URI
     */
    private Map<String, String> getQueryParameter(Uri uri) {
        if (uri.isOpaque()) {
            return Collections.emptyMap();
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> parameters = new LinkedHashMap<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            String value;
            if (separator < end)
                value = query.substring(separator + 1, end);
            else
                value = "";

            parameters.put(Uri.decode(name), Uri.decode(value));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableMap(parameters);
    }

    private boolean isBaseUrl(Uri url) {
        return mConfigStorage.getSelectedMarket().getEndpointUrl().contains(url.getHost());
    }

    private void startExternalWebActivity(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) this.getApplication()).getAppComponent().inject(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    interface ArgsKeys {
        String WEB_URL = "com.google.android.apps.miyagi.development.ui.web.WEB_URL";
        String APP_BAR_TITLE = "com.google.android.apps.miyagi.development.ui.web.APP_BAR_TITLE";
    }
}
