package com.tastybug.timetracker.report.internal;

import org.joda.time.Duration;

import java.util.List;

public class ReportableTotalDurationHelper {

    public ReportableTotalDurationHelper() {
    }

    public Duration getTotalForList(List<ReportableItem> reportableItems) {
        Duration total = new Duration(0);
        for (ReportableItem reportableItem : reportableItems) {
            total = total.plus(reportableItem.getDuration());
        }
        return total;
    }
}
