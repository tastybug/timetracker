package com.tastybug.timetracker.extension.autoclosure.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;

import java.util.List;

public class AutoClosureService {

    private ProjectDAO projectDAO;
    private ClosabilityIndicator closabilityIndicator;
    private ProjectCloser projectCloser;

    public AutoClosureService(Context context) {
        this(new ProjectDAO(context),
                new ClosabilityIndicator(context),
                new ProjectCloser(context));
    }

    AutoClosureService(ProjectDAO projectDAO,
                       ClosabilityIndicator closabilityIndicator,
                       ProjectCloser projectCloser) {
        this.projectDAO = projectDAO;
        this.closabilityIndicator = closabilityIndicator;
        this.projectCloser = projectCloser;
    }

    public void performGlobalAutoClose() {
        List<Project> projects = projectDAO.getAll();
        for (Project project : projects) {
            if (closabilityIndicator.isProjectClosable(project)) {
                projectCloser.closeProject(project);
            }
        }
    }
}
