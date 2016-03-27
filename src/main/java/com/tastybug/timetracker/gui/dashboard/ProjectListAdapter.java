package com.tastybug.timetracker.gui.dashboard;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.view.ProjectView;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;

import java.util.ArrayList;

public class ProjectListAdapter extends BaseAdapter {

    private ArrayList<Project> projectArrayList = new ArrayList<Project>();
    private Activity activity;

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    public ProjectListAdapter(Activity activity) {
        projectArrayList = new ProjectDAO(activity).getAll();
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
        if( convertView == null ){
            convertView = new ProjectView(activity, null);
        }
        Project project = getProjectAt(position);
        ProjectView projectView = (ProjectView) convertView;
        projectView.showProject(project,
                trackingRecordDAO.getLatestByStartDateForProjectUuid(project.getUuid()),
                trackingRecordDAO.getByProjectUuid(project.getUuid()),
                trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get());

        projectView.renderProjectRemainingTimeFrameInfo(getTrackingConfigurationAt(position));
        projectView.renderProjectDurationStatistic(getTrackingConfigurationAt(position),
                getDurationStatisticAt(position).get());

        return projectView;
    }

    private TrackingConfiguration getTrackingConfigurationAt(int position) {
        Project project = getProjectAt(position);
        return new TrackingConfigurationDAO(activity).getByProjectUuid(project.getUuid()).get();
    }

    private StatisticProjectDuration getDurationStatisticAt(int position) {
        Project project = getProjectAt(position);
        return new StatisticProjectDuration(getTrackingConfigurationAt(position),
                new TrackingRecordDAO(activity).getByProjectUuid(project.getUuid()));

    }
}
