package com.tastybug.timetracker.extension.reporting.controller.internal.aggregated;

import com.google.common.base.Preconditions;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides a list of AggregatedDay instances for a given time frame.
 */
class DayListFactory {

    List<AggregatedDay> createList(Date firstDay, Date lastDay) {
        Preconditions.checkNotNull(firstDay);
        Preconditions.checkNotNull(lastDay);
        Preconditions.checkArgument(!lastDay.before(firstDay), "Given time frame has end date before start date. Must be equal or greater!");

        ArrayList<AggregatedDay> aggregatedDays = new ArrayList<>();
        for (DateTime day = new DateTime(firstDay); !day.isAfter(new DateTime(lastDay)); day = day.plusDays(1)) {
            aggregatedDays.add(new AggregatedDay(day.toDate()));
        }

        return aggregatedDays;
    }
}
