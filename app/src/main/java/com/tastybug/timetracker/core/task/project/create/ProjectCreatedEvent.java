package com.tastybug.timetracker.core.task.project.create;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.project.ProjectChangeIntent;

import static com.tastybug.timetracker.core.task.project.ProjectChangeIntent.Type.CREATE;

public class ProjectCreatedEvent implements LifecycleEvent {

    private Project project;

    public ProjectCreatedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new ProjectChangeIntent(project.getUuid(), CREATE);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("project", getProject())
                .toString();
    }
}
