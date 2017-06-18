package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.csv.AggregatedCsvReportFactory;

import java.io.IOException;

public class AggregatedCsvReportService extends AbstractReportService {

    private AggregatedCsvReportFactory aggregatedCsvReportFactory;

    public AggregatedCsvReportService(Context context) throws IOException {
        super(context);
        this.aggregatedCsvReportFactory = new AggregatedCsvReportFactory(context);
    }

    @Override
    ReportFactory getReportFactory() {
        return aggregatedCsvReportFactory;
    }
}
