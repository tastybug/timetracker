package com.tastybug.timetracker.extension.autoclosure.controller;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.statistics.Expiration;
import com.tastybug.timetracker.core.task.project.update.UpdateProjectTask;

import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class AutoClosureService extends IntentService {

    private static final String TAG = AutoClosureService.class.getSimpleName();

    public AutoClosureService() {
        super(AutoClosureService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logInfo(TAG, "Starting autoclosure NOW!");
        List<Project> projects = getAllProjects();
        for (Project project : projects) {
            if (isProjectClosable(project)) {
                new UpdateProjectTask(getApplicationContext())
                        .withProjectUuid(project.getUuid())
                        .withClosureState(true)
                        .run();
            }
        }
    }

    private boolean isProjectClosable(Project project) {
        if (!project.isClosed()) {
            Expiration expiration = new Expiration(getTrackingConfiguration(project.getUuid()));
            return expiration.isExpired();
        }
        return false;
    }

    private List<Project> getAllProjects() {
        return new ProjectDAO(getApplicationContext()).getAll();
    }

    private TrackingConfiguration getTrackingConfiguration(String projectUuid) {
        return new TrackingConfigurationDAO(getApplicationContext()).getByProjectUuid(projectUuid).get();
    }
}
