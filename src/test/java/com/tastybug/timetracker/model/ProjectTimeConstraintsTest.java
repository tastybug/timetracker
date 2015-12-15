package com.tastybug.timetracker.model;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class ProjectTimeConstraintsTest {

    Context contextMock = mock(Context.class);
    ProjectTimeConstraintsDAO daoMock = mock(ProjectTimeConstraintsDAO.class);

    @Test
    public void canCreateProjectTimeConstraints() {
        // when
        DateTime start = new DateTime();
        DateTime end = start.plusDays(5);
        ProjectTimeConstraints constraints = new ProjectTimeConstraints("1", "2", 3, start.toDate(), end.toDate());

        // then
        assertNotNull(constraints);
        assertEquals("1", constraints.getUuid());
        assertEquals("2", constraints.getProjectUuid());
        assertEquals(3, constraints.getHourLimit().get().intValue());
        assertEquals(start.toDate(), constraints.getStart().get());
        assertEquals(end.toDate(), constraints.getEnd().get());
    }

    @Test public void noHourLimitIsHandledWell() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();

        // when
        constraints.setHourLimit(Optional.of(new Integer(5)));

        // then
        assertEquals(5, constraints.getHourLimit().get().intValue());

        // when
        constraints.setHourLimit(Optional.<Integer>absent());

        // then
        assertFalse(constraints.getHourLimit().isPresent());
    }

    @Test public void noStartDateIsHandledWell() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();

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
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();

        // when
        Date d = new Date();
        constraints.setEnd(Optional.of(d));

        // then
        assertEquals(d, constraints.getEnd().get());

        // when
        constraints.setEnd(Optional.<Date>absent());

        // then
        assertFalse(constraints.getEnd().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullUuid() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();

        // when
        constraints.setUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectFk() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();

        // when
        constraints.setProjectUuid(null);
    }

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();
        constraints.setContext(contextMock);
        constraints.setDAO(daoMock);

        // when
        constraints.setUuid("10000"); // this does not trigger
        constraints.setProjectUuid("1234");
        constraints.setHourLimit(Optional.of(10));
        constraints.setStart(Optional.of(new Date()));
        constraints.setEnd(Optional.of(new Date()));

        // then
        verify(daoMock, times(4)).update(constraints);
    }
}