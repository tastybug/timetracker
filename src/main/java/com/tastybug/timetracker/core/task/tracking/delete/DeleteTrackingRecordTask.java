package com.tastybug.timetracker.core.task.tracking.delete;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.Collections;
import java.util.List;

public class DeleteTrackingRecordTask extends TaskPayload {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";

    private TrackingRecordDAO trackingRecordDAO;
    private String projectUuid, trackingRecordUuid;

    public DeleteTrackingRecordTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    DeleteTrackingRecordTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public DeleteTrackingRecordTask withTrackingRecordUuid(String uuid) {
        arguments.putString(TRACKING_RECORD_UUID, uuid);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkNotNull(arguments.getString(TRACKING_RECORD_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        trackingRecordUuid = arguments.getString(TRACKING_RECORD_UUID);
        projectUuid = trackingRecordDAO.get(trackingRecordUuid).get().getProjectUuid();
        trackingRecordDAO.delete(trackingRecordUuid);

        return Collections.emptyList();
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new DeletedTrackingRecordEvent(projectUuid, trackingRecordUuid);
    }
}
