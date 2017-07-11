package com.tastybug.timetracker.extension.wifitracking.controller.checkout;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.extension.wifitracking.controller.SessionsLog;

import java.util.Date;
import java.util.List;

class WifiTriggeredCheckOutTask extends CheckOutTask {

    private Date wifiLogoffDate;

    WifiTriggeredCheckOutTask(Context context) {
        super(context);
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        SessionsLog sessionsLog = new SessionsLog(context);
        SessionsLog.Entry entry = sessionsLog.getSession(getTrackingRecordUuid()).get();
        wifiLogoffDate = entry.getEndDate();
        return super.prepareBatchOperations();
    }

    @Override
    protected void stopTrackingRecord(TrackingRecord trackingRecord) {
        trackingRecord.setEnd(wifiLogoffDate);
    }
}
