package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.reporting.controller.internal.TrackingRecordTimeFrameFilter;
import com.tastybug.timetracker.extension.reporting.controller.internal.csv.AggregatedCsvReportBuilder;
import com.tastybug.timetracker.extension.reporting.controller.internal.csv.CsvReport;
import com.tastybug.timetracker.extension.reporting.controller.internal.csv.CsvReportBuilder;
import com.tastybug.timetracker.extension.reporting.controller.internal.html.AggregatedHtmlReportBuilder;
import com.tastybug.timetracker.extension.reporting.controller.internal.html.HtmlReport;
import com.tastybug.timetracker.extension.reporting.controller.internal.html.HtmlReportBuilder;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;

import java.io.IOException;
import java.util.Date;

public class ReportService {

    private Context context;
    private ProjectDAO projectDAO;
    private TrackingRecordDAO trackingRecordDAO;
    private HtmlReportBuilder htmlReportBuilder;
    private AggregatedHtmlReportBuilder aggregatedHtmlReportBuilder;

    public ReportService(Context context) throws IOException {
        this.context = context;
        this.projectDAO = new ProjectDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
        this.htmlReportBuilder = new HtmlReportBuilder(context);
        this.aggregatedHtmlReportBuilder = new AggregatedHtmlReportBuilder(context);
    }

    public Report createReport(String projectUuid, Date firstDay, Date lastDay, ReportFormat format) throws IOException {
        Project project = projectDAO.get(projectUuid).get();
        TrackingRecordTimeFrameFilter filter = new TrackingRecordTimeFrameFilter()
                .withFirstDay(firstDay)
                .withLastDayInclusive(lastDay)
                .withTrackingRecordList(trackingRecordDAO.getByProjectUuid(projectUuid));

        switch(format) {
            case HTML_AGGREGATED:
                return createAggregatedHtmlReport(firstDay, lastDay, project, filter);
            case HTML_NON_AGGREGATED:
                return createHtmlReport(firstDay, lastDay, project, filter);
            case CSV_AGGREGATED:
                return createAggregatedCsvReport(firstDay, lastDay, project, filter);
            case CSV_NON_AGGREGATED:
                return createCsvReport(firstDay, lastDay, project, filter);
            default:
                throw new RuntimeException("Unexpected format requested: " + format.toString());
        }
    }

    @NonNull
    private Report createHtmlReport(Date firstDay, Date lastDay, Project project, TrackingRecordTimeFrameFilter filter) {
        HtmlReport htmlReport = htmlReportBuilder.withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withTrackingRecords(filter.build())
                .build();

        return htmlReport;
    }

    @NonNull
    private Report createAggregatedHtmlReport(Date firstDay, Date lastDay, Project project, TrackingRecordTimeFrameFilter filter) {
        HtmlReport htmlReport = aggregatedHtmlReportBuilder.withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withTrackingRecords(filter.build())
                .build();
        return htmlReport;
    }

    @NonNull
    private Report createCsvReport(Date firstDay, Date lastDay, Project project, TrackingRecordTimeFrameFilter filter) {
        CsvReport csvReport = new CsvReportBuilder(context)
                .withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withTrackingRecords(filter.build())
                .build();
        return csvReport;
    }

    @NonNull
    private Report createAggregatedCsvReport(Date firstDay, Date lastDay, Project project, TrackingRecordTimeFrameFilter filter) {
        CsvReport csvReport = new AggregatedCsvReportBuilder(context)
                .withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withTrackingRecords(filter.build())
                .build();
        return csvReport;
    }
}
