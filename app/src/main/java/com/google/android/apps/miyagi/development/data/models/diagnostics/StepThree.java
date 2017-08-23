package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepThree {

    @SerializedName("no_business_goals")
    protected StepThreeGoals mNoBusinessGoals;

    @SerializedName("pre_business_goals")
    protected StepThreeGoals mPreBusinessGoals;

    @SerializedName("in_business_goals")
    protected StepThreeGoals mInBusinessGoals;

    public StepThreeGoals getNoBusinessGoals() {
        return mNoBusinessGoals;
    }

    public StepThreeGoals getPreBusinessGoals() {
        return mPreBusinessGoals;
    }

    public StepThreeGoals getInBusinessGoals() {
        return mInBusinessGoals;
    }
}
