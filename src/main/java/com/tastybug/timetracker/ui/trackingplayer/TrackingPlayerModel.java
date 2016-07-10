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

    private static final String PAUSED_PROJECTS_KEY = "PAUSED_PROJECTS_KEY";

    private Context context;
    private ProjectDAO projectDAO;
    private TrackingRecordDAO trackingRecordDAO;

    public TrackingPlayerModel(Context context) {
        this.context = context;
        this.projectDAO = new ProjectDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public ArrayList<Project> getSortedRunningAndPausedProjectList() {
        ArrayList<Project> runningProjects = new ArrayList<>();
        for (TrackingRecord record : trackingRecordDAO.getRunning()) {
            runningProjects.add(projectDAO.get(record.getProjectUuid()).get());
        }
        for (String pausedProjectUuid : getPausedProjectUuidSet()) {
            runningProjects.add(projectDAO.get(pausedProjectUuid).get());
        }


        Collections.sort(runningProjects);
        return runningProjects;
    }

    public void addPausedProject(String projectUuid) {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> pausedList = prefs.getStringSet(PAUSED_PROJECTS_KEY, new HashSet<String>());
        pausedList.add(projectUuid);
        editor.putStringSet(PAUSED_PROJECTS_KEY, pausedList);
        editor.apply();
    }

    public void removePausedProject(String projectUuid) {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> pausedList = prefs.getStringSet(PAUSED_PROJECTS_KEY, new HashSet<String>());
        pausedList.remove(projectUuid);
        editor.putStringSet(PAUSED_PROJECTS_KEY, pausedList);
        editor.apply();
    }

    public boolean isProjectPaused(String projectUuid) {
        SharedPreferences prefs = getPreferences();
        return prefs.getStringSet(PAUSED_PROJECTS_KEY, new HashSet<String>()).contains(projectUuid);
    }

    private Set<String> getPausedProjectUuidSet() {
        return getPreferences().getStringSet(PAUSED_PROJECTS_KEY, new HashSet<String>());
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences("PausedProjectsManager", Context.MODE_PRIVATE);
    }

}
