package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableTotalDurationCalculator;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated.TrackingRecordToReportableMapper;

import org.joda.time.Duration;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HtmlReportBuilder {

    private TrackingRecordToReportableMapper trackingRecordToReportableMapper = new TrackingRecordToReportableMapper();
    private ReportableTotalDurationCalculator reportableTotalDurationCalculator;
    private ReportableListRenderer reportableListRenderer;
    private TitleGenerator titleGenerator;
    private LocalizedDurationFormatter localizedDurationFormatter;

    protected Context context;
    protected Project project;
    protected Date firstDay, lastDay;
    private List<TrackingRecord> trackingRecordList = Collections.emptyList();

    public HtmlReportBuilder(Context context) throws IOException {
        this(context,
                new ReportableListRenderer(context),
                new TitleGenerator(context),
                new LocalizedDurationFormatter(context),
                new ReportableTotalDurationCalculator());
    }

    HtmlReportBuilder(Context context,
                      ReportableListRenderer reportableListRenderer,
                      TitleGenerator titleGenerator,
                      LocalizedDurationFormatter localizedDurationFormatter,
                      ReportableTotalDurationCalculator reportableTotalDurationCalculator) {
        this.context = context;
        this.reportableListRenderer = reportableListRenderer;
        this.titleGenerator = titleGenerator;
        this.localizedDurationFormatter = localizedDurationFormatter;
        this.reportableTotalDurationCalculator = reportableTotalDurationCalculator;
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

    public HtmlReportBuilder withTrackingRecords(List<TrackingRecord> trackingRecordList) {
        this.trackingRecordList = trackingRecordList;
        return this;
    }

    public HtmlReport build() {
        List<ReportableItem> reportableItems = generateReportables(trackingRecordList);
        Duration totalDuration = computeTotalDuration(reportableItems);

        HtmlReport htmlReport = new HtmlReport(context, project, firstDay, lastDay);

        htmlReport.insertReportablesList(reportableListRenderer.renderReportablesList(reportableItems));
        htmlReport.insertReportTitle(titleGenerator.getTitle(project, firstDay, lastDay));
        htmlReport.insertProjectTitle(project);
        htmlReport.insertProjectDescription(project);
        htmlReport.insertContractId(project);
        htmlReport.insertTotalDuration(localizedDurationFormatter.formatDuration(totalDuration));
        htmlReport.localizeHeaders();

        return htmlReport;
    }

    protected List<ReportableItem> generateReportables(List<TrackingRecord> trackingRecordList) {
        return trackingRecordToReportableMapper.mapRecords(trackingRecordList);
    }

    private Duration computeTotalDuration(List<ReportableItem> reportableItems) {
        return reportableTotalDurationCalculator.getTotalForList(reportableItems);
    }
}
