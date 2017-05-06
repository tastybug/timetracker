package com.tastybug.timetracker.core.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingRecordDAOTest {

    private Context context = mock(Context.class);
    private ContentResolver resolver = mock(ContentResolver.class);

    private TrackingRecordDAO trackingRecordDAO = new TrackingRecordDAO(context);

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test
    public void canGetExistingTrackingRecordById() {
        // given
        Cursor cursor = aTrackingRecordCursor("uuid", "project-uuid");
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        TrackingRecord tf = trackingRecordDAO.get("uuid").get();

        // then
        assertNotNull(tf);
        assertEquals("uuid", tf.getUuid());
        assertEquals("project-uuid", tf.getProjectUuid());
        assertNotNull(tf.getStart());
        assertNotNull(tf.getEnd());
        assertNotNull(tf.getDescription());
        assertNotNull(tf.getRoundingStrategy());
    }

    @Test
    public void gettingNonExistingProjectByIdYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
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
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test
    public void getAllWorksForExistingTrackingRecords() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2TrackingRecords();
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(aCursorWith2TrackingRecords);

        // when
        List<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertEquals(2, trackingRecords.size());
    }

    @Test
    public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(noProjects);

        // when
        List<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertEquals(0, trackingRecords.size());
    }

    @Test
    public void getLatestByStartDateReturnsFirstEntry() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2TrackingRecords();
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), anyString(), any(String[].class), anyString()))
                .thenReturn(aCursorWith2TrackingRecords);

        // when
        Optional<TrackingRecord> recordOptional = trackingRecordDAO.getLatestByStartDateForProjectUuid("a project uuid");

        // then
        assertEquals("uuid1", recordOptional.get().getUuid());
    }

    @Test
    public void getLatestByStartDateReturnsEmptyOptionalOnNoTrackingRecords() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(TrackingRecordDAO.COLUMNS), eq("project_uuid=?"), any(String[].class), eq("start_date DESC")))
                .thenReturn(noProjects);

        // when
        Optional<TrackingRecord> recordOptional = trackingRecordDAO.getLatestByStartDateForProjectUuid("a project uuid");

        // then
        assertFalse(recordOptional.isPresent());
    }

    @Test
    public void canCreateTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecordDAO.create(trackingRecord);

        // then
        assertNotNull(trackingRecord.getUuid());
    }

    @Test
    public void canUpdateTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = trackingRecordDAO.update(trackingRecord);

        // then
        assertEquals(1, updateCount);
    }

    @Test
    public void canDeleteTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertTrue(success);
    }

    @Test
    public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertFalse(success);
    }

    @Test
    public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(TrackingRecordDAO.ID_COLUMN, trackingRecordDAO.getPKColumn());
    }

    @Test
    public void knowsAllColumns() {
        // expect
        assertEquals(6, trackingRecordDAO.getColumns().length);
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.ID_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.PROJECT_UUID_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.START_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.END_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.DESCRIPTION_COLUMN));
        assertTrue(Arrays.asList(trackingRecordDAO.getColumns()).contains(TrackingRecordDAO.ROUNDING_STRATEGY_COLUMN));
    }

    @Test(expected = NullPointerException.class)
    public void gettingAllTrackingRecordsByNullProjectUuidYieldsException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), any(String.class)))
                .thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);

        // when
        trackingRecordDAO.getByProjectUuid(null);
    }

    @Test
    public void canGetTrackingRecordsByProjectUuid() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2");
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
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

    @Test
    public void gettingTrackingRecordsByUnknownProjectUuidYieldsEmptyList() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
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
        when(cursor.getString(4)).thenReturn(null);
        when(cursor.getString(5)).thenReturn(Rounding.Strategy.NO_ROUNDING.name());
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aTrackingRecordCursor(String uuid, String projectUuid, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(projectUuid);
        when(cursor.getString(2)).thenReturn(startDateString);
        when(cursor.getString(3)).thenReturn(endDateString);
        when(cursor.getString(4)).thenReturn(null);
        when(cursor.getString(5)).thenReturn(Rounding.Strategy.NO_ROUNDING.name());
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
        when(cursor.getString(0)).thenReturn("uuid1", "uuid2");
        when(cursor.getString(1)).thenReturn("project_uuid1", "project_uuid2");
        when(cursor.getString(2)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(4)).thenReturn(null);
        when(cursor.getString(5)).thenReturn(Rounding.Strategy.NO_ROUNDING.name());
        when(cursor.moveToNext()).thenReturn(true, true, false);
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }
}