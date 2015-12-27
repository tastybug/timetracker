package com.tastybug.timetracker.gui.projects;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tastybug.timetracker.gui.view.ProjectView;
import com.tastybug.timetracker.model.Project;

import java.util.ArrayList;

public class ProjectListAdapter extends BaseAdapter {

    private ArrayList<Project> projectArrayList = new ArrayList<Project>();

    private Activity activity;

    public ProjectListAdapter(Activity activity) {
        projectArrayList.add(new Project("project 1"));
        projectArrayList.add(new Project("project 2"));
        projectArrayList.add(new Project("project 3"));

        this.activity = activity;
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

        ProjectView projectView = (ProjectView) convertView;
        projectView.showProject(getProjectAt(position));

        return projectView;
    }
}
