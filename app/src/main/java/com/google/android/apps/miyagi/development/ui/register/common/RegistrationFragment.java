package com.google.android.apps.miyagi.development.ui.register.common;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.ui.BaseFragment;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by jerzyw on 10.10.2016.
 */

public abstract class RegistrationFragment<T extends BasePresenter> extends BaseFragment<T> {

    protected NavigationCallback mNavigationCallback = NavigationCallback.NULL;
    private boolean mIsDestroyed;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        boolean shouldNotAnimate = enter && mIsDestroyed;
        mIsDestroyed = false;
        return shouldNotAnimate ? AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in)
                : super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsDestroyed = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mNavigationCallback = (NavigationCallback) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement NavigationCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNavigationCallback = NavigationCallback.NULL;
    }

    public void onNextButtonClicked() {
        Lh.e(this, "No action on next button clicked.");
    }

    protected void showToast(String message) {
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        KeyboardHelper.hideKeyboard(getActivity());
        super.onStop();
    }

    public class DoneClicked implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onNextButtonClicked();
            }
            return false;
        }
    }
}
