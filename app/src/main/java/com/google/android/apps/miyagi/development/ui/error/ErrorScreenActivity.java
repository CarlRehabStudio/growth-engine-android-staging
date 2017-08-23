package com.google.android.apps.miyagi.development.ui.error;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.apps.miyagi.development.R;

/**
 * An error screen that offers to check your network connection and try again.
 */
public class ErrorScreenActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, ErrorScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_fullscreen_error);
    }

}
