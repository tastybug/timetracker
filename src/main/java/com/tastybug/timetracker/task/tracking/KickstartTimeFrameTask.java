package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.project.AbstractAsyncTask;

public class KickstartTimeFrameTask extends AbstractAsyncTask {

    private static final String TAG = KickstartTimeFrameTask.class.getSimpleName();

    static final String PROJECT_UUID    = "PROJECT_UUID";

    protected TimeFrame timeFrame;

    public static KickstartTimeFrameTask aTask(Context context) {
        return new KickstartTimeFrameTask(context);
    }

    protected KickstartTimeFrameTask(Context context) {
        super(context);
    }

    public KickstartTimeFrameTask withProjectUuid(String projectUuid) {
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
        if(new TimeFrameDAO(context).getRunning(projectUuid).isPresent()) {
            throw new IllegalArgumentException("Project is already tracking!");
        }

        timeFrame = new TimeFrame(projectUuid);
        timeFrame.start();
        storeBatchOperation(timeFrame.getDAO(context).getBatchCreate(timeFrame));
    }

    protected void onPostExecute(Long result) {
        // notify otto
        // and update the test
        Log.i(TAG, "Created timeframe " + timeFrame);
        ottoProvider.getSharedBus().post(new TimeFrameCreatedEvent(timeFrame));
    }
}
