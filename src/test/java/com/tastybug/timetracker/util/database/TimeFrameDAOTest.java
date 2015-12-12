package com.tastybug.timetracker.util.database;

import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.TimeFrame;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeFrameDAOTest {


    Context context = mock(Context.class);
    ContentResolverProvider provider = mock(ContentResolverProvider.class);

    // test subject
    TimeFrameDAO timeFrameDAO;


    @Before
    public void setup() {
        timeFrameDAO = new TimeFrameDAO(context);
        timeFrameDAO.setContentResolverProvider(provider);
    }

    @Test
    public void canGetExistingTimeFrameById() {
        // given
        Cursor cursor = aTimeframeCursor(1);
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNotNull(tf);
        assertEquals(1, tf.getId().intValue());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
    }

    @Test public void gettingNonexistingProjectByIdYieldsNull() {
        // given
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(null);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNull(tf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedStartDateStringLeadsToException() {
        // given
        Cursor cursor = aTimeframeCursor(1, "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNotNull(tf);
        assertEquals(1, tf.getId().intValue());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aTimeframeCursor(1, getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNotNull(tf);
        assertEquals(1, tf.getId().intValue());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
    }

    private Cursor aTimeframeCursor(int id) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(id);
        when(cursor.getString(1)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aTimeframeCursor(int id, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(id);
        when(cursor.getString(1)).thenReturn(startDateString);
        when(cursor.getString(2)).thenReturn(endDateString);
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}