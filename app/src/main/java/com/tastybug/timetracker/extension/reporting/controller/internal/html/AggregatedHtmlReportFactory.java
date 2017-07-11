package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated.AggregatedReportablesProvider;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AggregatedHtmlReportFactory extends HtmlReportFactory {

    private AggregatedReportablesProvider aggregatedReportablesProvider = new AggregatedReportablesProvider();

    public AggregatedHtmlReportFactory(Context context) throws IOException {
        super(context);
    }

    @Override
    protected List<ReportableItem> generateReportables(Project project,
                                                       Date firstDay,
                                                       Date lastDay,
                                                       List<TrackingRecord> trackingRecordList) {
        return aggregatedReportablesProvider.getReportables(firstDay, lastDay, trackingRecordList);
    }
}
