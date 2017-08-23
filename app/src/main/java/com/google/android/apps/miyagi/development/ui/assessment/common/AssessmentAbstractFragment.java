package com.google.android.apps.miyagi.development.ui.assessment.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import com.google.android.apps.miyagi.development.ui.assessment.navigation.Navigation;

import butterknife.ButterKnife;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public class AssessmentAbstractFragment extends Fragment {

    protected Navigation mNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Navigation) {
            mNavigation = (Navigation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Navigation");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNavigation = Navigation.EMPTY;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
