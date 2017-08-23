package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponse;

import rx.Observable;

/**
 * Created by marcin on 22.12.2016.
 */

public interface LabelsService extends BaseService {

    Observable<LabelsResponse> getLabels();
}
