package com.tastybug.timetracker.model;

import android.content.Context;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.EntityDAO;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.UUID;

public class ProjectTimeConstraints extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Integer hourLimit;
    private Date start, end;


    public ProjectTimeConstraints() {}

    public ProjectTimeConstraints(String uuid, String projectUuid, Integer hourLimit, Date start, Date end) {
        this.uuid = uuid;
        this.projectUuid = projectUuid;
        this.hourLimit = hourLimit;
        this.start = start;
        this.end = end;
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

    public Optional<Integer> getHourLimit() {
        return Optional.fromNullable(hourLimit);
    }

    public void setHourLimit(Optional<Integer> hourLimit) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "hourLimit", this.hourLimit, hourLimit);
        this.hourLimit = hourLimit.orNull();
        propertyChange(e);
    }

    public Optional<Date> getStart() {
        return Optional.fromNullable(start);
    }

    public void setStart(Optional<Date> start) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "start", this.start, start);
        this.start = start.orNull();
        propertyChange(e);
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public void setEnd(Optional<Date> end) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "end", this.end, end);
        this.end = end.orNull();
        propertyChange(e);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("projectUuid", getProjectUuid())
                .add("hourLimit", getHourLimit().orNull())
                .add("start", getStart().orNull())
                .add("end", getEnd().orNull())
                .toString();
    }
}
