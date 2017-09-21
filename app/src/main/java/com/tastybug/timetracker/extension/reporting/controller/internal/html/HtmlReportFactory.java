package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableTotalDurationCalculator;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated.TrackingRecordToReportableMapper;

import org.joda.time.Duration;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class HtmlReportFactory implements ReportFactory {

    private TrackingRecordToReportableMapper trackingRecordToReportableMapper = new TrackingRecordToReportableMapper();
    private ReportableTotalDurationCalculator reportableTotalDurationCalculator;
    private ReportableListRenderer reportableListRenderer;
    private TitleGenerator titleGenerator;
    private LocalizedDurationFormatter localizedDurationFormatter;

    protected Context context;
    protected Project project;
    protected Date firstDay, lastDay;

    public HtmlReportFactory(Context context) {
        this(context,
                new ReportableListRenderer(context),
                new TitleGenerator(context),
                new LocalizedDurationFormatter(context),
                new ReportableTotalDurationCalculator());
    }

    private HtmlReportFactory(Context context,
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

    public HtmlReport createReport(Project project, Date notBefore, Date notAfter, List<TrackingRecord> trackingRecords) {
        List<ReportableItem> reportableItems = generateReportables(project, notBefore, notAfter, trackingRecords);
        Duration totalDuration = computeTotalDuration(reportableItems);

        HtmlReport htmlReport = new HtmlReport(context, getHtmlTemplate(context), project, notBefore, notAfter);

        htmlReport.insertReportablesList(reportableListRenderer.renderReportablesList(reportableItems));
        htmlReport.insertReportTitle(titleGenerator.getTitle(project, notBefore, notAfter));
        htmlReport.insertProjectTitle(project);
        htmlReport.insertProjectDescription(project);
        htmlReport.insertContractId(project);
        htmlReport.insertTotalDuration(localizedDurationFormatter.formatDuration(totalDuration));
        htmlReport.localizeHeaders();

        return htmlReport;
    }

    protected List<ReportableItem> generateReportables(Project project,
                                                       Date firstDay,
                                                       Date lastDay,
                                                       List<TrackingRecord> trackingRecordList) {
        return trackingRecordToReportableMapper.mapRecords(trackingRecordList);
    }

    private Duration computeTotalDuration(List<ReportableItem> reportableItems) {
        return reportableTotalDurationCalculator.getTotalForList(reportableItems);
    }

    private String getHtmlTemplate(Context context) {
        try {
            return new TemplateAssetProvider(context.getAssets()).getReportTemplate();
        } catch (IOException e) {
            throw new RuntimeException("Problem accessing report template.", e);
        }
    }
}