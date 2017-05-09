package com.tastybug.timetracker.core.ui.core;

import android.support.v7.app.AppCompatActivity;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

public abstract class BaseActivity extends AppCompatActivity {

    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    protected TrackingRecord getTrackingRecordByUuid(String uuid) {
        return new TrackingRecordDAO(this).get(uuid).get();
    }
}
