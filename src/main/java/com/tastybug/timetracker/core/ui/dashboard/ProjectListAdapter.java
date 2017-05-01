package com.tastybug.timetracker.core.ui.dashboard;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.ProjectDuration;

import java.util.ArrayList;
import java.util.Collections;

class ProjectListAdapter extends BaseAdapter {

    private ArrayList<Project> projectArrayList = new ArrayList<>();
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
        projectView.showProject(project,
                trackingRecordDAO.getLatestByStartDateForProjectUuid(project.getUuid()));

        projectView.renderProjectRemainingTimeFrameInfo(getTrackingConfiguration(project));
        projectView.renderProjectDurationStatistic(getTrackingConfiguration(project),
                getDurationStatisticAt(project).getDuration());


        return projectView;
    }

    private TrackingConfiguration getTrackingConfiguration(Project project) {
        return trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get();
    }

    private ProjectDuration getDurationStatisticAt(Project project) {
        return new ProjectDuration(trackingRecordDAO.getByProjectUuid(project.getUuid()));
    }
}
