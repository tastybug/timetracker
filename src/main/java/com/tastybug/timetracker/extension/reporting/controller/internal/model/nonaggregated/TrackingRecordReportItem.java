package com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Date;

class TrackingRecordReportItem implements ReportableItem {

    public java.util.Date start, end;
    public Optional<String> description;
    public Duration duration;

    TrackingRecordReportItem(TrackingRecord trackingRecord) {
        this.start = trackingRecord.getStart().get();
        this.end = trackingRecord.getEnd().get();
        this.duration = trackingRecord.toEffectiveDuration().get();
        this.description = trackingRecord.getDescription();
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public Date getEndDate() {
        return end;
    }

    public Date getStartDate() {
        return start;
    }

    public boolean isSameDay() {
        return new LocalDate(getStartDate()).isEqual(new LocalDate(getEndDate()));
    }

    public boolean isWholeDay() {
        return false;
    }
}
