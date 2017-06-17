package com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated.AggregatedDay;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AggregatedDayTest {

    private DateTime today = new DateTime(2016, 12, 24, 0, 0);

    private AggregatedDay aggregatedDay = new AggregatedDay(today.toDate());

    @Test(expected = NullPointerException.class)
    public void can_not_create_aggDays_without_providing_a_reference_day() {
        // expect
        new AggregatedDay(null);
    }

    @Test
    public void getDate_returns_date_of_the_aggregated_day() {
        // when
        AggregatedDay aggregatedDay = new AggregatedDay(today.toDate());

        // then
        assertEquals(aggregatedDay.getStartDate(), today.toDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void add_fails_for_tracking_record_that_is_still_running() {
        aggregatedDay.addRecord(anOngoingTrackingRecord(Rounding.Strategy.SIXTY_MINUTES_UP));
    }

    @Test
    public void addRecord_absorbs_effective_duration_TR() {
        // when: adding a tracking record with duration of 160 minutes
        aggregatedDay.addRecord(aRecordWithinTheAggregatedDay(today, 160, Rounding.Strategy.SIXTY_MINUTES_UP));

        // then: duration is increased by *effective* duration
        assertEquals(3, aggregatedDay.getDuration().getStandardHours());
    }

    @Test
    public void addRecord_can_read_TR_spanning_from_yesterday_until_today() {
        // given: 12hours yesterday, 12 hours today
        DateTime start = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() - 1, 12, 0);
        DateTime end = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 12, 0);

        // when
        aggregatedDay.addRecord(aRecord(start, end, Rounding.Strategy.SIXTY_MINUTES_UP));

        // then: duration equals the hours covering today
        assertEquals(12, aggregatedDay.getDuration().getStandardHours());
    }

    @Test
    public void addRecord_can_read_TR_spanning_from_today_until_tomorrow() {
        // given: 12 hours today, 12 hours tomorrow
        DateTime start = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 12, 0);
        DateTime end = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() + 1, 12, 0);

        // when
        aggregatedDay.addRecord(aRecord(start, end, Rounding.Strategy.SIXTY_MINUTES_UP));

        // then: duration equals the hours covering today
        assertEquals(12, aggregatedDay.getDuration().getStandardHours());
    }

    @Test
    public void addRecord_will_add_the_difference_between_calc_and_effective_duration_to_last_day() {
        // given: 12hours yesterday, 11:10 hours today
        DateTime start = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() - 1, 12, 0);
        DateTime end = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 11, 10);

        // when: providing a tracking conf that rounds up
        aggregatedDay.addRecord(aRecord(start, end, Rounding.Strategy.SIXTY_MINUTES_UP));

        // then: duration for the 2nd day will contain the *effective* duration
        assertEquals(12, aggregatedDay.getDuration().getStandardHours());
    }

    @Test
    public void addRecord_will_add_up_multiple_TRs() {
        // given
        TrackingRecord beforeDinnerBreak = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 9, 45),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 12, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        TrackingRecord afterDinnerBreak = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 13, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 18, 30), Rounding.Strategy.SIXTY_MINUTES_UP);

        // when
        aggregatedDay.addRecord(beforeDinnerBreak);
        aggregatedDay.addRecord(afterDinnerBreak);

        // then
        assertEquals(9 * 60 * 60, aggregatedDay.getDuration().getStandardSeconds());
    }

    @Test
    public void addRecord_is_noop_for_TRs_outside_of_time_frame() {
        // given
        TrackingRecord endsBefore = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() - 2, 12, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() - 1, 12, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        TrackingRecord startsAfter = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() + 1, 12, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth() + 2, 14, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        Duration initialDuration = aggregatedDay.getDuration();

        // when
        aggregatedDay.addRecord(endsBefore);

        // then
        assertEquals(aggregatedDay.getDuration().compareTo(initialDuration), 0);

        // when
        aggregatedDay.addRecord(startsAfter);

        // then
        assertEquals(aggregatedDay.getDuration().compareTo(initialDuration), 0);
    }

    @Test
    public void addRecord_aggregates_multiple_TR_descriptions() {
        // given
        TrackingRecord first = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 9, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 10, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        TrackingRecord second = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 11, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 12, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        TrackingRecord third = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 13, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 14, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        String firstDescription = "Setup of the webserver\nStarted the server";
        String secondDescription = "Installed Apache\nStarted Apache";
        String thirdDescription = "Added webpages to /var/www\nChecked website";
        String expectedAggregatedDescription = firstDescription + "<br/>" + secondDescription + "<br/>" + thirdDescription;
        first.setDescription(Optional.of(firstDescription));
        second.setDescription(Optional.of(secondDescription));
        third.setDescription(Optional.of(thirdDescription));

        // when
        aggregatedDay.addRecord(first);
        aggregatedDay.addRecord(second);
        aggregatedDay.addRecord(third);

        // then
        assertTrue(aggregatedDay.getDescription().isPresent());

        // and
        assertEquals(aggregatedDay.getDescription().get(), expectedAggregatedDescription);
    }

    @Test
    public void TRs_without_description_result_in_absent_aggregated_description() {
        // given
        TrackingRecord first = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 9, 45),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 12, 0), Rounding.Strategy.SIXTY_MINUTES_UP);
        TrackingRecord second = aRecord(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 13, 0),
                new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 18, 30), Rounding.Strategy.SIXTY_MINUTES_UP);

        // when
        aggregatedDay.addRecord(first);
        aggregatedDay.addRecord(second);

        // then
        assertFalse(aggregatedDay.getDescription().isPresent());
    }

    @Test
    public void aggregatedDay_has_no_aggregated_description_initially() {
        // expect
        assertFalse(aggregatedDay.getDescription().isPresent());
    }

    private TrackingRecord aRecordWithinTheAggregatedDay(DateTime aggDayDate, int durationMinutes, Rounding.Strategy rounding) {
        DateTime start = new DateTime(aggDayDate.getYear(), aggDayDate.getMonthOfYear(), aggDayDate.getDayOfMonth(), 12, 0);

        return new TrackingRecord("uuid",
                "project-uuid",
                Optional.of(start.toDate()),
                Optional.of(start.plusMinutes(durationMinutes).toDate()),
                Optional.<String>absent(),
                rounding);
    }

    private TrackingRecord aRecord(DateTime start, DateTime end, Rounding.Strategy rounding) {

        return new TrackingRecord(
                "uuid",
                "project-uuid",
                Optional.of(start.toDate()),
                Optional.of(end.toDate()),
                Optional.<String>absent(),
                rounding);
    }

    private TrackingRecord anOngoingTrackingRecord(Rounding.Strategy rounding) {
        return new TrackingRecord(
                "uuid",
                "project-uuid",
                Optional.of(new Date()),
                Optional.<Date>absent(),
                Optional.<String>absent(),
                rounding);
    }
}