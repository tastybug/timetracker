package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated.AggregatedReportablesProvider;

import java.util.List;

public class AggregatedCsvReportFactory extends CsvReportFactory {

    private AggregatedReportablesProvider aggregatedReportablesProvider = new AggregatedReportablesProvider();

    public AggregatedCsvReportFactory(Context context) {
        super(context);
    }

    @Override
    protected List<ReportableItem> generateReportables(List<TrackingRecord> trackingRecordList) {
        return aggregatedReportablesProvider.getReportables(super.firstDay, super.lastDay, trackingRecordList);
    }
}
