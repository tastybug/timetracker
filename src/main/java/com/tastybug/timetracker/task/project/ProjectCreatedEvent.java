package com.tastybug.timetracker.task.project;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoEvent;

public class ProjectCreatedEvent implements OttoEvent {

    private Project project;

    public ProjectCreatedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
