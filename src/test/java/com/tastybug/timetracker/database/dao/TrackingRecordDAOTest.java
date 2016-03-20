package com.tastybug.timetracker.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.TrackingRecord;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingRecordDAOTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    TrackingRecordDAO trackingRecordDAO = new TrackingRecordDAO(context);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test public void canGetExistingTrackingRecordById() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingRecord tf = trackingRecordDAO.get("1").get();

        // then
        assertNotNull(tf);
        assertEquals("1", tf.getUuid());
        assertEquals("2", tf.getProjectUuid());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
    }

    @Test public void gettingNonexistingProjectByIdYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingRecord tf = trackingRecordDAO.get("1").orNull();

        // then
        assertNull(tf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedStartDateStringLeadsToException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test public void getAllWorksForExistingTrackingRecords() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2TrackingRecords();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(aCursorWith2TrackingRecords);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertEquals(2, trackingRecords.size());
    }

    @Test public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(noProjects);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertEquals(0, trackingRecords.size());
    }

    @Test public void canCreateTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecordDAO.create(trackingRecord);

        // then
        assertNotNull(trackingRecord.getUuid());
        assertNotNull(trackingRecord.getContext());
    }

    @Test public void canUpdateTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = trackingRecordDAO.update(trackingRecord);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertTrue(success);
    }

    @Test public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertFalse(success);
    }

    @Test public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(TrackingRecordDAO.ID_COLUMN, trackingRecordDAO.getPKColumn());
    }

    @Test public void knowsAllColumns() {
        // expect
        assertEquals(5, trackingRecordDAO.getColumns().length);
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.ID_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.PROJECT_UUID_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.START_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.END_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.DESCRIPTION_COLUMN));
    }

    @Test(expected = NullPointerException.class)
    public void gettingAllTrackinRecordsByNullProjectUuidYieldsException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);

        // when
        trackingRecordDAO.getByProjectUuid(null);
    }

    @Test public void canGetTrackingRecordsByProjectUuid() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true, false);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getByProjectUuid("1");

        // then
        assertEquals(1, trackingRecords.size());
        assertEquals("1", trackingRecords.get(0).getUuid());
        assertEquals("2", trackingRecords.get(0).getProjectUuid());
        assertNotNull(trackingRecords.get(0).getStart());
        assertNotNull(trackingRecords.get(0).getEnd());
    }

    @Test public void gettingTrackingRecordsByUnknownProjectUuidYieldsEmptyList() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getByProjectUuid("1");

        // then
        assertTrue(trackingRecords.isEmpty());
    }

    private Cursor aTrackingRecordCursor(String uuid, String projectUuid) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(projectUuid);
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aTrackingRecordCursor(String uuid, String projectUuid, String startDateString, String endDateString) {
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

    private Cursor aCursorWith2TrackingRecords() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn("1");
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }
}