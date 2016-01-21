package com.tastybug.timetracker.model;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class ProjectTimeConstraintsTest {

    Context contextMock = mock(Context.class);
    DAOFactory daoFactory = mock(DAOFactory.class);
    ProjectTimeConstraintsDAO projectTimeConstraintsDAO = mock(ProjectTimeConstraintsDAO.class);

    @Before
    public void setup() {
        when(daoFactory.getDao(eq(ProjectTimeConstraints.class), isA(Context.class))).thenReturn(projectTimeConstraintsDAO);
    }

    @Test
    public void canCreateProjectTimeConstraints() {
        // when
        DateTime start = new DateTime();
        DateTime end = start.plusDays(5);
        ProjectTimeConstraints constraints = new ProjectTimeConstraints("1", "2", 3, start.toDate(), end.toDate(), TimeFrameRounding.Strategy.NO_ROUNDING);

        // then
        assertNotNull(constraints);
        assertEquals("1", constraints.getUuid());
        assertEquals("2", constraints.getProjectUuid());
        assertEquals(3, constraints.getHourLimit().get().intValue());
        assertEquals(start.toDate(), constraints.getStart().get());
        assertEquals(end.toDate(), constraints.getEnd().get());
        assertEquals(TimeFrameRounding.Strategy.NO_ROUNDING, constraints.getRoundingStrategy());
    }

    @Test public void noHourLimitIsHandledWell() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        constraints.setHourLimit(Optional.of(5));

        // then
        assertEquals(5, constraints.getHourLimit().get().intValue());

        // when
        constraints.setHourLimit(Optional.<Integer>absent());

        // then
        assertFalse(constraints.getHourLimit().isPresent());
    }

    @Test public void noStartDateIsHandledWell() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        Date d = new Date();
        constraints.setStart(Optional.of(d));

        // then
        assertEquals(d, constraints.getStart().get());

        // when
        constraints.setStart(Optional.<Date>absent());

        // then
        assertFalse(constraints.getStart().isPresent());
    }

    @Test public void noEndDateIsHandledWell() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        Date d = new Date();
        constraints.setEnd(Optional.of(d));

        // then
        assertEquals(d, constraints.getEnd().get());

        // when
        constraints.setEnd(Optional.<Date>absent());

        // then
        assertFalse(constraints.getEnd().isPresent());

        // and
        assertFalse(constraints.getEndDateAsInclusive().isPresent());
    }

    @Test public void canGetEnddateAsAnInclusiveDate() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when: set an end date (which is EXCLUSIVE)
        DateTime exclusiveDate = new DateTime(2016, 1, 1, 0, 0);
        constraints.setEnd(Optional.of(exclusiveDate.toDate()));

        // then
        DateTime expectedInclusiveDate = new DateTime(2015, 12, 31, 0, 0);
        assertEquals(expectedInclusiveDate.toDate(), constraints.getEndDateAsInclusive().get());
    }

    @Test public void canSetEnddateAsInclusiveDate() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();
        DateTime lastInclusiveDate = new DateTime(2015, 12, 31, 0, 0);

        // when: I set the last day of the year as my last project day
        constraints.setEndAsInclusive(Optional.of(lastInclusiveDate.toDate()));

        // then
        assertEquals(lastInclusiveDate.toDate(), constraints.getEndDateAsInclusive().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetEndDateBeforeStartDate() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();
        DateTime startDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidEndDate = new DateTime(2015, 1, 1, 0, 0);
        constraints.setStart(Optional.of(startDate.toDate()));

        // when
        constraints.setEnd(Optional.of(invalidEndDate.toDate()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetStartDateAfterEndDate() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();
        DateTime endDate = new DateTime(2016, 1, 1, 0, 0);
        DateTime invalidStartDate = new DateTime(2016, 10, 10, 0, 0);
        constraints.setEnd(Optional.of(endDate.toDate()));

        // when
        constraints.setStart(Optional.of(invalidStartDate.toDate()));
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullRoundingStrategy() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        constraints.setRoundingStrategy(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullUuid() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        constraints.setUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectFk() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();

        // when
        constraints.setProjectUuid(null);
    }

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        ProjectTimeConstraints constraints = aConstraintsInstance();
        constraints.setContext(contextMock);
        constraints.setDAOFactory(daoFactory);

        // when
        constraints.setUuid("10000"); // this does not trigger
        constraints.setProjectUuid("1234");
        constraints.setHourLimit(Optional.of(10));
        constraints.setStart(Optional.of(new Date()));
        constraints.setEnd(Optional.of(new Date()));
        constraints.setRoundingStrategy(TimeFrameRounding.Strategy.FULL_MINUTE_DOWN);

        // then
        verify(projectTimeConstraintsDAO, times(5)).update(constraints);
    }

    private ProjectTimeConstraints aConstraintsInstance() {
        return new ProjectTimeConstraints("some project-uuid");
    }
}