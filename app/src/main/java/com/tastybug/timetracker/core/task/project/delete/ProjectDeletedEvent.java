package com.tastybug.timetracker.core.task.project.delete;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.project.ProjectChangeIntent;

import static com.tastybug.timetracker.core.task.project.ProjectChangeIntent.Type.DELETE;

public class ProjectDeletedEvent implements LifecycleEvent {

    private String projectUuid;

    ProjectDeletedEvent(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new ProjectChangeIntent(projectUuid, DELETE);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .toString();
    }
}
