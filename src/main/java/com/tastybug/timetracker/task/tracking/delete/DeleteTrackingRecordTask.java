package com.tastybug.timetracker.task.tracking.delete;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Collections;
import java.util.List;

public class DeleteTrackingRecordTask extends TaskPayload {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";

    public DeleteTrackingRecordTask(Context context) {
        super(context, new OttoProvider());
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
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        new TrackingRecordDAO(context).delete(uuid);

        return Collections.emptyList();
    }

    @Override
    protected OttoEvent preparePostEvent() {
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        return new DeletedTrackingRecordEvent(uuid);
    }
}
