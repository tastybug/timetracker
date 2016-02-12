package com.tastybug.timetracker.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.joda.time.Duration;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.UUID;

public class TimeFrame extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Date start, end;

    private String description;

    public TimeFrame() {}

    public TimeFrame(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public TimeFrame(String uuid, String projectUuid, Date start, Date end, String description) {
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

        PropertyChangeEvent e = new PropertyChangeEvent(this, "projectUuid", this.projectUuid, projectUuid);
        this.projectUuid = projectUuid;
        propertyChange(e);
    }

    public void start() {
        if (getStart().isPresent()) {
            throw new IllegalStateException(toString() + " is already started!");
        }
        Date newDate = new Date();
        PropertyChangeEvent e = new PropertyChangeEvent(this, "start", this.start, newDate);
        this.start = newDate;
        propertyChange(e);
    }

    public Optional<Date> getStart() {
        return Optional.fromNullable(start);
    }

    public void setStart(Date start) {
        Preconditions.checkNotNull(start);
        PropertyChangeEvent e = new PropertyChangeEvent(this, "start", this.start, start);
        this.start = start;
        propertyChange(e);
    }

    public void stop() {
        if (!getStart().isPresent()) {
            throw new IllegalStateException(toString() + " is not started yet!");
        }
        if (getEnd().isPresent()) {
            throw new IllegalStateException(toString() + " is already stopped!");
        }
        Date newDate = new Date();
        PropertyChangeEvent e = new PropertyChangeEvent(this, "end", this.end, newDate);
        this.end = newDate;
        propertyChange(e);
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public void setEnd(Date end) {
        Preconditions.checkNotNull(end);
        PropertyChangeEvent e = new PropertyChangeEvent(this, "end", this.end, end);
        this.end = end;
        propertyChange(e);
    }

    public boolean isRunning() {
        return getStart().isPresent() && !getEnd().isPresent();
    }

    public void setDescription(Optional<String> description) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "description", this.description, description);
        this.description = description.orNull();
        propertyChange(e);
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(description);
    }

    public Optional<Duration> toDuration() {
        if (getStart().isPresent() && getEnd().isPresent()) {
            return Optional.of(new Duration(start.getTime(), end.getTime()));
        }
        return Optional.absent();
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
}
