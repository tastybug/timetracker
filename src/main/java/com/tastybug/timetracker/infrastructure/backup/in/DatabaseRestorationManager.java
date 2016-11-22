package com.tastybug.timetracker.infrastructure.backup.in;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;
import static com.tastybug.timetracker.util.ConditionalLog.logWarn;

class DatabaseRestorationManager {

    private static final String TAG = DatabaseRestorationManager.class.getSimpleName();

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    DatabaseRestorationManager(Context context) {
        projectDAO = new ProjectDAO(context);
        trackingConfigurationDAO = new TrackingConfigurationDAO(context);
        trackingRecordDAO = new TrackingRecordDAO(context);
    }

    DatabaseRestorationManager(ProjectDAO projectDAO,
                               TrackingConfigurationDAO trackingConfigurationDAO,
                               TrackingRecordDAO trackingRecordDAO) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    void restoreProjectList(List<Project> projects) {
        Preconditions.checkNotNull(projects);
        if (projects.size() == 0) {
            logWarn(TAG, "Skipping restoration, no projects given to restore!");
            return;
        }
        logInfo(TAG, "Starting to restore " + projects.size() + " projects..");
        emptyDatabase();
        for (Project project : projects) {
            restoreProject(project);
        }
    }

    private void emptyDatabase() {
        logInfo(TAG, "Clearing database..");
        List<Project> existingProject = projectDAO.getAll();
        for (Project p : existingProject) {
            projectDAO.delete(p);
            logInfo(TAG, "Deleted " + p.getTitle());
        }
    }

    private void restoreProject(Project project) {
        logInfo(TAG, "Restoring project " + project.getTitle() + "..");
        projectDAO.create(project);
        trackingConfigurationDAO.create(project.getTrackingConfiguration());
        for (TrackingRecord trackingRecord : project.getTrackingRecords()) {
            trackingRecordDAO.create(trackingRecord);
        }
    }
}
