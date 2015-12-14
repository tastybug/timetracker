package com.tastybug.timetracker.model;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.util.database.EntityDAO;

import java.beans.PropertyChangeEvent;
import java.util.Date;

public class ProjectTimeConstraints extends Entity {

    private Integer id, projectId;

    private Integer hourLimit;

    private Date start, end;


    public ProjectTimeConstraints() {}

    public ProjectTimeConstraints(int id, int projectId, Integer hourLimit, Date start, Date end) {
        this.id = id;
        this.projectId = projectId;
        this.hourLimit = hourLimit;
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        Preconditions.checkNotNull(projectId);
        PropertyChangeEvent e = new PropertyChangeEvent(this, "projectId", this.projectId, projectId);
        this.projectId = projectId;
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

    @Override
    protected EntityDAO getDefaultDAOInstance(Context context) {
        return null;
    }
}
