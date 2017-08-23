package com.google.android.apps.miyagi.development.data.storage.config;

import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;

/**
 * Created by jerzyw on 03.11.2016.
 */

public interface ConfigStorage {

    void saveSelectedMarket(Market market);

    Market getSelectedMarket();

    void saveLoginToken(String token);

    String readLoginToken();

    void saveLabels(LabelsResponseData labels);

    LabelsResponseData getLabels();

    void saveCommonData(CommonDataResponseData responseData);

    CommonDataResponseData getCommonData();

    void savePushToken(String token);

    String readPushToken();

    void saveShouldUpdatePushToken(boolean shouldUpdatePushToken);

    boolean readShouldUpdatePushToken();

    void saveShouldUpdateDashboard(boolean shouldUpdateDashboard);

    boolean readShouldUpdateDashboard();

    void clearAllData();
}
