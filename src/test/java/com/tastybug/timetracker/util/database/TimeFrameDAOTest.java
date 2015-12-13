package com.tastybug.timetracker.util.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.TimeFrame;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TimeFrameDAOTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    TimeFrameDAO timeFrameDAO = new TimeFrameDAO(context);

    @Before public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test
    public void canGetExistingTimeFrameById() {
        // given
        Cursor cursor = aTimeframeCursor(1);
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
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
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
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
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
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
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNotNull(tf);
        assertEquals(1, tf.getId().intValue());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
    }

    @Test public void getAllWorksForExistingTimeFrames() {
        // given
        Cursor aCursorWith2TimeFrames = aCursorWith2TimeFrames();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(aCursorWith2TimeFrames);

        // when
        ArrayList<TimeFrame> timeFrames = timeFrameDAO.getAll();

        // then
        assertEquals(2, timeFrames.size());
    }

    @Test public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(noProjects);

        // when
        ArrayList<TimeFrame> timeFrames = timeFrameDAO.getAll();

        // then
        assertEquals(0, timeFrames.size());
    }

    @Test public void canCreateTimeframe() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        Uri uriMock = mock(Uri.class);
        when(uriMock.getLastPathSegment()).thenReturn("5");
        when(resolver.insert(any(Uri.class), any(ContentValues.class))).thenReturn(uriMock);

        // when
        timeFrameDAO.create(timeFrame);

        // then
        assertEquals(5, timeFrame.getId().intValue());
    }

    @Test public void canUpdateTimeframe() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = timeFrameDAO.update(timeFrame);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteTimeframe() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = timeFrameDAO.delete(timeFrame);

        // then
        assertTrue(success);
    }

    @Test public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = timeFrameDAO.delete(timeFrame);

        // then
        assertFalse(success);
    }

    @Test public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(TimeFrameDAO.ID_COLUMN, timeFrameDAO.getPKColumn());
    }

    @Test public void knowsAllColumns() {
        // expect
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.ID_COLUMN));
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.START_DATE_COLUMN));
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.END_DATE_COLUMN));
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

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        return cursor;
    }

    private Cursor aCursorWith2TimeFrames() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(1);
        when(cursor.getString(1)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }
    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}