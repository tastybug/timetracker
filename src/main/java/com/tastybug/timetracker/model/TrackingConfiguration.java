package com.tastybug.timetracker.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.joda.time.DateTime;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.UUID;

public class TrackingConfiguration extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Integer hourLimit;
    private Date start, end;
    private RoundingFactory.Strategy roundingStrategy;


    public TrackingConfiguration(String projectUuid) {
        this(projectUuid, RoundingFactory.Strategy.NO_ROUNDING);
    }

    public TrackingConfiguration(String projectUuid, RoundingFactory.Strategy strategy) {
        this.projectUuid = projectUuid;
        this.roundingStrategy = strategy;
    }

    public TrackingConfiguration(String uuid,
                                 String projectUuid,
                                 Integer hourLimit,
                                 Date start,
                                 Date end,
                                 RoundingFactory.Strategy roundingStrategy) {
        this.uuid = uuid;
        this.projectUuid = projectUuid;
        this.hourLimit = hourLimit;
        this.start = start;
        this.end = end;
        this.roundingStrategy = roundingStrategy;
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

    public RoundingFactory.Strategy getRoundingStrategy() {
        return roundingStrategy;
    }

    public void setRoundingStrategy(RoundingFactory.Strategy roundingStrategy) {
        Preconditions.checkNotNull(roundingStrategy);

        PropertyChangeEvent e = new PropertyChangeEvent(this, "roundingStrategy", this.roundingStrategy, roundingStrategy);
        this.roundingStrategy = roundingStrategy;
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
        if (getEnd().isPresent() && start.isPresent()) {
            Preconditions.checkArgument(start.get().before(getEnd().get()), "Start date cannot be after end date!");
        }
        PropertyChangeEvent e = new PropertyChangeEvent(this, "start", this.start, start);
        this.start = start.orNull();
        propertyChange(e);
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public Optional<Date> getEndDateAsInclusive() {
        if (end != null) {
            DateTime inclusiveDate = new DateTime(end).minusDays(1);
            return Optional.of(inclusiveDate.toDate());
        } else {
            return Optional.absent();
        }
    }

    public void setEnd(Optional<Date> end) {
        if (getStart().isPresent() && end.isPresent()) {
            Preconditions.checkArgument(end.get().after(getStart().get()), "End date cannot be before start date!");
        }
        PropertyChangeEvent e = new PropertyChangeEvent(this, "end", this.end, end);
        this.end = end.orNull();
        propertyChange(e);
    }

    public void setEndAsInclusive(Optional<Date> endAsInclusive) {
        if(endAsInclusive.isPresent()) {
            DateTime dateTime = new DateTime(endAsInclusive.get());
            setEnd(Optional.of(dateTime.plusDays(1).toDate()));
        } else {
            setEnd(Optional.<Date>absent());
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("projectUuid", getProjectUuid())
                .add("hourLimit", getHourLimit().orNull())
                .add("start", getStart().orNull())
                .add("end", getEnd().orNull())
                .add("roundingStrategy", getRoundingStrategy().name())
                .toString();
    }
}
