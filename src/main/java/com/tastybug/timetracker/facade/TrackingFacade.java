package com.tastybug.timetracker.facade;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.tracking.KickstartTimeFrameTask;
import com.tastybug.timetracker.task.tracking.ModifyTimeFrameTask;

import java.util.ArrayList;

/**
 * Serviceklasse rund um die Zeiterfassung.
 */
public class TrackingFacade {

    private Context context;
    private TimeFrameDAO timeFrameDAO;

    public TrackingFacade(Context context) {
        this.context = context;
        this.timeFrameDAO = new TimeFrameDAO(context);
    }

    public TrackingFacade(Context context, TimeFrameDAO timeFrameDAO) {
        this.context = context;
        this.timeFrameDAO = timeFrameDAO;
    }

    public void startTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        KickstartTimeFrameTask.aTask(context).withProjectUuid(projectUuid).execute();
    }

    public void stopTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        ModifyTimeFrameTask.aTask(context).withStoppableProjectUuid(projectUuid).execute();
    }

    public boolean isTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        return timeFrameDAO.getRunning(projectUuid).isPresent();
    }

    public ArrayList<TimeFrame> getTimeFrames(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        return timeFrameDAO.getByProjectUuid(projectUuid);
    }

}
