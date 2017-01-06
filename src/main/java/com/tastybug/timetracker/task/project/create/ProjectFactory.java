package com.tastybug.timetracker.task.project.create;

import com.tastybug.timetracker.model.Project;

public class ProjectFactory {

    Project aProject(String title) {
        return new Project(title);
    }
}
