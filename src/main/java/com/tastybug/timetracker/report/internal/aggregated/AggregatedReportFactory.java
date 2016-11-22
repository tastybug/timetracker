package com.tastybug.timetracker.report.internal.aggregated;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.report.Report;
import com.tastybug.timetracker.report.internal.ReportFactory;
import com.tastybug.timetracker.report.internal.ReportableItem;
import com.tastybug.timetracker.report.internal.ReportableTotalDurationHelper;
import com.tastybug.timetracker.report.internal.html.HtmlReport;
import com.tastybug.timetracker.report.internal.html.HtmlReportBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AggregatedReportFactory implements ReportFactory {

    private DayListFactory dayListFactory = new DayListFactory();
    private TrackingRecordToAggregatedDayMapper trackingRecordToAggregatedDayMapper = new TrackingRecordToAggregatedDayMapper();
    private ReportableTotalDurationHelper reportableTotalDurationHelper = new ReportableTotalDurationHelper();
    private HtmlReportBuilder htmlReportBuilder;

    public AggregatedReportFactory(Context context) throws IOException {
        htmlReportBuilder = new HtmlReportBuilder(context);
    }

    public Report create(Project project, Date firstDay,
                         Date lastDay,
                         List<TrackingRecord> includedTrackingRecords,
                         List<TrackingRecord> edgeTrackingRecords,
                         TrackingConfiguration trackingConfiguration) {
        List<AggregatedDay> aggregatedDayList = dayListFactory.createList(firstDay, lastDay);
        aggregatedDayList = trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(aggregatedDayList, includedTrackingRecords, trackingConfiguration);
        List<ReportableItem> reportableItems = filterEmptyDays(aggregatedDayList);
        HtmlReport htmlReport = htmlReportBuilder.withProject(project)
                .withTimeFrame(firstDay, lastDay)
                .withReportablesList(reportableItems)
                .withEdgeCases(edgeTrackingRecords)
                .withTotalDuration(reportableTotalDurationHelper.getTotalForList(reportableItems))
                .build();

        return new Report(project, firstDay, lastDay, htmlReport);
    }

    private List<ReportableItem> filterEmptyDays(List<AggregatedDay> aggregatedDays) {
        ArrayList<ReportableItem> results = new ArrayList<>();
        for (ReportableItem reportable : aggregatedDays) {
            if (reportable.getDuration().getMillis() > 0) {
                results.add(reportable);
            }
        }
        return results;
    }
}
