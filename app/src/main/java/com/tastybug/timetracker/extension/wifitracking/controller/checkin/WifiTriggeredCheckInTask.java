package com.tastybug.timetracker.extension.wifitracking.controller.checkin;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.extension.wifitracking.controller.SessionsLog;

import java.util.List;

class WifiTriggeredCheckInTask extends com.tastybug.timetracker.core.task.tracking.checkin.CheckInTask {

    private static final String TRIGGERING_SSID = "TRIGGERING_SSID";

    WifiTriggeredCheckInTask(Context context) {
        super(context);
    }

    WifiTriggeredCheckInTask withSsidUuid(String ssid) {
        arguments.putString(TRIGGERING_SSID, ssid);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        super.validate();
        Preconditions.checkArgument(arguments.containsKey(TRIGGERING_SSID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        List<ContentProviderOperation> operations = super.prepareBatchOperations();
        logSessionStart();
        return operations;
    }

    private void logSessionStart() {
        SessionsLog sessionsLog = new SessionsLog(context);
        sessionsLog.addSession(trackingRecord.getUuid(), arguments.getString(TRIGGERING_SSID));
    }
}
