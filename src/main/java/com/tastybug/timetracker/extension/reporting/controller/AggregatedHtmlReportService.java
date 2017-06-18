package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.html.AggregatedHtmlReportFactory;

import java.io.IOException;

public class AggregatedHtmlReportService extends AbstractReportService {

    private AggregatedHtmlReportFactory aggregatedHtmlReportFactory;

    public AggregatedHtmlReportService(Context context) throws IOException {
        super(context);
        this.aggregatedHtmlReportFactory = new AggregatedHtmlReportFactory(context);
    }

    @Override
    ReportFactory getReportFactory() {
        return aggregatedHtmlReportFactory;
    }
}
