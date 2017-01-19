package com.tastybug.timetracker.task.tracking.checkout;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Collections;
import java.util.List;

public class CheckOutTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    protected TrackingRecord trackingRecord;
    private TrackingRecordDAO trackingRecordDAO;

    public CheckOutTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    CheckOutTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public CheckOutTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        String stoppableProjectUuid = arguments.getString(PROJECT_UUID);

        trackingRecord = trackingRecordDAO.getRunning(stoppableProjectUuid).get();
        trackingRecord.stop();

        return Collections.singletonList(trackingRecordDAO.getBatchUpdate(trackingRecord));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new CheckOutEvent(trackingRecord);
    }
}