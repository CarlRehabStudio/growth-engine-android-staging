package com.google.android.apps.miyagi.development.helpers;

/**
 * Created by Paweł on 2017-04-27.
 */


import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Movement method to handle TextView links with exception catching (for os that cannot handle
 * ACTION_VIEW intents.
 */
public class SafeLinkMovementMethod extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        try {
            return super.onTouchEvent(widget, buffer, event);
        } catch (Exception e) {
            return true;
        }
    }

}