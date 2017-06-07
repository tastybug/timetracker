package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated.TrackingRecordToReportableMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CsvReportBuilder {

    private Context context;
    private Project project;
    protected Date firstDay, lastDay;
    private TrackingRecordToReportableMapper trackingRecordToReportableMapper = new TrackingRecordToReportableMapper();
    private List<TrackingRecord> trackingRecordList = Collections.emptyList();

    public CsvReportBuilder(Context context) {
        this.context = context;
    }

    public CsvReportBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public CsvReportBuilder withTimeFrame(Date firstDay, Date lastDay) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        return this;
    }

    public CsvReportBuilder withTrackingRecords(List<TrackingRecord> trackingRecordList) {
        this.trackingRecordList = trackingRecordList;
        return this;
    }

    public CsvReport build() {
        CsvReport csvReport = new CsvReport(context, project, firstDay, lastDay);
        List<ReportableItem> reportableItems = generateReportables(trackingRecordList);

        try {
            csvReport.writeHeaders();
            for (ReportableItem item : reportableItems) {
                csvReport.addReportableItem(project, item);
            }
            csvReport.close();
            return csvReport;
        } catch (IOException ioe) {
            throw new RuntimeException("Problem creating csv report.", ioe);
        }
    }

    protected List<ReportableItem> generateReportables(List<TrackingRecord> trackingRecordList) {
        return trackingRecordToReportableMapper.mapRecords(trackingRecordList);
    }

}

