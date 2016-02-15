package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TimeFrame;

import java.util.Date;

public class CreateTimeFrameTask extends KickstartTimeFrameTask {

    private static final String START_DATE      = "START_DATE";
    private static final String END_DATE        = "END_DATE";
    private static final String DESCRIPTION_OPT = "DESCRIPTION_OPT";

    public static CreateTimeFrameTask aTask(Context context) {
        return new CreateTimeFrameTask(context);
    }

    protected CreateTimeFrameTask(Context context) {
        super(context);
    }

    public CreateTimeFrameTask withProjectUuid(String projectUuid) {
        super.withProjectUuid(projectUuid);
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
    protected void performBackgroundStuff(Bundle args) {
        timeFrame = new TimeFrame(arguments.getString(PROJECT_UUID));
        timeFrame.setStart((Date)arguments.getSerializable(START_DATE));
        timeFrame.setEnd((Date)arguments.getSerializable(END_DATE));
        if(arguments.containsKey(DESCRIPTION_OPT)) {
            timeFrame.setDescription((Optional<String>)arguments.getSerializable(DESCRIPTION_OPT));
        }
        storeBatchOperation(timeFrame.getDAO(context).getBatchCreate(timeFrame));
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkArgument(arguments.containsKey(PROJECT_UUID)
                                    && arguments.containsKey(START_DATE)
                                    && arguments.containsKey(END_DATE));
    }

}