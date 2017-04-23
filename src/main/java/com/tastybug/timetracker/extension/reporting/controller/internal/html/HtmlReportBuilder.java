package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import org.joda.time.Duration;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HtmlReportBuilder {

    private ReportableListRenderer reportableListRenderer;
    private TitleGenerator titleGenerator;
    private LocalizedDurationFormatter localizedDurationFormatter;

    private Project project;
    private Date firstDay, lastDay;
    private List<ReportableItem> reportableItems = Collections.emptyList();
    private List<TrackingRecord> edgeCaseTrackingRecords = Collections.emptyList();
    private Duration totalDuration = new Duration(0);

    private HtmlReport htmlReport;

    public HtmlReportBuilder(Context context) throws IOException {
        this(new ReportableListRenderer(context),
                new TitleGenerator(context),
                new LocalizedDurationFormatter(context),
                new HtmlReport(context));
    }

    HtmlReportBuilder(ReportableListRenderer reportableListRenderer,
                      TitleGenerator titleGenerator,
                      LocalizedDurationFormatter localizedDurationFormatter,
                      HtmlReport htmlReport) {
        this.reportableListRenderer = reportableListRenderer;
        this.titleGenerator = titleGenerator;
        this.localizedDurationFormatter = localizedDurationFormatter;
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

    public HtmlReportBuilder withTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
        return this;
    }

    public HtmlReport build() {
        htmlReport.insertReportablesList(reportableListRenderer.renderReportablesList(reportableItems));
        htmlReport.insertReportTitle(titleGenerator.getTitle(project, firstDay, lastDay));
        htmlReport.insertProjectTitle(project);
        htmlReport.insertProjectDescription(project);
        htmlReport.insertContractId(project);
        htmlReport.insertTotalDuration(localizedDurationFormatter.formatDuration(totalDuration));
        htmlReport.localizeHeaders();

        return htmlReport;
    }
}
