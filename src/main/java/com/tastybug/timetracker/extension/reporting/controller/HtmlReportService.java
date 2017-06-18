package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.html.HtmlReportFactory;

public class HtmlReportService extends AbstractReportService {

    private HtmlReportFactory htmlReportFactory;

    public HtmlReportService(Context context) {
        this(new ProjectDAO(context),
                new TrackingRecordFilteringService(context),
                new HtmlReportFactory(context));
    }

    HtmlReportService(ProjectDAO projectDAO,
                      TrackingRecordFilteringService trackingRecordDAO,
                      HtmlReportFactory factory) {
        super(projectDAO, trackingRecordDAO);
        this.htmlReportFactory = factory;
    }

    @Override
    ReportFactory getReportFactory() {
        return htmlReportFactory;
    }
}
