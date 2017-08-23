package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;

import rx.Observable;

/**
 * Created by jerzyw on 13.10.2016.
 */

public interface DashboardService extends BaseService {

    Observable<DashboardResponse> getDashboardData();
}
