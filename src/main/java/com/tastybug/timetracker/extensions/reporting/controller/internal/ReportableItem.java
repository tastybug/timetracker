package com.tastybug.timetracker.extensions.reporting.controller.internal;

import com.google.common.base.Optional;

import org.joda.time.Duration;

import java.util.Date;

public interface ReportableItem {

    Optional<String> getDescription();

    Duration getDuration();

    Date getStartDate();

    Date getEndDate();

    boolean isSameDay();

    boolean isWholeDay();
}
