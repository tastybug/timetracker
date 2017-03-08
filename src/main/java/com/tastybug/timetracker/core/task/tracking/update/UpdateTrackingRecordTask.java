package com.tastybug.timetracker.core.task.tracking.update;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UpdateTrackingRecordTask extends TaskPayload {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecord trackingRecord;
    private boolean wasStopped = false;

    public UpdateTrackingRecordTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    UpdateTrackingRecordTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public UpdateTrackingRecordTask withTrackingRecordUuid(String trackingRecordUuid) {
        arguments.putString(TRACKING_RECORD_UUID, trackingRecordUuid);
        return this;
    }

    public UpdateTrackingRecordTask withStartDate(Date startDate) {
        arguments.putSerializable(START_DATE, startDate);
        return this;
    }

    public UpdateTrackingRecordTask withEndDate(Date endDate) {
        arguments.putSerializable(END_DATE, endDate);
        return this;
    }

    public UpdateTrackingRecordTask withDescription(Optional<String> description) {
        arguments.putSerializable(DESCRIPTION_OPT, description);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(TRACKING_RECORD_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        String trackingRecordUuid = arguments.getString(TRACKING_RECORD_UUID);
        trackingRecord = trackingRecordDAO.get(trackingRecordUuid).get();

        if (arguments.containsKey(START_DATE)) {
            trackingRecord.setStart((Date) arguments.getSerializable(START_DATE));
        }
        if (arguments.containsKey(END_DATE)) {
            wasStopped = trackingRecord.isRunning();
            trackingRecord.setEnd((Date) arguments.getSerializable(END_DATE));
        }
        if (arguments.containsKey(DESCRIPTION_OPT)) {
            trackingRecord.setDescription((Optional<String>) arguments.getSerializable(DESCRIPTION_OPT));
        }

        return Collections.singletonList(trackingRecordDAO.getBatchUpdate(trackingRecord));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new UpdateTrackingRecordEvent(trackingRecord, wasStopped);
    }
}