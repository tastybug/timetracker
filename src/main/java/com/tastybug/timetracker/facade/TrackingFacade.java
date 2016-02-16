package com.tastybug.timetracker.facade;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.KickstartTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.ModifyTrackingRecordTask;

import java.util.ArrayList;

/**
 * Serviceklasse rund um die Zeiterfassung.
 */
public class TrackingFacade {

    private Context context;
    private TrackingRecordDAO trackingRecordDAO;

    public TrackingFacade(Context context) {
        this.context = context;
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public TrackingFacade(Context context, TrackingRecordDAO trackingRecordDAO) {
        this.context = context;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public void startTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        KickstartTrackingRecordTask.aTask(context).withProjectUuid(projectUuid).execute();
    }

    public void stopTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        ModifyTrackingRecordTask.aTask(context).withStoppableProjectUuid(projectUuid).execute();
    }

    public boolean isTracking(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        return trackingRecordDAO.getRunning(projectUuid).isPresent();
    }

    // TODO klaeren:
    // entweder macht man das ueber die Entity (Project) oder hier ueber die Facade
    // beide Wege anzubieten ist kacke
    // Unter Umstaenden hat man zwar nicht immer eine Projektinstanz am start, es waere aber
    // konsistenter, dann eben eine zu erstellen
    public ArrayList<TrackingRecord> getTrackingRecords(String projectUuid) {
        Preconditions.checkArgument(!TextUtils.isEmpty(projectUuid), "Project UUID is empty!");

        return trackingRecordDAO.getByProjectUuid(projectUuid);
    }



}
