package com.tastybug.timetracker.core.task.tracking.checkout;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.Collections;
import java.util.List;

public class CheckOutTask extends TaskPayload {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    protected TrackingRecord trackingRecord;
    protected TrackingRecordDAO trackingRecordDAO;

    public CheckOutTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    CheckOutTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public CheckOutTask withTrackingRecordUuid(String trackingRecordUuid) {
        arguments.putString(TRACKING_RECORD_UUID, trackingRecordUuid);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(TRACKING_RECORD_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        String trackingRecordUuid = getTrackingRecordUuid();

        trackingRecord = trackingRecordDAO.get(trackingRecordUuid).get();
        stopTrackingRecord(trackingRecord);

        return Collections.singletonList(trackingRecordDAO.getBatchUpdate(trackingRecord));
    }

    protected String getTrackingRecordUuid() {
        return arguments.getString(TRACKING_RECORD_UUID);
    }

    protected void stopTrackingRecord(TrackingRecord trackingRecord) {
        trackingRecord.stop();
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new CheckOutEvent(trackingRecord);
    }
}