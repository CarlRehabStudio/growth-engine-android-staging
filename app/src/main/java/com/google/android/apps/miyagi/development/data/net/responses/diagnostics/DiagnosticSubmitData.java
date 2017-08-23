package com.google.android.apps.miyagi.development.data.net.responses.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 22.12.2016.
 */

@Parcel
public class DiagnosticSubmitData {

    @SerializedName("persona")
    protected String mPersona;

    @SerializedName("plan")
    protected List<Integer> mPlans;

    @SerializedName("xsrf_token")
    protected String mXsrfToken;

    DiagnosticSubmitData() {
    }

    /**
     * Instantiates a new Diagnostic submit data.
     *
     * @param persona the persona.
     * @param plans  the plans.
     * @param xsrf   the mXsrfToken token.
     */
    public DiagnosticSubmitData(String persona, List<Integer> plans, String xsrf) {
        mPersona = persona;
        mPlans = plans;
        mXsrfToken = xsrf;
    }
}
