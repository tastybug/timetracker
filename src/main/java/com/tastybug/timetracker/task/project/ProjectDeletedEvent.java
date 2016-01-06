package com.tastybug.timetracker.task.project;

import com.tastybug.timetracker.task.OttoEvent;

public class ProjectDeletedEvent implements OttoEvent {

    private String projectUuid;

    public ProjectDeletedEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }
}
