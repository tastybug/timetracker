package com.tastybug.timetracker.core.task.tracking.checkin;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.Collections;
import java.util.List;

public class CheckInTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecord trackingRecord;

    public CheckInTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    CheckInTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public CheckInTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        String projectUuid = arguments.getString(PROJECT_UUID);
        if (trackingRecordDAO.getRunning(projectUuid).isPresent()) {
            throw new IllegalArgumentException("Project is already tracking!");
        }

        trackingRecord = new TrackingRecord(projectUuid);
        trackingRecord.start();
        return Collections.singletonList(trackingRecordDAO.getBatchCreate(trackingRecord));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new CheckInEvent(trackingRecord);
    }
}
