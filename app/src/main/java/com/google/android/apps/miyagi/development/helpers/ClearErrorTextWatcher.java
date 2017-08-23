package com.google.android.apps.miyagi.development.helpers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by marcinarciszew on 29.12.2016.
 */

public class ClearErrorTextWatcher implements TextWatcher {

    private final TextInputLayout mTextInputLayout;

    public ClearErrorTextWatcher(TextInputLayout textInputLayout) {
        mTextInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextInputLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
