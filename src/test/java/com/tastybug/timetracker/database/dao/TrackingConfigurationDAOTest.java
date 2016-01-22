package com.tastybug.timetracker.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.TimeFrameRounding;
import com.tastybug.timetracker.model.TrackingConfiguration;

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
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TrackingConfigurationDAOTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    TrackingConfigurationDAO trackingConfigurationDAO = new TrackingConfigurationDAO(context);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }


    @Test public void canGetExistingEntityById() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor("1");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.get("1").get();

        // then
        assertNotNull(trackingConfiguration);
        assertEquals("1", trackingConfiguration.getUuid());
        assertEquals("2", trackingConfiguration.getProjectUuid());
        assertEquals(5, trackingConfiguration.getHourLimit().get().intValue());
        assertNotNull(trackingConfiguration.getStart());
        assertNotNull(trackingConfiguration.getEnd());
    }

    @Test public void gettingNonexistingEntityByIdYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.get("1").orNull();

        // then
        assertNull(trackingConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedStartDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor("1", "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        trackingConfigurationDAO.get("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor("1", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        trackingConfigurationDAO.get("1");
    }

    @Test public void getAllWorksForExistingTimeFrames() {
        // given
        Cursor aCursorWith2TimeFrames = aCursorWith2Entities();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(aCursorWith2TimeFrames);

        // when
        ArrayList<TrackingConfiguration> entities = trackingConfigurationDAO.getAll();

        // then
        assertEquals(2, entities.size());
    }

    @Test public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(noProjects);

        // when
        ArrayList<TrackingConfiguration> entities = trackingConfigurationDAO.getAll();

        // then
        assertEquals(0, entities.size());
    }

    @Test public void canCreateEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");

        // when
        trackingConfigurationDAO.create(trackingConfiguration);

        // then
        assertNotNull(trackingConfiguration.getUuid());
        assertNotNull(trackingConfiguration.getContext());
    }

    @Test public void canUpdateEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = trackingConfigurationDAO.update(trackingConfiguration);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = trackingConfigurationDAO.delete(trackingConfiguration);

        // then
        assertTrue(success);
    }

    @Test public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = trackingConfigurationDAO.delete(trackingConfiguration);

        // then
        assertFalse(success);
    }

    @Test public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(TimeFrameDAO.ID_COLUMN, trackingConfigurationDAO.getPKColumn());
    }

    @Test public void knowsAllColumns() {
        // expect
        assertEquals(6, trackingConfigurationDAO.getColumns().length);
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.UUID_COLUMN));
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.PROJECT_UUID_COLUMN));
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.HOUR_LIMIT_COLUMN));
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.START_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.END_DATE_COLUMN));
        assertTrue(Arrays.asList(trackingConfigurationDAO.getColumns()).contains(TrackingConfigurationDAO.ROUNDING_STRATEGY_COLUMN));
    }

    @Test(expected = NullPointerException.class)
    public void gettingTrackingConfigurationByNullProjectUuidYieldsException() {
        // expect
        trackingConfigurationDAO.getByProjectUuid(null);
    }

    @Test public void gettingTrackingConfigurationByUnknownProjectUuidYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid("1").orNull();

        // then
        assertNull(trackingConfiguration);
    }

    @Test public void canGetTrackingConfigurationByProjectUuid() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor("111");
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), any(String.class)))
                .thenReturn(cursor);

        // when
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid("1").get();

        // then
        assertNotNull(trackingConfiguration);
        assertEquals(trackingConfiguration.getUuid(), "111");
    }

    private Cursor aProjectTimeContraintsCursor(String uuid) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(4)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.getString(5)).thenReturn(TimeFrameRounding.Strategy.NO_ROUNDING.name());
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aProjectTimeContraintsCursor(String uuid, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(startDateString);
        when(cursor.getString(4)).thenReturn(endDateString);
        when(cursor.getString(5)).thenReturn(TimeFrameRounding.Strategy.NO_ROUNDING.name());
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }
    private Cursor aCursorWith2Entities() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn("1");
        when(cursor.getString(1)).thenReturn("2");
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(4)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.getString(5)).thenReturn(TimeFrameRounding.Strategy.NO_ROUNDING.name());
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToFirst()).thenReturn(false);
        when(cursor.moveToNext()).thenReturn(false);

        return cursor;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }
}