package com.tastybug.timetracker.model;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TrackingConfigurationTest {

    Context contextMock = mock(Context.class);
    DAOFactory daoFactory = mock(DAOFactory.class);
    TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);

    @Before
    public void setup() {
        when(daoFactory.getDao(eq(TrackingConfiguration.class), isA(Context.class))).thenReturn(trackingConfigurationDAO);
    }

    @Test
    public void canCreateTrackingConfiguration() {
        // when
        DateTime start = new DateTime();
        DateTime end = start.plusDays(5);
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("1", "2", 3, start.toDate(), end.toDate(), RoundingFactory.Strategy.NO_ROUNDING);

        // then
        assertNotNull(trackingConfiguration);
        assertEquals("1", trackingConfiguration.getUuid());
        assertEquals("2", trackingConfiguration.getProjectUuid());
        assertEquals(3, trackingConfiguration.getHourLimit().get().intValue());
        assertEquals(start.toDate(), trackingConfiguration.getStart().get());
        assertEquals(end.toDate(), trackingConfiguration.getEnd().get());
        assertEquals(RoundingFactory.Strategy.NO_ROUNDING, trackingConfiguration.getRoundingStrategy());
    }

    @Test public void noHourLimitIsHandledWell() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setHourLimit(Optional.of(5));

        // then
        assertEquals(5, trackingConfiguration.getHourLimit().get().intValue());

        // when
        trackingConfiguration.setHourLimit(Optional.<Integer>absent());

        // then
        assertFalse(trackingConfiguration.getHourLimit().isPresent());
    }

    @Test public void noStartDateIsHandledWell() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        Date d = new Date();
        trackingConfiguration.setStart(Optional.of(d));

        // then
        assertEquals(d, trackingConfiguration.getStart().get());

        // when
        trackingConfiguration.setStart(Optional.<Date>absent());

        // then
        assertFalse(trackingConfiguration.getStart().isPresent());
    }

    @Test public void noEndDateIsHandledWell() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        Date d = new Date();
        trackingConfiguration.setEnd(Optional.of(d));

        // then
        assertEquals(d, trackingConfiguration.getEnd().get());

        // when
        trackingConfiguration.setEnd(Optional.<Date>absent());

        // then
        assertFalse(trackingConfiguration.getEnd().isPresent());

        // and
        assertFalse(trackingConfiguration.getEndDateAsInclusive().isPresent());
    }

    @Test public void canGetEnddateAsAnInclusiveDate() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when: set an end date (which is EXCLUSIVE)
        DateTime exclusiveDate = new DateTime(2016, 1, 1, 0, 0);
        trackingConfiguration.setEnd(Optional.of(exclusiveDate.toDate()));

        // then
        DateTime expectedInclusiveDate = new DateTime(2015, 12, 31, 0, 0);
        assertEquals(expectedInclusiveDate.toDate(), trackingConfiguration.getEndDateAsInclusive().get());
    }

    @Test public void canSetEnddateAsInclusiveDate() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime lastInclusiveDate = new DateTime(2015, 12, 31, 0, 0);

        // when: I set the last day of the year as my last project day
        trackingConfiguration.setEndAsInclusive(Optional.of(lastInclusiveDate.toDate()));

        // then
        assertEquals(lastInclusiveDate.toDate(), trackingConfiguration.getEndDateAsInclusive().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetEndDateBeforeStartDate() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime startDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidEndDate = new DateTime(2015, 1, 1, 0, 0);
        trackingConfiguration.setStart(Optional.of(startDate.toDate()));

        // when
        trackingConfiguration.setEnd(Optional.of(invalidEndDate.toDate()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetStartDateAfterEndDate() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();
        DateTime endDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidStartDate = new DateTime(2016, 10, 10, 0, 0);
        trackingConfiguration.setEnd(Optional.of(endDate.toDate()));

        // when
        trackingConfiguration.setStart(Optional.of(invalidStartDate.toDate()));
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullRoundingStrategy() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setRoundingStrategy(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullHourLimit() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setHourLimit(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullStartDateOptional() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setStart(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullEndDateOptional() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setEnd(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullEndDateInclusiveOptional() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setEndAsInclusive(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullUuid() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectFk() {
        // given
        TrackingConfiguration trackingConfiguration = anInstance();

        // when
        trackingConfiguration.setProjectUuid(null);
    }

    private TrackingConfiguration anInstance() {
        return new TrackingConfiguration("some project-uuid");
    }
}