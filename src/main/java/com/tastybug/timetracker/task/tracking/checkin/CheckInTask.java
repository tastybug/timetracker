package com.tastybug.timetracker.task.tracking.checkin;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Collections;
import java.util.List;

public class CheckInTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecord trackingRecord;

    public CheckInTask(Context context) {
        this(context, new OttoProvider(), new TrackingRecordDAO(context));
    }

    CheckInTask(Context context, OttoProvider ottoProvider, TrackingRecordDAO trackingRecordDAO) {
        super(context, ottoProvider);
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
