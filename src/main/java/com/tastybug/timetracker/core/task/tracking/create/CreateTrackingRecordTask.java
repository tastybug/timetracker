package com.tastybug.timetracker.core.task.tracking.create;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.rounding.Rounding;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CreateTrackingRecordTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecord trackingRecord;

    public CreateTrackingRecordTask(Context context) {
        this(context, new TrackingRecordDAO(context));
    }

    CreateTrackingRecordTask(Context context, TrackingRecordDAO trackingRecordDAO) {
        super(context);
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public CreateTrackingRecordTask withProjectUuid(String projectUuid) {
        arguments.putSerializable(PROJECT_UUID, projectUuid);
        return this;
    }

    public CreateTrackingRecordTask withRoundingStrategy(Rounding.Strategy strategy) {
        arguments.putSerializable(ROUNDING_STRATEGY, strategy);
        return this;
    }

    public CreateTrackingRecordTask withStartDate(Date startDate) {
        arguments.putSerializable(START_DATE, startDate);
        return this;
    }

    public CreateTrackingRecordTask withEndDate(Date endDate) {
        arguments.putSerializable(END_DATE, endDate);
        return this;
    }

    public CreateTrackingRecordTask withDescription(Optional<String> description) {
        arguments.putSerializable(DESCRIPTION_OPT, description);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID)
                && arguments.containsKey(START_DATE)
                && arguments.containsKey(END_DATE)
                && arguments.containsKey(ROUNDING_STRATEGY));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        trackingRecord = new TrackingRecord(arguments.getString(PROJECT_UUID),
                (Rounding.Strategy) arguments.getSerializable(ROUNDING_STRATEGY));
        trackingRecord.setStart((Date) arguments.getSerializable(START_DATE));
        trackingRecord.setEnd((Date) arguments.getSerializable(END_DATE));
        if (arguments.containsKey(DESCRIPTION_OPT)) {
            trackingRecord.setDescription((Optional<String>) arguments.getSerializable(DESCRIPTION_OPT));
        }

        return Collections.singletonList(trackingRecordDAO.getBatchCreate(trackingRecord));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new CreatedTrackingRecordEvent(trackingRecord);
    }
}