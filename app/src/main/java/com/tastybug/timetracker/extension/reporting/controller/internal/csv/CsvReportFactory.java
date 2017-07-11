package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated.TrackingRecordToReportableMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CsvReportFactory implements ReportFactory {

    private Context context;
    protected Date firstDay, lastDay;
    private TrackingRecordToReportableMapper trackingRecordToReportableMapper = new TrackingRecordToReportableMapper();

    public CsvReportFactory(Context context) {
        this.context = context;
    }


    public CsvReport createReport(Project project, Date firstDay, Date lastDay, List<TrackingRecord> trackingRecords) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        CsvReport csvReport = new CsvReport(context, project, firstDay, lastDay);
        List<ReportableItem> reportableItems = generateReportables(trackingRecords);

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

