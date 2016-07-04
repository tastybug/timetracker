package com.tastybug.timetracker.trackingplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TrackingPlayerModel {

    private static final String PAUSED_PROJECTS_PREF_KEY = "PAUSED_PROJECTS_PREF_KEY";

    private Context context;
    private ProjectDAO projectDAO;
    private TrackingRecordDAO trackingRecordDAO;

    public TrackingPlayerModel(Context context) {
        this.context = context;
        this.projectDAO = new ProjectDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public ArrayList<Project> getRunningProjectList() {
        ArrayList<Project> runningProjects = new ArrayList<>();
        for (TrackingRecord record : trackingRecordDAO.getRunning()) {
            runningProjects.add(projectDAO.get(record.getProjectUuid()).get());
        }

        return runningProjects;
    }

    public ArrayList<Project> getPausedProjectList() {
        ArrayList<Project> pausedProjects = new ArrayList<>();
        for (String pausedProjectUuid : getPausedProjectUuidSet()) {
            pausedProjects.add(projectDAO.get(pausedProjectUuid).get());
        }
        return  pausedProjects;
    }

    public Set<String> getPausedProjectUuidSet() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TrackingPlayerSettings", Context.MODE_PRIVATE);
        return new HashSet<>(sharedPreferences.getStringSet(PAUSED_PROJECTS_PREF_KEY, new HashSet<String>()));
    }

    public void addPausedProject() {

    }

    public void removePausedProject() {

    }

}
