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

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.COLUMNS;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.END_DATE_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.ID_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.PROJECT_UUID_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.ROUNDING_STRATEGY_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.START_DATE_COLUMN;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
    public void can_get_TrackingRecord_by_uuid() {
        // given
        Cursor cursor = aTrackingRecordCursor("uuid", "project-uuid");
        given(resolver.query(any(Uri.class), eq(COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        TrackingRecord tf = trackingRecordDAO.get("uuid").get();

        // then
        assertThat(tf).isNotNull();
        assertThat(tf.getUuid()).isEqualTo("uuid");
        assertThat(tf.getProjectUuid()).isEqualTo("project-uuid");
        assertThat(tf.getStart()).isNotNull();
        assertThat(tf.getEnd()).isNotNull();
        assertThat(tf.getDescription()).isNotNull();
        assertThat(tf.getRoundingStrategy()).isNotNull();
    }

    @Test
    public void getting_none_existing_TrackingRecord_yields_empty() {
        // given
        Cursor cursor = anEmptyCursor();
        given(resolver.query(any(Uri.class), eq(COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        TrackingRecord tf = trackingRecordDAO.get("1").orNull();

        // then
        assertThat(tf).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_start_date_format_from_database_yields_IllegalArgumentException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(resolver.query(any(Uri.class), eq(COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_end_date_format_from_database_yields_IllegalArgumentException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        given(resolver.query(any(Uri.class), eq(COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        trackingRecordDAO.get("1");
    }

    @Test
    public void can_get_multiple_TrackingRecords_by_getAll() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2TrackingRecords();
        given(resolver.query(any(Uri.class), eq(COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .willReturn(aCursorWith2TrackingRecords);

        // when
        List<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertThat(trackingRecords.size()).isEqualTo(2);
    }

    @Test
    public void can_get_empty_list_by_getAll() {
        // given
        Cursor noProjects = anEmptyCursor();
        given(resolver.query(any(Uri.class), eq(COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .willReturn(noProjects);

        // when
        List<TrackingRecord> trackingRecords = trackingRecordDAO.getAll();

        // then
        assertThat(trackingRecords).isEmpty();
    }

    @Test
    public void can_get_latest_TrackingRecord() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2TrackingRecords();
        given(resolver.query(any(Uri.class), eq(COLUMNS), anyString(), any(String[].class), anyString()))
                .willReturn(aCursorWith2TrackingRecords);

        // when
        Optional<TrackingRecord> recordOptional = trackingRecordDAO.getLatestByStartDateForProjectUuid("a project uuid");

        // then
        assertThat(recordOptional.get().getUuid()).isEqualTo("uuid1");
    }

    @Test
    public void getLatestByStartDate_yields_empty_when_no_TrackingRecords_are_found() {
        // given
        Cursor noProjects = anEmptyCursor();
        given(resolver.query(any(Uri.class), eq(COLUMNS), eq("project_uuid=?"), any(String[].class), eq("start_date DESC")))
                .willReturn(noProjects);

        // when
        Optional<TrackingRecord> recordOptional = trackingRecordDAO.getLatestByStartDateForProjectUuid("a project uuid");

        // then
        assertThat(recordOptional.isPresent()).isFalse();
    }

    @Test
    public void create_TrackingRecord_inserts_via_ContentResolver() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecordDAO.create(trackingRecord);

        // then
        verify(resolver).insert(any(Uri.class), any(ContentValues.class));
    }

    @Test
    public void update_TrackingRecord_updates_via_ContentResolver() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        given(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).willReturn(1234);

        // when
        int updateCount = trackingRecordDAO.update(trackingRecord);

        // then
        assertThat(updateCount).isEqualTo(1234);
    }

    @Test
    public void canDeleteTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        given(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).willReturn(1);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertThat(success).isTrue();
    }

    @Test
    public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        given(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).willReturn(0);

        // when
        boolean success = trackingRecordDAO.delete(trackingRecord);

        // then
        assertThat(success).isFalse();
    }

    @Test
    public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertThat(trackingRecordDAO.getPKColumn()).isEqualTo(ID_COLUMN);
    }

    @Test
    public void knowsAllColumns() {
        // expect
        assertThat(trackingRecordDAO.getColumns()).containsOnly(
                ID_COLUMN,
                PROJECT_UUID_COLUMN,
                START_DATE_COLUMN,
                END_DATE_COLUMN,
                DESCRIPTION_COLUMN,
                ROUNDING_STRATEGY_COLUMN
        );
    }

    @Test(expected = NullPointerException.class)
    public void gettingAllTrackingRecordsByNullProjectUuidYieldsException() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), any(String.class)))
                .willReturn(cursor);
        given(cursor.moveToNext()).willReturn(true);

        // when
        trackingRecordDAO.getByProjectUuid(null);
    }

    @Test
    public void canGetTrackingRecordsByProjectUuid() {
        // given
        Cursor cursor = aTrackingRecordCursor("1", "2");
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);
        given(cursor.moveToNext()).willReturn(true, false);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getByProjectUuid("1");

        // then
        assertThat(trackingRecords.size()).isEqualTo(1);
        assertThat(trackingRecords.get(0).getUuid()).isEqualTo("1");
        assertThat(trackingRecords.get(0).getProjectUuid()).isEqualTo("2");
        assertThat(trackingRecords.get(0).getStart()).isNotNull();
        assertThat(trackingRecords.get(0).getEnd()).isNotNull();
    }

    @Test
    public void gettingTrackingRecordsByUnknownProjectUuidYieldsEmptyList() {
        // given
        Cursor cursor = anEmptyCursor();
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        ArrayList<TrackingRecord> trackingRecords = trackingRecordDAO.getByProjectUuid("1");

        // then
        assertThat(trackingRecords).isEmpty();
    }

    private Cursor aTrackingRecordCursor(String uuid, String projectUuid) {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn(uuid);
        given(cursor.getString(1)).willReturn(projectUuid);
        given(cursor.getString(2)).willReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(cursor.getString(3)).willReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        given(cursor.getString(4)).willReturn(null);
        given(cursor.getString(5)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToFirst()).willReturn(true);

        return cursor;
    }

    private Cursor aTrackingRecordCursor(String uuid, String projectUuid, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn(uuid);
        given(cursor.getString(1)).willReturn(projectUuid);
        given(cursor.getString(2)).willReturn(startDateString);
        given(cursor.getString(3)).willReturn(endDateString);
        given(cursor.getString(4)).willReturn(null);
        given(cursor.getString(5)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToFirst()).willReturn(true);

        return cursor;
    }

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        return cursor;
    }

    private Cursor aCursorWith2TrackingRecords() {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn("uuid1", "uuid2");
        given(cursor.getString(1)).willReturn("project_uuid1", "project_uuid2");
        given(cursor.getString(2)).willReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(cursor.getString(3)).willReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(cursor.getString(4)).willReturn(null);
        given(cursor.getString(5)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToNext()).willReturn(true, true, false);
        given(cursor.moveToFirst()).willReturn(true);

        return cursor;
    }
}