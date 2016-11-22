package com.tastybug.timetracker.task.project;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ProjectDeletedEvent implements OttoEvent {

    private String projectUuid;

    ProjectDeletedEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }
}
