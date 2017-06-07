package com.tastybug.timetracker.extension.reporting.controller.internal.model;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.Project;

import java.io.Serializable;
import java.util.Date;

public abstract class Report implements Serializable {

    private String projectTitle;
    private Date firstDay, lastDay;

    public Report(Project project, Date firstDay, Date lastDay) {
        Preconditions.checkNotNull(project);
        Preconditions.checkNotNull(firstDay);
        Preconditions.checkNotNull(lastDay);

        this.projectTitle = project.getTitle();
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public abstract String getContent();

    public abstract String getMimeType();

    public abstract String getFileExtension();

    public Date getFirstDay() {
        return firstDay;
    }

    public Date getLastDay() {
        return lastDay;
    }
}
