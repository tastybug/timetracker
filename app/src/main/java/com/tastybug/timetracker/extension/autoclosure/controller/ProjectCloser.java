package com.tastybug.timetracker.extension.autoclosure.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.task.project.update.UpdateProjectTask;

class ProjectCloser {

    private Context context;

    ProjectCloser(Context context) {
        this.context = context;
    }

    void closeProject(Project project) {
        new UpdateProjectTask(context)
                .withProjectUuid(project.getUuid())
                .withClosureState(true)
                .run();
    }
}
