package com.tastybug.timetracker.model;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.rounding.Rounding;

import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TrackingConfigurationTest {

    @Test
    public void setHourLimit_with_zero_argument_is_stored_as_no_hour_limit() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setHourLimit(Optional.of(0));

        // then
        assertFalse(trackingConfiguration.getHourLimit().isPresent());
    }

    @Test
    public void getEndDateAsInclusive_returns_end_date_minus_1_day() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when: set an end date (which is EXCLUSIVE)
        DateTime exclusiveDate = new DateTime(2016, 1, 1, 0, 0);
        trackingConfiguration.setEnd(Optional.of(exclusiveDate.toDate()));

        // then
        DateTime expectedInclusiveDate = new DateTime(2015, 12, 31, 0, 0);
        assertEquals(expectedInclusiveDate.toDate(), trackingConfiguration.getEndDateAsInclusive().get());
    }

    @Test
    public void setEndDateAsInclusive_sets_end_date_plus_1_day() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime lastInclusiveDate = new DateTime(2015, 12, 31, 0, 0);

        // when: I set the last day of the year as my last project day
        trackingConfiguration.setEndAsInclusive(Optional.of(lastInclusiveDate.toDate()));

        // then
        assertEquals(lastInclusiveDate.toDate(), trackingConfiguration.getEndDateAsInclusive().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEnd_with_end_before_start_date_yields_IllegalArgument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime startDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidEndDate = new DateTime(2015, 1, 1, 0, 0);
        trackingConfiguration.setStart(Optional.of(startDate.toDate()));

        // when
        trackingConfiguration.setEnd(Optional.of(invalidEndDate.toDate()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStart_with_start_after_end_date_yields_IllegalArgument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime endDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidStartDate = new DateTime(2016, 10, 10, 0, 0);
        trackingConfiguration.setEnd(Optional.of(endDate.toDate()));

        // when
        trackingConfiguration.setStart(Optional.of(invalidStartDate.toDate()));
    }

    @Test(expected = NullPointerException.class)
    public void setRoundingStrategy_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setRoundingStrategy(null);
    }

    @Test(expected = NullPointerException.class)
    public void setHourLimit_yields_null_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setHourLimit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHourLimit_yields_IllegalArgument_for_negative_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // expect
        trackingConfiguration.setHourLimit(Optional.of(-1));
    }

    @Test(expected = NullPointerException.class)
    public void setStart_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setStart(null);
    }

    @Test(expected = NullPointerException.class)
    public void setEnd_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setEnd(null);
    }

    @Test(expected = NullPointerException.class)
    public void setEndAsInclusive_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setEndAsInclusive(null);
    }

    @Test(expected = NullPointerException.class)
    public void setUuid_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void setProjectUuid_yields_NPE_for_null_argument() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setProjectUuid(null);
    }

    @Test
    public void can_serialize() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("proj", Rounding.Strategy.NO_ROUNDING);

        // when: this is supposed to cause no exception
        SerializationUtils.serialize(trackingConfiguration);
    }

    private TrackingConfiguration anInstance() {
        return new TrackingConfiguration("some project-uuid");
    }
}