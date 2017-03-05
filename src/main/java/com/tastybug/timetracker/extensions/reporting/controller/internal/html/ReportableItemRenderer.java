package com.tastybug.timetracker.extensions.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.util.Date;

class ReportableItemRenderer {

    private Context context;
    private DefaultLocaleDateFormatter defaultLocaleDateFormatter;
    private LocalizedDurationFormatter localizedDurationFormatter;

    ReportableItemRenderer(Context context) {
        this(context, LocalizedDurationFormatter.a(context), new DefaultLocaleDateFormatter());
    }

    ReportableItemRenderer(Context context,
                           LocalizedDurationFormatter localizedDurationFormatter,
                           DefaultLocaleDateFormatter defaultLocaleDateFormatter) {
        this.context = context;
        this.localizedDurationFormatter = localizedDurationFormatter;
        this.defaultLocaleDateFormatter = defaultLocaleDateFormatter;
    }

    String render(ReportableItem reportableItem) {
        return "<div class=\"row\">" +
                "<div class=\"data twenty\">" + getTimeFrameString(reportableItem) + "</div>" +
                "<div class=\"data twenty\">" + localizedDurationFormatter.formatDuration(reportableItem.getDuration()) + "</div>" +
                "<div class=\"data fifty\">" + reportableItem.getDescription().or(context.getString(R.string.report_reportable_item_no_description_placeholder)) + "</div>" +
                "</div>";
    }

    private String getTimeFrameString(ReportableItem reportableItem) {
        Date start = reportableItem.getStartDate();
        Date end = reportableItem.getEndDate();

        if (reportableItem.isSameDay()) {
            if (reportableItem.isWholeDay()) {
                return defaultLocaleDateFormatter.dateFormat(start);
            } else {
                return defaultLocaleDateFormatter.dateFormat(start)
                        + ", "
                        + defaultLocaleDateFormatter.timeFormat(start)
                        + " - "
                        + defaultLocaleDateFormatter.timeFormat(end);
            }
        } else {
            return defaultLocaleDateFormatter.dateTimeFormat(reportableItem.getStartDate())
                    + " - "
                    + defaultLocaleDateFormatter.dateTimeFormat(reportableItem.getEndDate());
        }
    }
}
