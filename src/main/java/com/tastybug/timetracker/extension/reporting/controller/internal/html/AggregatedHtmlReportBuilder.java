package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated.AggregatedReportablesProvider;

import java.io.IOException;
import java.util.List;

public class AggregatedHtmlReportBuilder extends HtmlReportBuilder {

    private AggregatedReportablesProvider aggregatedReportablesProvider = new AggregatedReportablesProvider();

    public AggregatedHtmlReportBuilder(Context context) throws IOException {
        super(context);
    }

    @Override
    protected List<ReportableItem> generateReportables(List<TrackingRecord> trackingRecordList) {
        return aggregatedReportablesProvider.getReportables(super.firstDay, super.lastDay, trackingRecordList);
    }
}
