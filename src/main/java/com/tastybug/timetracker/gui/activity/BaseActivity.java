package com.tastybug.timetracker.gui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.fragment.project.configuration.ProjectConfigurationFragment;
import com.tastybug.timetracker.gui.fragment.project.configuration.TrackingConfigurationFragment;
import com.tastybug.timetracker.gui.fragment.project.statistics.ProjectStatisticsFragment;
import com.tastybug.timetracker.gui.fragment.trackingrecord.control.TrackingControlPanelFragment;
import com.tastybug.timetracker.gui.fragment.trackingrecord.list.TrackingRecordListFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

public abstract class BaseActivity extends AppCompatActivity {

    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    protected TrackingConfiguration getTrackingConfigurationByProjectUuid(String projectUuid) {
        return new TrackingConfigurationDAO(this).getByProjectUuid(projectUuid).get();
    }

    protected TrackingRecord getTrackingRecordByUuid(String uuid) {
        return new TrackingRecordDAO(this).get(uuid).get();
    }

    protected ProjectStatisticsFragment getProjectStatisticsFragment() {
        return (ProjectStatisticsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_statistics);
    }

    protected TrackingControlPanelFragment getTrackingControlPanelFragment() {
        return (TrackingControlPanelFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_control_panel);
    }

    protected TrackingRecordListFragment getTrackingRecordListFragment() {
        return (TrackingRecordListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_record_list);
    }

    protected ProjectConfigurationFragment getProjectConfigurationFragment() {
        return (ProjectConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_project_configuration);
    }

    protected TrackingConfigurationFragment getTrackingConfigurationFragment() {
        return (TrackingConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_tracking_configuration);
    }

}
