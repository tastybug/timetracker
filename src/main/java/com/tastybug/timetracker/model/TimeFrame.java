package com.tastybug.timetracker.model;

import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.util.Date;
import org.joda.time.Duration;

public class TimeFrame {

    private Date start, end;

    @Nullable
    private String description;

    public TimeFrame() {}

    public TimeFrame(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public void start() {
        if (hasStart()) {
            throw new IllegalStateException(toString() + " is already started!");
        }
        this.start = new Date();
    }

    public boolean hasStart() {
        return start != null;
    }

    public void stop() {
        if (!hasStart()) {
            throw new IllegalStateException(toString() + " is not started yet!");
        }
        if (hasEnd()) {
            throw new IllegalStateException(toString() + " is already stopped!");
        }
        this.end = new Date();
    }

    public boolean hasEnd() {
        return end != null;
    }

    public boolean isRunning() {
        return start != null && end == null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Duration> getAsDuration() {
        if (hasStart() && hasEnd()) {
            return Optional.of(new Duration(start.getTime(), end.getTime()));
        }
        return Optional.absent();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("start", start)
                .add("end", end)
                .add("description", description)
                .toString();
    }
}
