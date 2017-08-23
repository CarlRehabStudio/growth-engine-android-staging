package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepOne;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepOneOptions;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;
import com.google.android.apps.miyagi.development.utils.SpaceItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcin on 08.02.2017.
 */

public class SelectPersonaFragment extends Fragment {

    @BindView(R.id.diagnostics_step_two_header) TextView mLabelHeader;
    @BindView(R.id.diagnostics_step_three_sub_header) TextView mLabelSubHeader;
    @BindView(R.id.diagnostics_step_three_recycler) RecyclerView mDiagnosticsRecycler;

    @Inject AnalyticsHelper mAnalyticsHelper;

    private SelectPersonaAdapter mAdapter;
    private StepOne mStepData;
    private OnItemSelectedListener<StepOneOptions> mOnSelectionChangeListener;
    private Unbinder mUnbinder;
    private int mSelectedPosition = -1;

    public SelectPersonaFragment() {

    }

    /**
     * New instance of SelectPersonaFragment fragment.
     */
    public static SelectPersonaFragment newInstance(DiagnosticsResponseData responseData) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKey.STEP_DATA, Parcels.wrap(responseData.getStepOne()));

        SelectPersonaFragment fragment = new SelectPersonaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
        Bundle args = getArguments();
        mStepData = Parcels.unwrap(args.getParcelable(BundleKey.STEP_DATA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diagnostics_select_persona_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_create_plan_persona));

        if (savedInstanceState != null) {
            int savedPosition = savedInstanceState.getInt(BundleKey.SELECTED_POSITION, -1);
            if (savedPosition >= 0) {
                mSelectedPosition = savedPosition;
            }
        }

        initUi();
        initRecycler();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mSelectedPosition >= 0) {
            outState.putInt(BundleKey.SELECTED_POSITION, mSelectedPosition);
        }
    }

    void initUi() {
        mLabelHeader.setText(mStepData.getHeaderText());
        mLabelSubHeader.setText(mStepData.getSubHeaderText());
    }

    void initRecycler() {
        List<SelectPersonaItem> items = new ArrayList<>();
        for (int i = 0; i < mStepData.getOptions().size(); i++) {
            SelectPersonaItem item = new SelectPersonaItem(mStepData.getOptions().get(i));
            if (mSelectedPosition >= 0) {
                if (i == mSelectedPosition) {
                    item.setState(true);
                } else {
                    item.setState(false);
                }
            }
            items.add(item);
        }

        mAdapter = new SelectPersonaAdapter(getContext(), items, this::onItemSelected);

        RecyclerView.LayoutManager layoutManager;
        if (ViewUtils.isLandTablet(getContext())) {
            layoutManager = new GridLayoutManager(getContext(), 3);
            mDiagnosticsRecycler.addItemDecoration(
                    new SpaceItemDecoration(
                            0,
                            (int) getResources().getDimension(R.dimen.padding_22),
                            false,
                            (int) getResources().getDimension(R.dimen.small_margin)));
        } else {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        }

        mDiagnosticsRecycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = mDiagnosticsRecycler.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mDiagnosticsRecycler.setAdapter(mAdapter);
        if (mSelectedPosition >= 0) {
            mDiagnosticsRecycler.smoothScrollToPosition(mSelectedPosition);
        }
    }

    private void onItemSelected(SelectPersonaItem selectPersonaItem) {
        int position = mAdapter.getItems().indexOf(selectPersonaItem);
        mSelectedPosition = position;
        mAnalyticsHelper.trackEvent(getString(R.string.screen_create_plan_persona), getString(R.string.event_category_diagnostics), getString(R.string.event_action_select_option), mStepData.getOptions().get(position).getId());
        StepOneOptions selectedOptions = selectPersonaItem.getStepOneOptions();

        if (mOnSelectionChangeListener != null) {
            mOnSelectionChangeListener.onItemSelected(selectedOptions);
        }

        for (SelectPersonaItem item : mAdapter.getItems()) {
            item.setState(false);
        }
        selectPersonaItem.setState(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    public void setOnSelectionChangeListener(OnItemSelectedListener<StepOneOptions> onSelectionChangeListener) {
        mOnSelectionChangeListener = onSelectionChangeListener;
    }

    interface BundleKey {
        String SELECTED_POSITION = "SELECTED_POSITION";
        String STEP_DATA = "STEP_DATA";
    }
}
