package com.tastybug.timetracker.core.ui.dashboard;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ProjectListAdapter extends BaseAdapter {

    private List<Project> projectArrayList = new ArrayList<>();
    private Activity activity;

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    ProjectListAdapter(Activity activity) {
        projectArrayList = new ProjectDAO(activity).getAll();
        Collections.sort(projectArrayList, new ProjectComparator());
        this.activity = activity;

        this.trackingRecordDAO = new TrackingRecordDAO(activity);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(activity);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        return projectArrayList.size();
    }

    public Object getItem(int position) {
        return projectArrayList.get(position);
    }

    private Project getProjectAt(int position) {
        return (Project) getItem(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ProjectView(activity, null);
        }
        Project project = getProjectAt(position);
        ProjectView projectView = (ProjectView) convertView;
        if (project.isClosed()) {
            projectView.showClosedProject(project);
        } else {
            projectView.showProject(project, getLatestByStartDateForProject(project));
        }

        return projectView;
    }

    private Optional<TrackingRecord> getLatestByStartDateForProject(Project project) {
        return trackingRecordDAO.getLatestByStartDateForProjectUuid(project.getUuid());
    }
}
