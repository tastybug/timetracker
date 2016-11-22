package com.tastybug.timetracker.report.internal.aggregated;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.report.internal.ReportableItem;
import com.tastybug.timetracker.ui.util.DurationFormatter;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Date;

class AggregatedDay implements ReportableItem {

    private DateTime dayStart, dayEnd;

    private Duration duration = new Duration(0);

    private String aggregatedDescription;

    AggregatedDay(Date startDate) {
        Preconditions.checkNotNull(startDate);
        this.dayStart = new DateTime(startDate);
        this.dayEnd = dayStart.plusDays(1);
    }

    void addRecord(TrackingRecord trackingRecord, TrackingConfiguration configuration) {
        Preconditions.checkArgument(trackingRecord.getStart().isPresent());
        Preconditions.checkArgument(trackingRecord.getEnd().isPresent());

        if (!isOutsideOfScope(trackingRecord)) {
            if (isContainedTrackingRecord(trackingRecord)) {
                processContainedTrackingRecord(trackingRecord, configuration);
            } else {
                processLeakingTrackingRecord(trackingRecord, configuration);
            }
        }
    }

    public Date getStartDate() {
        return dayStart.toDate();
    }

    public Date getEndDate() {
        return dayEnd.toDate();
    }

    public boolean isSameDay() {
        return true;
    }

    public boolean isWholeDay() {
        return true;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean isEmpty() {
        return getDuration().getStandardSeconds() == 0;
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(aggregatedDescription);
    }

    private void processContainedTrackingRecord(TrackingRecord trackingRecord, TrackingConfiguration configuration) {
        duration = duration.plus(trackingRecord.toEffectiveDuration(configuration).get());
        addAggregatedDescription(trackingRecord);
    }

    private void processLeakingTrackingRecord(TrackingRecord trackingRecord, TrackingConfiguration configuration) {
        Date startingFrom;
        Date endingAt;
        if (isRecordStartingBeforeToday(trackingRecord)) {
            startingFrom = dayStart.toDate();
        } else {
            startingFrom = trackingRecord.getStart().get();
        }
        if (isRecordEndingAfterToday(trackingRecord)) {
            endingAt = dayEnd.toDate();
        } else {
            endingAt = trackingRecord.getEnd().get();
        }
        //
        if (!isRecordEndingAfterToday(trackingRecord)
                && configuration.hasAlteringRoundingStrategy()) {
            duration = duration.plus(getRoundingDifferenceAsDuration(trackingRecord, configuration));
        }
        duration = duration.plus(getDurationForInterval(startingFrom, endingAt));

        addAggregatedDescription(trackingRecord);
    }

    private void addAggregatedDescription(TrackingRecord trackingRecord) {
        String description = trackingRecord.getDescription().orNull();
        if (description == null) {
            return;
        }
        if (aggregatedDescription == null) {
            aggregatedDescription = description;
        } else {
            aggregatedDescription = aggregatedDescription + " -- " + description;
        }
    }

    private boolean isOutsideOfScope(TrackingRecord trackingRecord) {
        return trackingRecord.getEnd().get().before(dayStart.toDate())
                || trackingRecord.getStart().get().after(dayEnd.toDate());
    }

    private Duration getDurationForInterval(Date from, Date until) {
        return new Interval(new DateTime(from), new DateTime(until)).toDuration();
    }

    private boolean isContainedTrackingRecord(TrackingRecord trackingRecord) {
        return !isRecordStartingBeforeToday(trackingRecord)
                && !isRecordEndingAfterToday(trackingRecord);
    }

    private boolean isRecordStartingBeforeToday(TrackingRecord trackingRecord) {
        return new DateTime(trackingRecord.getStart().get()).isBefore(dayStart);
    }

    private boolean isRecordEndingAfterToday(TrackingRecord trackingRecord) {
        return new DateTime(trackingRecord.getEnd().get()).isAfter(dayEnd);
    }

    private Duration getRoundingDifferenceAsDuration(TrackingRecord trackingRecord,
                                                     TrackingConfiguration trackingConfiguration) {
        return trackingRecord.toEffectiveDuration(trackingConfiguration).get().minus(trackingRecord.toDuration().get());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("start", dayStart)
                .add("end", dayEnd)
                .add("duration", DurationFormatter.a().formatDuration(duration))
                .toString();
    }
}
