package com.tastybug.timetracker.extension.wifitracking.controller.checkin;

import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logWarn;

public class WifiTriggeredCheckInDelegate {

    private TriggerRepository triggerRepository;
    private ProjectDAO projectDAO;
    private TrackingRecordDAO trackingRecordDAO;
    private WifiTriggeredCheckInTask wifiTriggeredCheckInTask;

    public WifiTriggeredCheckInDelegate(Context context) {
        this(new TriggerRepository(context),
                new ProjectDAO(context),
                new TrackingRecordDAO(context),
                new WifiTriggeredCheckInTask(context));
    }

    WifiTriggeredCheckInDelegate(TriggerRepository triggerRepository,
                                 ProjectDAO projectDAO,
                                 TrackingRecordDAO trackingRecordDAO,
                                 WifiTriggeredCheckInTask task) {
        this.triggerRepository = triggerRepository;
        this.projectDAO = projectDAO;
        this.trackingRecordDAO = trackingRecordDAO;
        this.wifiTriggeredCheckInTask = task;
    }

    public void handleWifiConnected(String ssid) {
        List<String> projectsListeningAtThisSsid = triggerRepository.getProjectUuidsBoundToSsid(ssid);
        if (projectsListeningAtThisSsid.isEmpty()) {
            return;
        }

        for (String projectUuid : projectsListeningAtThisSsid) {
            if (isProjectTriggerable(projectUuid)) {
                wifiTriggeredCheckInTask
                        .withSsidUuid(ssid)
                        .withProjectUuid(projectUuid)
                        .run();
            } else {
                logInfo(getClass().getSimpleName(), "Project %s cannot be triggered, skipped.", projectUuid);
            }
        }
    }

    private boolean isProjectTriggerable(String projectUuid) {
        Optional<Project> project = projectDAO.get(projectUuid);
        if (!project.isPresent()) {
            logWarn(getClass().getSimpleName(), "Project %s was not found, but is configured for triggering!", projectUuid);
            return false;
        } else if (project.get().isClosed()) {
            logInfo(getClass().getSimpleName(), "Project %s is closed and will not be triggered.", projectUuid);
            return false;
        } else {
            return !trackingRecordDAO.getRunning(projectUuid).isPresent();
        }
    }
}
