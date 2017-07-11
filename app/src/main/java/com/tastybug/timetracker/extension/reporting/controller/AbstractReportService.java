package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;

import java.util.Date;
import java.util.List;

abstract class AbstractReportService {

    private ProjectDAO projectDAO;
    private TrackingRecordFilteringService filteringService;

    AbstractReportService(Context context) {
        this(new ProjectDAO(context),
             new TrackingRecordFilteringService(context));
    }

    AbstractReportService(ProjectDAO projectDAO,
                          TrackingRecordFilteringService filteringService) {
        this.projectDAO = projectDAO;
        this.filteringService = filteringService;
    }

    public Report createReport(String projectUuid, Date notBefore, Date notAfter) {
        Project project = getProject(projectUuid);
        List<TrackingRecord> recordsInTimeFrame = filteringService.getFilteredByTimeFrame(projectUuid, notBefore, notAfter);

        return getReportFactory().createReport(project, notBefore, notAfter, recordsInTimeFrame);

    }

    abstract ReportFactory getReportFactory();

    private Project getProject(String projectUuid) {
        return projectDAO.get(projectUuid).get();
    }
}
