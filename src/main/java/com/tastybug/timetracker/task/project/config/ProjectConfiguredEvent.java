package com.tastybug.timetracker.task.project.config;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ProjectConfiguredEvent implements OttoEvent {

    private String projectUuid;

    ProjectConfiguredEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }
}
