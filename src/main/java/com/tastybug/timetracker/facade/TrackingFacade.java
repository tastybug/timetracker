package com.tastybug.timetracker.facade;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.TrackingTaskFactory;

/**
 * Serviceklasse rund um die Zeiterfassung.
 */
public class TrackingFacade {

    private Context context;
    private TrackingTaskFactory trackingTaskFactory;
    private TrackingRecordDAO trackingRecordDAO;

    public TrackingFacade(Context context) {
        this(context, new TrackingRecordDAO(context), new TrackingTaskFactory());
    }

    public TrackingFacade(Context context, TrackingRecordDAO trackingRecordDAO, TrackingTaskFactory factory) {
        this.context = context;
        this.trackingRecordDAO = trackingRecordDAO;
        this.trackingTaskFactory = factory;
    }

    public void startTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        trackingTaskFactory.aKickstartTask(context).withProjectUuid(projectUuid).execute();
    }

    public void stopTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        trackingTaskFactory.aModificationTask(context).withStoppableProjectUuid(projectUuid).execute();
    }

    public Optional<TrackingRecord> getOngoingTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        return trackingRecordDAO.getRunning(projectUuid);
    }
}
