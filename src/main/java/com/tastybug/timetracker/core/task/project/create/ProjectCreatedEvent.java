package com.tastybug.timetracker.core.task.project.create;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ProjectCreatedEvent implements OttoEvent {

    private Project project;

    ProjectCreatedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("project", getProject())
                .toString();
    }

}
