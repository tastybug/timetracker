package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class CheckOutTask extends AbstractAsyncTask {

    static final String PROJECT_UUID = "PROJECT_UUID";

    protected TrackingRecord trackingRecord;

    public static CheckOutTask aTask(Context context) {
        return new CheckOutTask(context);
    }

    protected CheckOutTask(Context context) {
        super(context);
    }

    public CheckOutTask withProjectUuid(String projectUuid) {
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
        logInfo(getClass().getSimpleName(), "Performed CheckOut " + trackingRecord);
        ottoProvider.getSharedBus().post(new CheckOutEvent(trackingRecord));
    }
}