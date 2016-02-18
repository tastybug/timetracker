package com.tastybug.timetracker.task.tracking;

import android.content.Context;

public class TrackingTaskFactory {

    public TrackingTaskFactory() {}

    public CreateTrackingRecordTask aCreateTask(Context context) {
        return new CreateTrackingRecordTask(context);
    }

    public DeleteTrackingRecordTask aDeleteTask(Context context) {
        return new DeleteTrackingRecordTask(context);
    }

    public KickstartTrackingRecordTask aKickstartTask(Context context) {
        return new KickstartTrackingRecordTask(context);
    }

    public ModifyTrackingRecordTask aModificationTask(Context context) {
        return new ModifyTrackingRecordTask(context);
    }

}
