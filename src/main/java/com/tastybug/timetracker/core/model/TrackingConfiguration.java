package com.tastybug.timetracker.core.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

public class TrackingConfiguration extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String projectUuid;
    private Integer hourLimit;
    private Date start, end;
    private boolean promptForDescription = false;
    private Rounding.Strategy roundingStrategy;


    public TrackingConfiguration(String projectUuid) {
        this(projectUuid, Rounding.Strategy.NO_ROUNDING);
    }

    public TrackingConfiguration(String projectUuid, Rounding.Strategy strategy) {
        this.projectUuid = projectUuid;
        this.roundingStrategy = strategy;
    }

    public TrackingConfiguration(String uuid,
                                 String projectUuid,
                                 Optional<Integer> hourLimit,
                                 Optional<Date> start,
                                 Optional<Date> end,
                                 Boolean promptForDescription,
                                 Rounding.Strategy roundingStrategy) {
        this.uuid = uuid;
        this.projectUuid = projectUuid;
        this.hourLimit = hourLimit.orNull();
        this.start = start.orNull();
        this.end = end.orNull();
        this.promptForDescription = promptForDescription;
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
        this.projectUuid = projectUuid;
    }

    public Rounding.Strategy getRoundingStrategy() {
        return roundingStrategy;
    }

    public void setRoundingStrategy(Rounding.Strategy roundingStrategy) {
        Preconditions.checkNotNull(roundingStrategy);
        this.roundingStrategy = roundingStrategy;
    }

    public boolean hasAlteringRoundingStrategy() {
        return roundingStrategy != Rounding.Strategy.NO_ROUNDING;
    }

    public Optional<Integer> getHourLimit() {
        return Optional.fromNullable(hourLimit);
    }

    public void setHourLimit(Optional<Integer> hourLimit) {
        Preconditions.checkNotNull(hourLimit);
        Preconditions.checkArgument(!(hourLimit.isPresent() && hourLimit.get() < 0));
        this.hourLimit = (hourLimit.isPresent() && hourLimit.get() == 0) ? null : hourLimit.orNull();
    }

    public boolean isPromptForDescription() {
        return promptForDescription;
    }

    public void setPromptForDescription(boolean promptForDescription) {
        this.promptForDescription = promptForDescription;
    }

    public Optional<Date> getStart() {
        return Optional.fromNullable(start);
    }

    public void setStart(Optional<Date> start) {
        Preconditions.checkNotNull(start);
        if (getEnd().isPresent() && start.isPresent()) {
            Preconditions.checkArgument(start.get().before(getEnd().get()), "Start date cannot be after end date!");
        }
        this.start = start.orNull();
    }

    public Optional<Date> getEnd() {
        return Optional.fromNullable(end);
    }

    public void setEnd(Optional<Date> end) {
        Preconditions.checkNotNull(end);
        if (getStart().isPresent() && end.isPresent()) {
            Preconditions.checkArgument(end.get().after(getStart().get()), "End date cannot be before start date!");
        }
        this.end = end.orNull();
    }

    public Optional<Date> getEndDateAsInclusive() {
        if (getEnd().isPresent()) {
            DateTime inclusiveDate = new DateTime(end).minusDays(1);
            return Optional.of(inclusiveDate.toDate());
        } else {
            return Optional.absent();
        }
    }

    public void setEndAsInclusive(Optional<Date> endAsInclusive) {
        Preconditions.checkNotNull(endAsInclusive);
        if (endAsInclusive.isPresent()) {
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
                .add("promptForDescription", isPromptForDescription())
                .add("roundingStrategy", getRoundingStrategy().name())
                .toString();
    }
}
