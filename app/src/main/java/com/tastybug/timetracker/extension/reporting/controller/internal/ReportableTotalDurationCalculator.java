package com.tastybug.timetracker.extension.reporting.controller.internal;

import org.joda.time.Duration;

import java.util.List;

public class ReportableTotalDurationCalculator {

    public Duration getTotalForList(List<ReportableItem> reportableItems) {
        Duration total = new Duration(0);
        for (ReportableItem reportableItem : reportableItems) {
            total = total.plus(reportableItem.getDuration());
        }
        return total;
    }
}
