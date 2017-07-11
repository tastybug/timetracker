package com.tastybug.timetracker.core.task.project.update;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.project.ProjectChangeIntent;

import static com.tastybug.timetracker.core.task.project.ProjectChangeIntent.Type.UPDATE;

public class UpdateProjectEvent implements LifecycleEvent {

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

    @Override
    public Intent getAsBroadcastEvent() {
        return new ProjectChangeIntent(projectUuid, UPDATE);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .toString();
    }
}
