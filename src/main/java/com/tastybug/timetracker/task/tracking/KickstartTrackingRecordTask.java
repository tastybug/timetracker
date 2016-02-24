package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.AbstractAsyncTask;

public class KickstartTrackingRecordTask extends AbstractAsyncTask {

    static final String PROJECT_UUID    = "PROJECT_UUID";

    private TrackingRecord trackingRecord;

    public static KickstartTrackingRecordTask aTask(Context context) {
        return new KickstartTrackingRecordTask(context);
    }

    protected KickstartTrackingRecordTask(Context context) {
        super(context);
    }

    public KickstartTrackingRecordTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String projectUuid = arguments.getString(PROJECT_UUID);
        if(new TrackingRecordDAO(context).getRunning(projectUuid).isPresent()) {
            throw new IllegalArgumentException("Project is already tracking!");
        }

        trackingRecord = new TrackingRecord(projectUuid);
        trackingRecord.start();
        storeBatchOperation(trackingRecord.getDAO(context).getBatchCreate(trackingRecord));
    }

    protected void onPostExecute(Long result) {
        Log.i(getClass().getSimpleName(), "Kick started tracking record " + trackingRecord);
        ottoProvider.getSharedBus().post(new TrackingRecordKickstartedEvent(trackingRecord));
    }
}
