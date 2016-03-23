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
                trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get());

        return projectView;
    }
}
