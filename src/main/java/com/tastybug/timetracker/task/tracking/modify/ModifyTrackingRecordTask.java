package com.tastybug.timetracker.task.tracking.modify;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ModifyTrackingRecordTask extends TaskPayload {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecord trackingRecord;
    private boolean wasStopped = false;

    public ModifyTrackingRecordTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    ModifyTrackingRecordTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public ModifyTrackingRecordTask withTrackingRecordUuid(String trackingRecordUuid) {
        arguments.putString(TRACKING_RECORD_UUID, trackingRecordUuid);
        return this;
    }

    public ModifyTrackingRecordTask withStartDate(Date startDate) {
        arguments.putSerializable(START_DATE, startDate);
        return this;
    }

    public ModifyTrackingRecordTask withEndDate(Date endDate) {
        arguments.putSerializable(END_DATE, endDate);
        return this;
    }

    public ModifyTrackingRecordTask withDescription(Optional<String> description) {
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
        return new ModifiedTrackingRecordEvent(trackingRecord, wasStopped);
    }
}