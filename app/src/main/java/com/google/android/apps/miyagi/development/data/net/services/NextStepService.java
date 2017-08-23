package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;

import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public interface NextStepService extends BaseService {

    Observable<NextStepResponse> getNextStepData();
}
