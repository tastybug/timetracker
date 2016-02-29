package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Date;

public class CreateTrackingRecordTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID    = "PROJECT_UUID";
    private static final String START_DATE      = "START_DATE";
    private static final String END_DATE        = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TrackingRecord trackingRecord;

    public static CreateTrackingRecordTask aTask(Context context) {
        return new CreateTrackingRecordTask(context);
    }

    protected CreateTrackingRecordTask(Context context) {
        super(context);
    }

    public CreateTrackingRecordTask withProjectUuid(String projectUuid) {
        arguments.putSerializable(PROJECT_UUID, projectUuid);
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
    protected void performBackgroundStuff(Bundle args) {
        trackingRecord = new TrackingRecord(arguments.getString(PROJECT_UUID));
        trackingRecord.setStart((Date)arguments.getSerializable(START_DATE));
        trackingRecord.setEnd((Date)arguments.getSerializable(END_DATE));
        if(arguments.containsKey(DESCRIPTION_OPT)) {
            trackingRecord.setDescription((Optional<String>)arguments.getSerializable(DESCRIPTION_OPT));
        }
        storeBatchOperation(trackingRecord.getDAO(context).getBatchCreate(trackingRecord));
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID)
                                    && arguments.containsKey(START_DATE)
                                    && arguments.containsKey(END_DATE));
    }

    protected void onPostExecute(Long result) {
        Log.i(getClass().getSimpleName(), "Created tracking record " + trackingRecord);
        ottoProvider.getSharedBus().post(new CreatedTrackingRecordEvent(trackingRecord));
    }
}