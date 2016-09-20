package com.tastybug.timetracker.report;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.report.internal.html.HtmlReport;

import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {

    private Project project;
    private Date firstDay, lastDay;
    private HtmlReport htmlReport;

    public Report(Project project, Date firstDay, Date lastDay, HtmlReport htmlReport) {
        Preconditions.checkNotNull(project);
        Preconditions.checkNotNull(firstDay);
        Preconditions.checkNotNull(lastDay);
        Preconditions.checkNotNull(htmlReport);

        this.project = project;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.htmlReport = htmlReport;
    }

    public String getProjectTitle() {
        return project.getTitle();
    }

    public String getHtml() {
        return htmlReport.toHtml();
    }

    public Date getFirstDay() {
        return firstDay;
    }

    public Date getLastDay() {
        return lastDay;
    }
}
