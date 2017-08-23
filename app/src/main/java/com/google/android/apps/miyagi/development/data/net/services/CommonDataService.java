package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;

import rx.Observable;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public interface CommonDataService extends BaseService {

    Observable<CommonDataResponse> getCommonData();
}
