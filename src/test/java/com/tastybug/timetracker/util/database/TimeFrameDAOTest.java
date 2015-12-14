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

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test public void canGetExistingTimeFrameById() {
        // given
        Cursor cursor = aTimeframeCursor("1");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TimeFrame tf = timeFrameDAO.get(1);

        // then
        assertNotNull(tf);
        assertEquals("1", tf.getUuid());
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
        Cursor cursor = aTimeframeCursor("1", "2", "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        timeFrameDAO.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aTimeframeCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        timeFrameDAO.get(1);
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

    @Test public void canCreateTimeFrame() {
        // given
        TimeFrame timeFrame = new TimeFrame();

        // when
        timeFrameDAO.create(timeFrame);

        // then
        assertNotNull(timeFrame.getUuid());
        assertNotNull(timeFrame.getContext());
    }

    @Test public void canUpdateTimeFrame() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = timeFrameDAO.update(timeFrame);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteTimeFrame() {
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
        assertEquals(4, timeFrameDAO.getColumns().length);
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.ID_COLUMN));
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.PROJECT_UUID_COLUMN));
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.START_DATE_COLUMN));
        assertTrue(Arrays.asList(timeFrameDAO.getColumns()).contains(TimeFrameDAO.END_DATE_COLUMN));
    }

    private Cursor aTimeframeCursor(String uuid) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aTimeframeCursor(String uuid, String projectUuid, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(projectUuid);
        when(cursor.getString(2)).thenReturn(startDateString);
        when(cursor.getString(3)).thenReturn(endDateString);
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
        when(cursor.getString(0)).thenReturn("1");
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}