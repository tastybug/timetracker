package com.tastybug.timetracker.core.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ProjectListAdapter extends BaseAdapter {

    private List<Project> projectArrayList = new ArrayList<>();
    private Context context;

    private TrackingRecordDAO trackingRecordDAO;

    ProjectListAdapter(Activity activity) {
        createModel(activity);
        this.context = activity;

        this.trackingRecordDAO = new TrackingRecordDAO(activity);
    }

    private void createModel(Context context) {
        projectArrayList = new ProjectDAO(context).getAll();
        Collections.sort(projectArrayList, new ProjectComparator());
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
       createModel(context);
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
            convertView = new ProjectView(context, null);
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
