package com.tastybug.timetracker.report.internal.html;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.report.internal.ReportableItem;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HtmlReportBuilder {

    private Project project;
    private Date firstDay, lastDay;
    private List<ReportableItem> reportableItems = Collections.emptyList();
    private List<TrackingRecord> edgeCaseTrackingRecords = Collections.emptyList();

    private ReportableListRenderer reportableListRenderer;
    private TitleGenerator titleGenerator;
    private HtmlReport htmlReport;

    public HtmlReportBuilder(Context context) throws IOException {
        reportableListRenderer = new ReportableListRenderer(context);
        htmlReport = new HtmlReport(context);
        titleGenerator = new TitleGenerator(context);
    }

    HtmlReportBuilder(ReportableListRenderer reportableListRenderer,
                      TitleGenerator titleGenerator,
                      HtmlReport htmlReport) {
        this.reportableListRenderer = reportableListRenderer;
        this.titleGenerator = titleGenerator;
        this.htmlReport = htmlReport;
    }

    public HtmlReportBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public HtmlReportBuilder withTimeFrame(Date firstDay, Date lastDay) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        return this;
    }

    public HtmlReportBuilder withReportablesList(List<ReportableItem> reportableItems) {
        this.reportableItems = reportableItems;
        return this;
    }

    public HtmlReportBuilder withEdgeCases(List<TrackingRecord> trackingRecords) {
        this.edgeCaseTrackingRecords = trackingRecords;
        return this;
    }

    public HtmlReport build() {
        htmlReport.insertReportablesList(reportableListRenderer.renderReportablesList(reportableItems));
        htmlReport.insertReportTitle(titleGenerator.getTitle(project, firstDay, lastDay));
        htmlReport.insertProjectTitle(project);
        htmlReport.insertProjectDescription(project);
        htmlReport.localizeHeaders();

        return htmlReport;
    }
}
