package com.tastybug.timetracker.task.project;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.Project;

public class ProjectCreatedEvent implements OttoEvent {

    private Project project;

    public ProjectCreatedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
