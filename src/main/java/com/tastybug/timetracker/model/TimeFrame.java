package com.tastybug.timetracker.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.tastybug.timetracker.util.database.EntityDAO;
import com.tastybug.timetracker.util.database.TimeFrameDAO;

import org.joda.time.Duration;

import java.beans.PropertyChangeEvent;
import java.util.Date;

public class TimeFrame extends Entity {

    private Integer id;

    private Date start, end;

    @Nullable
    private String description;

    public TimeFrame() {}

    @Override
    protected EntityDAO getDefaultDAOInstance(Context context) {
        return new TimeFrameDAO(context);
    }

    public TimeFrame(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public TimeFrame(int id, Date start, Date end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public void start() {
        if (hasStart()) {
            throw new IllegalStateException(toString() + " is already started!");
        }
        Date newDate = new Date();
        PropertyChangeEvent e = new PropertyChangeEvent(this, "start", this.start, newDate);
        this.start = newDate;
        propertyChange(e);
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
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
        Date newDate = new Date();
        PropertyChangeEvent e = new PropertyChangeEvent(this, "end", this.end, newDate);
        this.end = newDate;
        propertyChange(e);
    }

    public boolean hasEnd() {
        return end != null;
    }

    public boolean isRunning() {
        return start != null && end == null;
    }

    public void setDescription(String description) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "description", this.description, description);
        this.description = description;
        propertyChange(e);
    }

    public String getDescription() {
        return description;
    }

    public Optional<Duration> toDuration() {
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
