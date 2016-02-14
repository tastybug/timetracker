package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.project.AbstractAsyncTask;

import java.util.Date;

public class CreateTimeFrameTask extends AbstractAsyncTask {

    private static final String TAG = CreateTimeFrameTask.class.getSimpleName();

    private static final String BY_PROJECT_UUID = "BY_PROJECT_UUID";
    private static final String PROJECT_UUID    = "PROJECT_UUID";
    private static final String START_DATE      = "START_DATE";
    private static final String END_DATE        = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    private TimeFrame timeFrame;

    public static CreateTimeFrameTask aTask(Context context) {
        return new CreateTimeFrameTask(context);
    }

    private CreateTimeFrameTask(Context context) {
        super(context);
    }

    public CreateTimeFrameTask byProjectUuid(String projectUuid) {
        arguments.putString(BY_PROJECT_UUID, projectUuid);
        return this;
    }

    public CreateTimeFrameTask withProjectUuid(String projectUuid) {
        arguments.putString(PROJECT_UUID, projectUuid);
        return this;
    }

    public CreateTimeFrameTask withStartDate(Date startDate) {
        arguments.putSerializable(START_DATE, startDate);
        return this;
    }

    public CreateTimeFrameTask withEndDate(Date endDate) {
        arguments.putSerializable(END_DATE, endDate);
        return this;
    }

    public CreateTimeFrameTask withDescription(Optional<String> description) {
        arguments.putSerializable(DESCRIPTION_OPT, description);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(BY_PROJECT_UUID)
                                    || (arguments.containsKey(PROJECT_UUID)
                                        && arguments.containsKey(START_DATE)
                                        && arguments.containsKey(END_DATE)));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String projectUuid;
        if(arguments.containsKey(BY_PROJECT_UUID)) {
            projectUuid = arguments.getString(BY_PROJECT_UUID);
            if(new TimeFrameDAO(context).getRunning(projectUuid).isPresent()) {
                throw new IllegalArgumentException("Project is already tracking!");
            }

            timeFrame = new TimeFrame(projectUuid);
            timeFrame.start();
        } else {
            timeFrame = new TimeFrame(arguments.getString(PROJECT_UUID));
            timeFrame.setStart((Date)arguments.getSerializable(START_DATE));
            timeFrame.setEnd((Date)arguments.getSerializable(END_DATE));
            if(arguments.containsKey(DESCRIPTION_OPT)) {
                timeFrame.setDescription((Optional<String>)arguments.getSerializable(DESCRIPTION_OPT));
            }
        }
        storeBatchOperation(timeFrame.getDAO(context).getBatchCreate(timeFrame));
    }

    protected void onPostExecute(Long result) {
        // notify otto
        // and update the test
        Log.i(TAG, "Created timeframe " + timeFrame);
        ottoProvider.getSharedBus().post(new TimeFrameCreatedEvent(timeFrame));
    }
}