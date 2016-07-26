package com.tastybug.timetracker.infrastructure.backup.in;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.util.List;

public class BackupDataImporter {

    static final String TAG = BackupDataImporter.class.getSimpleName();

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    public BackupDataImporter(Context context) {
        projectDAO = new ProjectDAO(context);
        trackingConfigurationDAO = new TrackingConfigurationDAO(context);
        trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public BackupDataImporter(ProjectDAO projectDAO,
                              TrackingConfigurationDAO trackingConfigurationDAO,
                              TrackingRecordDAO trackingRecordDAO) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public void restoreProjectList(List<Project> projects) {
        Preconditions.checkNotNull(projects);
        if (projects.size() == 0) {
            Log.w(TAG, "Skipping restoration, no projects given to restore!");
            return;
        }
        Log.i(TAG, "Starting to restore " + projects.size() + " projects..");
        emptyDatabase();
        for (Project project : projects) {
            restoreProject(project);
        }
    }

    private void emptyDatabase() {
        Log.i(TAG, "Clearing database..");
        List<Project> existingProject = projectDAO.getAll();
        for (Project p : existingProject) {
            projectDAO.delete(p);
            Log.i(TAG, "Deleted " + p.getTitle());
        }
    }

    private void restoreProject(Project project) {
        Log.i(TAG, "Restoring project " + project.getTitle() + "..");
        projectDAO.create(project);
        trackingConfigurationDAO.create(project.getTrackingConfiguration());
        for (TrackingRecord trackingRecord : project.getTrackingRecords()) {
            trackingRecordDAO.create(trackingRecord);
        }
    }
}
