package com.tastybug.timetracker.model;

import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.joda.time.Duration;

import java.util.Date;
import java.util.UUID;

public class TrackingRecord extends Entity implements Comparable<TrackingRecord> {

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Date start, end;
    private String description;


    public TrackingRecord() {}

    public TrackingRecord(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public TrackingRecord(String uuid, String projectUuid, Date start, Date end, String description) {
        this.uuid = uuid;
        this.projectUuid = projectUuid;
        this.start = start;
        this.end = end;
        this.description = description;
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
        this.start = new Date();
    }

    public Optional<Date> getStart() {
        return Optional.fromNullable(start);
    }

    public void setStart(Date start) {
        Preconditions.checkNotNull(start);
        this.start = start;
    }

    public void stop() {
        if (!getStart().isPresent()) {
            throw new IllegalStateException(toString() + " is not started yet!");
        }
        if (getEnd().isPresent()) {
            throw new IllegalStateException(toString() + " is already stopped!");
        }
        this.end = new Date();
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public void setEnd(Date end) {
        Preconditions.checkNotNull(end);
        this.end = end;
    }

    public boolean isRunning() {
        return getStart().isPresent() && !getEnd().isPresent();
    }

    public boolean isFinished() {
        return getStart().isPresent() && getEnd().isPresent();
    }

    public void setDescription(Optional<String> description) {
        this.description = description.orNull();
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(TextUtils.isEmpty(description) ? null : description);
    }

    public Optional<Duration> toDuration() {
        if (isRunning()) {
            return Optional.of(new Duration(start.getTime(), new Date().getTime()));
        } else if (isFinished()){
            return Optional.of(new Duration(start.getTime(), end.getTime()));
        } else {
            return Optional.absent();
        }
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

    public int compareTo(TrackingRecord another) {
        Preconditions.checkState(another.getStart().isPresent());
        Preconditions.checkState(this.getStart().isPresent());
        return another.getStart().get().compareTo(getStart().get());
    }
}
