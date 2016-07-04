package com.tastybug.timetracker.task.project;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ProjectConfiguredEvent implements OttoEvent {

    private String projectUuid;

    public ProjectConfiguredEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }
}
