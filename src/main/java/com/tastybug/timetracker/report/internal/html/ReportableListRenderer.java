package com.tastybug.timetracker.report.internal.html;

import android.content.Context;

import com.tastybug.timetracker.report.internal.ReportableItem;

import java.util.List;

class ReportableListRenderer {

    private ReportableItemRenderer reportableItemRenderer;

    ReportableListRenderer(Context context) {
        this.reportableItemRenderer = new ReportableItemRenderer(context);
    }

    ReportableListRenderer(ReportableItemRenderer reportableItemRenderer) {
        this.reportableItemRenderer = reportableItemRenderer;
    }

    String renderReportablesList(List<ReportableItem> reportableItems) {
        StringBuilder html = new StringBuilder();
        for (ReportableItem reportableItem : reportableItems) {
            html.append(reportableItemRenderer.render(reportableItem));
        }

        return html.toString();
    }
}
