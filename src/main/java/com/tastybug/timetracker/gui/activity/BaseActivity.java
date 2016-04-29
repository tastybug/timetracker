package com.tastybug.timetracker.gui.activity;

import android.app.Activity;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.fragment.project.configuration.ProjectConfigurationFragment;
import com.tastybug.timetracker.gui.fragment.project.configuration.TrackingConfigurationFragment;
import com.tastybug.timetracker.gui.fragment.trackingrecord.list.TrackingRecordListFragment;
import com.tastybug.timetracker.gui.fragment.trackingrecord.log.TrackingLogStatisticsFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

public abstract class BaseActivity extends Activity {

    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    protected TrackingConfiguration getTrackingConfigurationByProjectUuid(String projectUuid) {
        return new TrackingConfigurationDAO(this).getByProjectUuid(projectUuid).get();
    }

    protected TrackingRecord getTrackingRecordByUuid(String uuid) {
        return new TrackingRecordDAO(this).get(uuid).get();
    }

    protected TrackingLogStatisticsFragment getTrackingLogStatisticsFragment() {
        return (TrackingLogStatisticsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_tracking_log_statistics);
    }

    protected TrackingRecordListFragment getTrackingLogFragment() {
        return (TrackingRecordListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_tracking_log);
    }

    protected ProjectConfigurationFragment getProjectConfigurationFragment() {
        return (ProjectConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_project_configuration);
    }

    protected TrackingConfigurationFragment getTrackingConfigurationFragment() {
        return (TrackingConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_tracking_configuration);
    }

}
