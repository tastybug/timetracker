package com.tastybug.timetracker.core.model;

import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.util.DateProvider;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;
import java.util.UUID;

public class TrackingRecord extends Entity implements Comparable<TrackingRecord> {

    private static final int MINUTES_LIMIT_FOR_TINY_RECORDS = 2;

    private transient DateProvider dateProvider = new DateProvider();

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Date start, end;
    private String description;


    public TrackingRecord() {
    }

    public TrackingRecord(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public TrackingRecord(String uuid,
                          String projectUuid,
                          Optional<Date> start,
                          Optional<Date> end,
                          Optional<String> description) {
        this.uuid = uuid;
        this.projectUuid = projectUuid;
        this.start = start.orNull();
        this.end = end.orNull();
        this.description = description.orNull();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        Preconditions.checkNotNull(projectUuid);
        this.projectUuid = projectUuid;
    }

    public void start() {
        if (getStart().isPresent()) {
            throw new IllegalStateException(toString() + " is already started!");
        }
        this.start = dateProvider.getCurrentDate();
    }

    public Optional<Date> getStart() {
        return Optional.fromNullable(start);
    }

    public void setStart(Date start) {
        Preconditions.checkNotNull(start);
        if (getEnd().isPresent()) {
            Preconditions.checkArgument(start.before(getEnd().get()), "Start date must be before end date!");
        }
        this.start = start;
    }

    public void stop() {
        if (!getStart().isPresent()) {
            throw new IllegalStateException(toString() + " is not started yet!");
        }
        if (getEnd().isPresent()) {
            throw new IllegalStateException(toString() + " is already stopped!");
        }
        this.end = dateProvider.getCurrentDate();
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public void setEnd(Date end) {
        Preconditions.checkNotNull(end);
        if (getStart().isPresent()) {
            Preconditions.checkArgument(end.after(getStart().get()), "End date must be after start date!");
        }
        this.end = end;
    }

    public boolean isRunning() {
        return getStart().isPresent() && !getEnd().isPresent();
    }

    public boolean isFinished() {
        return getStart().isPresent() && getEnd().isPresent();
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(description);
    }

    public void setDescription(Optional<String> description) {
        if (description.isPresent() && description.get().length() == 0) {
            this.description = null;
        } else {
            this.description = description.orNull();
        }
    }

    public Optional<Duration> toDuration() {
        // when creating the duration, shave off additional millis as it confuses the duration
        // calculation, resulting in e.g. a 5 minutes duration coming out as 4:59
        if (isRunning()) {
            return Optional.of(new Duration(new DateTime(start).withMillisOfSecond(0), new DateTime(dateProvider.getCurrentDate()).withMillisOfSecond(0)));
        } else if (isFinished()) {
            return Optional.of(new Duration(new DateTime(start).withMillisOfSecond(0), new DateTime(end).withMillisOfSecond(0)));
        } else {
            return Optional.absent();
        }
    }

    boolean isVeryShort() {
        return toDuration().isPresent() && toDuration().get().getStandardMinutes() <= MINUTES_LIMIT_FOR_TINY_RECORDS;
    }

    public Optional<Duration> toEffectiveDuration(TrackingConfiguration configuration) {
        Optional<Duration> durationOpt = toDuration();
        if (!durationOpt.isPresent()) {
            return Optional.absent();
        } else {
            return Optional.of(new Duration(configuration.getRoundingStrategy().getStrategy().getEffectiveDurationInSeconds(durationOpt.get()) * 1000));
        }
    }

    void setDateProvider(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TrackingRecord && getUuid().equals(((TrackingRecord) o).getUuid());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("projectUuid", getProjectUuid())
                .add("start", getStart().orNull())
                .add("end", getEnd().orNull())
                .add("description", getDescription().orNull())
                .toString();
    }

    public int compareTo(@NonNull TrackingRecord another) {
        Preconditions.checkState(another.getStart().isPresent());
        Preconditions.checkState(this.getStart().isPresent());
        return another.getStart().get().compareTo(getStart().get());
    }
}
