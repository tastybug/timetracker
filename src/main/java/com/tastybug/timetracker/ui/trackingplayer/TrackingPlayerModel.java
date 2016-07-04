package com.tastybug.timetracker.ui.trackingplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<Project> getSortedRunningProjectList() {
        ArrayList<Project> runningProjects = new ArrayList<>();
        for (TrackingRecord record : trackingRecordDAO.getRunning()) {
            runningProjects.add(projectDAO.get(record.getProjectUuid()).get());
        }

        Collections.sort(runningProjects);
        return runningProjects;
    }

    public ArrayList<Project> getSortedPausedProjectList() {
        ArrayList<Project> pausedProjects = new ArrayList<>();
        for (String pausedProjectUuid : getPausedProjectUuidSet()) {
            pausedProjects.add(projectDAO.get(pausedProjectUuid).get());
        }

        Collections.sort(pausedProjects);
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
