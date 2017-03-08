package com.tastybug.timetracker.core.ui.core;

import android.support.v7.app.AppCompatActivity;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.ui.projectdetails.ProjectStatisticsFragment;
import com.tastybug.timetracker.core.ui.projectdetails.TrackingControlPanelFragment;
import com.tastybug.timetracker.core.ui.projectdetails.TrackingRecordListFragment;

public abstract class BaseActivity extends AppCompatActivity {

    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
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
}
