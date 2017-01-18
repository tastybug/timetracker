package com.tastybug.timetracker.task.project.delete;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ProjectDeletedEvent implements OttoEvent {

    private String projectUuid;

    ProjectDeletedEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .toString();
    }
}
