package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

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
                "<div class=\"data fifty\">" + getDescriptionHtml(reportableItem.getDescription()) + "</div>" +
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

    private String getDescriptionHtml(Optional<String> description) {
        if (!description.isPresent()) {
            return context.getString(R.string.report_reportable_item_no_description_placeholder);
        }
        return description.get().replaceAll("\\n", "<br/>");
    }
}
