package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.AbstractAsyncTask;

public class KickstopTrackingRecordTask extends AbstractAsyncTask {

    private static final String TAG = ModifyTrackingRecordTask.class.getSimpleName();

    static final String PROJECT_UUID    = "PROJECT_UUID";

    protected TrackingRecord trackingRecord;

    public static KickStartTrackingRecordTask aTask(Context context) {
        return new KickStartTrackingRecordTask(context);
    }

    protected KickstopTrackingRecordTask(Context context) {
        super(context);
    }

    public KickstopTrackingRecordTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String stoppableProjectUuid = arguments.getString(PROJECT_UUID);

        trackingRecord = new TrackingRecordDAO(context).getRunning(stoppableProjectUuid).get();
        trackingRecord.stop();

        storeBatchOperation(trackingRecord.getDAO(context).getBatchUpdate(trackingRecord));
    }

    protected void onPostExecute(Long result) {
        Log.i(TAG, "Kick stopped tracking record " + trackingRecord);
        ottoProvider.getSharedBus().post(new KickStoppedTrackingRecordEvent(trackingRecord));
    }
}