package com.tastybug.timetracker.core.task.project.update;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class UpdateProjectEvent implements OttoEvent {

    private String projectUuid;
    private boolean closure = false;

    UpdateProjectEvent(String projectUuid, boolean closure) {
        this.projectUuid = projectUuid;
        this.closure = closure;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public boolean isClosure() {
        return closure;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .toString();
    }
}
