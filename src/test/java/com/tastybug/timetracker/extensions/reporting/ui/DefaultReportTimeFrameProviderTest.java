package com.tastybug.timetracker.extensions.reporting.ui;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.util.DateProvider;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultReportTimeFrameProviderTest {

    private final DateProvider dateProvider = mock(DateProvider.class);
    private final DateTime today = new DateTime(2016, 12, 24, 12, 30, 55, 66);
    private final Date defaultMonthStart = new DateTime(2016, 12, 1, 0, 0).toDate();
    private final Date defaultMonthEnd = new DateTime(2016, 12, 31, 0, 0).toDate();

    private DefaultReportTimeFrameProvider subject = new DefaultReportTimeFrameProvider(dateProvider);

    @Before
    public void setup() {
        when(dateProvider.getCurrentDate()).thenReturn(today.toDate());
    }

    @Test
    public void without_a_given_project_the_interval_is_the_current_month() {
        // given: no project set
        subject = subject.forTrackingConfiguration(null);

        // when
        Interval timeFrame = subject.getTimeFrame();

        // then
        assertEquals(defaultMonthStart, timeFrame.getStart().toDate());

        // and
        assertEquals(defaultMonthEnd, timeFrame.getEnd().toDate());

    }

    @Test
    public void the_interval_of_the_current_month_is_timeless() {
        // given: no project set
        subject = subject.forTrackingConfiguration(null);

        // when
        Interval timeFrame = subject.getTimeFrame();
        DateTime start = timeFrame.getStart();
        DateTime end = timeFrame.getEnd();

        // then
        assertEquals(0, start.getHourOfDay());
        assertEquals(0, start.getMinuteOfHour());
        assertEquals(0, start.getSecondOfMinute());
        assertEquals(0, start.getMillisOfSecond());

        // and
        assertEquals(0, end.getHourOfDay());
        assertEquals(0, end.getMinuteOfHour());
        assertEquals(0, end.getSecondOfMinute());
        assertEquals(0, end.getMillisOfSecond());

    }

    @Test
    public void when_given_a_non_framed_project_the_interval_is_the_current_month() {
        // given: non framed tracking configuration
        subject = subject.forTrackingConfiguration(new TrackingConfiguration(""));

        // when
        Interval timeFrame = subject.getTimeFrame();

        // then
        assertEquals(defaultMonthStart, timeFrame.getStart().toDate());

        // and
        assertEquals(defaultMonthEnd, timeFrame.getEnd().toDate());
    }

    @Test
    public void when_given_a_framed_project_the_report_interval_is_exactly_that() {
        // given: tracking configuration with start and end
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("");
        Date configurationStart = new DateTime(2016, 12, 1, 0, 0).toDate();
        Date configurationEnd = new DateTime(2016, 12, 20, 0, 0).toDate();
        trackingConfiguration.setStart(Optional.of(configurationStart));
        trackingConfiguration.setEnd(Optional.of(configurationEnd));
        subject = subject.forTrackingConfiguration(trackingConfiguration);

        // when
        Interval timeFrame = subject.getTimeFrame();

        // then
        assertEquals(configurationStart, timeFrame.getStart().toDate());

        // and
        assertEquals(configurationEnd, timeFrame.getEnd().toDate());
    }

}