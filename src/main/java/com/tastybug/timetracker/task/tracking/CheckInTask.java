package com.tastybug.timetracker.task.tracking;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Arrays;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class CheckInTask extends AbstractAsyncTask {

    static final String PROJECT_UUID = "PROJECT_UUID";

    private TrackingRecord trackingRecord;

    private CheckInTask(Context context) {
        super(context);
    }

    public static CheckInTask aTask(Context context) {
        return new CheckInTask(context);
    }

    public CheckInTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID));
    }

    @Override
    protected List<ContentProviderOperation> performBackgroundStuff(Bundle args) {
        String projectUuid = arguments.getString(PROJECT_UUID);
        if (new TrackingRecordDAO(context).getRunning(projectUuid).isPresent()) {
            throw new IllegalArgumentException("Project is already tracking!");
        }

        trackingRecord = new TrackingRecord(projectUuid);
        trackingRecord.start();
        return Arrays.asList(new TrackingRecordDAO(context).getBatchCreate(trackingRecord));
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Performed CheckIn " + trackingRecord);
        ottoProvider.getSharedBus().post(new CheckInEvent(trackingRecord));
    }
}
