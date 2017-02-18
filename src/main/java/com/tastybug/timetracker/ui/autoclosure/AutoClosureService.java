package com.tastybug.timetracker.ui.autoclosure;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.statistics.Expiration;
import com.tastybug.timetracker.task.project.config.ConfigureProjectTask;

import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

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
                new ConfigureProjectTask(getApplicationContext())
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
