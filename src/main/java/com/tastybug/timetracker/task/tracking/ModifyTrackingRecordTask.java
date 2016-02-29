package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Date;

public class ModifyTrackingRecordTask extends AbstractAsyncTask {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TrackingRecord trackingRecord;

    public static ModifyTrackingRecordTask aTask(Context context) {
        return new ModifyTrackingRecordTask(context);
    }

    protected ModifyTrackingRecordTask(Context context) {
        super(context);
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
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(!TextUtils.isEmpty(arguments.getString(TRACKING_RECORD_UUID)));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String trackingRecordUuid = arguments.getString(TRACKING_RECORD_UUID);
        trackingRecord = new TrackingRecordDAO(context).get(trackingRecordUuid).get();

        if(arguments.containsKey(START_DATE)) {
            trackingRecord.setStart((Date)arguments.getSerializable(START_DATE));
        }
        if(arguments.containsKey(END_DATE)) {
            trackingRecord.setEnd((Date)arguments.getSerializable(END_DATE));
        }
        if(arguments.containsKey(DESCRIPTION_OPT)) {
            trackingRecord.setDescription((Optional<String>)arguments.getSerializable(DESCRIPTION_OPT));
        }

        storeBatchOperation(trackingRecord.getDAO(context).getBatchUpdate(trackingRecord));
    }

    protected void onPostExecute(Long result) {
        Log.i(getClass().getSimpleName(), "Modified tracking record " + trackingRecord);
        ottoProvider.getSharedBus().post(new ModifiedTrackingRecordEvent(trackingRecord));
    }

}