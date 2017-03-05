package com.tastybug.timetracker.extensions.reporting.controller.internal.aggregated;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DayListFactoryTest {

    private DayListFactory dayListFactory = new DayListFactory();

    @Test(expected = NullPointerException.class)
    public void createList_throws_NPE_on_null_start_date() {
        dayListFactory.createList(null, new Date());
    }

    @Test(expected = NullPointerException.class)
    public void createList_throws_NPE_on_null_until_date() {
        dayListFactory.createList(new Date(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createList_throws_IAE_on_until_date_that_is_before_start_date() {
        dayListFactory.createList(new DateTime(2016, 12, 24, 0, 0).toDate(), new DateTime(2016, 12, 23, 0, 0).toDate());
    }

    @Test
    public void createList_accepts_time_frame_spanning_single_day() {
        // when
        List<AggregatedDay> dayList = dayListFactory.createList(new DateTime(2016, 12, 24, 0, 0).toDate(),
                new DateTime(2016, 12, 24, 0, 0).toDate());

        // then
        assertEquals(dayList.size(), 1);

        // and
        assertEquals(dayList.get(0).getStartDate(), new DateTime(2016, 12, 24, 0, 0).toDate());
    }

    @Test
    public void createList_accepts_time_frame_spanning_multiple_days() {
        // when
        List<AggregatedDay> dayList = dayListFactory.createList(new DateTime(2016, 12, 24, 0, 0).toDate(),
                new DateTime(2016, 12, 26, 0, 0).toDate());

        // then
        assertEquals(dayList.size(), 3);

        // and
        assertEquals(dayList.get(0).getStartDate(), new DateTime(2016, 12, 24, 0, 0).toDate());
        assertEquals(dayList.get(1).getStartDate(), new DateTime(2016, 12, 25, 0, 0).toDate());
        assertEquals(dayList.get(2).getStartDate(), new DateTime(2016, 12, 26, 0, 0).toDate());
    }

}