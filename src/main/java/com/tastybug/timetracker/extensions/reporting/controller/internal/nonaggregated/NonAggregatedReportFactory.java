package com.tastybug.timetracker.extensions.reporting.controller.internal.nonaggregated;

import android.content.Context;

import com.tastybug.timetracker.extensions.reporting.controller.Report;
import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportableTotalDurationHelper;
import com.tastybug.timetracker.extensions.reporting.controller.internal.html.HtmlReport;
import com.tastybug.timetracker.extensions.reporting.controller.internal.html.HtmlReportBuilder;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class NonAggregatedReportFactory implements ReportFactory {

    private TrackingRecordToReportableMapper trackingRecordToReportableMapper = new TrackingRecordToReportableMapper();
    private ReportableTotalDurationHelper reportableTotalDurationHelper = new ReportableTotalDurationHelper();
    private HtmlReportBuilder htmlReportBuilder;

    public NonAggregatedReportFactory(Context context) throws IOException {
        this(new HtmlReportBuilder(context));
    }

    private NonAggregatedReportFactory(HtmlReportBuilder htmlReportBuilder) {
        this.htmlReportBuilder = htmlReportBuilder;
    }

    public Report create(Project project,
                         Date firstDay,
                         Date lastDay,
                         List<TrackingRecord> includedTrackingRecords,
                         List<TrackingRecord> edgeTrackingRecords,
                         TrackingConfiguration trackingConfiguration) {
        List<ReportableItem> reportables = trackingRecordToReportableMapper.mapRecords(includedTrackingRecords, trackingConfiguration);
        HtmlReport htmlReport = htmlReportBuilder.withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withReportablesList(reportables)
                .withEdgeCases(edgeTrackingRecords)
                .withTotalDuration(reportableTotalDurationHelper.getTotalForList(reportables))
                .build();

        return new Report(project, firstDay, lastDay, htmlReport);
    }
}