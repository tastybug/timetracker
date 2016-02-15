package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Date;

public class ModifyTimeFrameTask extends AbstractAsyncTask {

    private static final String TAG = ModifyTimeFrameTask.class.getSimpleName();

    private static final String STOP_TRACKING_PROJECT_UUID = "STOP_TRACKING_PROJECT_UUID";
    private static final String TIME_FRAME_UUID = "TIME_FRAME_UUID";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TimeFrame timeFrame;

    public static ModifyTimeFrameTask aTask(Context context) {
        return new ModifyTimeFrameTask(context);
    }

    protected ModifyTimeFrameTask(Context context) {
        super(context);
    }

    public ModifyTimeFrameTask withStoppableProjectUuid(String projectUuid) {
        arguments.putString(STOP_TRACKING_PROJECT_UUID, projectUuid);
        return this;
    }

    public ModifyTimeFrameTask withTimeFrameUuid(String timeFrameUuid) {
        arguments.putString(TIME_FRAME_UUID, timeFrameUuid);
        return this;
    }

    public ModifyTimeFrameTask withStartDate(Date startDate) {
        arguments.putSerializable(START_DATE, startDate);
        return this;
    }

    public ModifyTimeFrameTask withEndDate(Date endDate) {
        arguments.putSerializable(END_DATE, endDate);
        return this;
    }

    public ModifyTimeFrameTask withDescription(Optional<String> description) {
        arguments.putSerializable(DESCRIPTION_OPT, description);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(
                !TextUtils.isEmpty(arguments.getString(STOP_TRACKING_PROJECT_UUID))
                        || !TextUtils.isEmpty(arguments.getString(TIME_FRAME_UUID))
        );
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        if (arguments.containsKey(STOP_TRACKING_PROJECT_UUID)) {
            String stoppableProjectUuid = arguments.getString(STOP_TRACKING_PROJECT_UUID);

            timeFrame = new TimeFrameDAO(context).getRunning(stoppableProjectUuid).get();
            timeFrame.stop();

        } else {
            String timeFrameUuid = arguments.getString(TIME_FRAME_UUID);
            timeFrame = new TimeFrameDAO(context).get(timeFrameUuid).get();

            if(arguments.containsKey(START_DATE)) {
                timeFrame.setStart((Date)arguments.getSerializable(START_DATE));
            }
            if(arguments.containsKey(END_DATE)) {
                timeFrame.setEnd((Date)arguments.getSerializable(END_DATE));
            }
            if(arguments.containsKey(DESCRIPTION_OPT)) {
                timeFrame.setDescription((Optional<String>)arguments.getSerializable(DESCRIPTION_OPT));
            }
        }

        storeBatchOperation(timeFrame.getDAO(context).getBatchUpdate(timeFrame));
    }

    protected void onPostExecute(Long result) {
        // notify otto
        // and update the test
        Log.i(TAG, "Modified timeframe " + timeFrame);
        ottoProvider.getSharedBus().post(new TimeFrameModifiedEvent(timeFrame));
    }

}