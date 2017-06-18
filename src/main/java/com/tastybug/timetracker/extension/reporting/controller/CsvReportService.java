package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.csv.CsvReportFactory;

import java.io.IOException;

public class CsvReportService extends AbstractReportService {

    private CsvReportFactory csvReportFactory;

    public CsvReportService(Context context) throws IOException {
        super(context);
        this.csvReportFactory = new CsvReportFactory(context);
    }

    @Override
    ReportFactory getReportFactory() {
        return csvReportFactory;
    }
}
