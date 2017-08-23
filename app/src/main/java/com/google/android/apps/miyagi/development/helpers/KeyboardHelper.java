package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by lukaszweglinski on 09.11.2016.
 */

public class KeyboardHelper {

    /**
     * Helper to hide keyboard on focused view.
     *
     * @param view - focused view.
     */
    public static void hideKeyboard(View view) {
        if (view != null) {
            final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.requestFocus();
        }
    }

    /**
     * Helper to hide keyboard on activity.
     *
     * @param activity - activity with focused view.
     */
    public static void hideKeyboard(FragmentActivity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
