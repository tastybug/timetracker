package com.tastybug.timetracker.util.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.ProjectTimeConstraints;

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
public class ProjectTimeConstraintsDAOTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    ProjectTimeConstraintsDAO projectTimeConstraintsDAO = new ProjectTimeConstraintsDAO(context);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }


    @Test public void canGetExistingEntityById() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor(1);
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        ProjectTimeConstraints constraints = projectTimeConstraintsDAO.get(1);

        // then
        assertNotNull(constraints);
        assertEquals(1, constraints.getId().intValue());
        assertEquals(2, constraints.getProjectId().intValue());
        assertEquals(5, constraints.getHourLimit().get().intValue());
        assertNotNull(constraints.getStart());
        assertNotNull(constraints.getEnd());
    }

    @Test public void gettingNonexistingEntityByIdYieldsNull() {
        // given
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(null);

        // when
        ProjectTimeConstraints constraints = projectTimeConstraintsDAO.get(1);

        // then
        assertNull(constraints);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedStartDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor(1, "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        projectTimeConstraintsDAO.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeContraintsCursor(1, getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        projectTimeConstraintsDAO.get(1);
    }

    @Test public void getAllWorksForExistingTimeFrames() {
        // given
        Cursor aCursorWith2TimeFrames = aCursorWith2Entities();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(aCursorWith2TimeFrames);

        // when
        ArrayList<ProjectTimeConstraints> entities = projectTimeConstraintsDAO.getAll();

        // then
        assertEquals(2, entities.size());
    }

    @Test public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(noProjects);

        // when
        ArrayList<ProjectTimeConstraints> entities = projectTimeConstraintsDAO.getAll();

        // then
        assertEquals(0, entities.size());
    }

    @Test public void canCreateEntity() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();
        Uri uriMock = mock(Uri.class);
        when(uriMock.getLastPathSegment()).thenReturn("5");
        when(resolver.insert(any(Uri.class), any(ContentValues.class))).thenReturn(uriMock);

        // when
        projectTimeConstraintsDAO.create(constraints);

        // then
        assertEquals(5, constraints.getId().intValue());
    }

    @Test public void canUpdateEntity() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = projectTimeConstraintsDAO.update(constraints);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteEntity() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectTimeConstraintsDAO.delete(constraints);

        // then
        assertTrue(success);
    }

    @Test public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        ProjectTimeConstraints constraints = new ProjectTimeConstraints();
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = projectTimeConstraintsDAO.delete(constraints);

        // then
        assertFalse(success);
    }

    @Test public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(TimeFrameDAO.ID_COLUMN, projectTimeConstraintsDAO.getPKColumn());
    }

    @Test public void knowsAllColumns() {
        // expect
        assertTrue(Arrays.asList(projectTimeConstraintsDAO.getColumns()).contains(ProjectTimeConstraintsDAO.ID_COLUMN));
        assertTrue(Arrays.asList(projectTimeConstraintsDAO.getColumns()).contains(ProjectTimeConstraintsDAO.PROJECT_FK_COLUMN));
        assertTrue(Arrays.asList(projectTimeConstraintsDAO.getColumns()).contains(ProjectTimeConstraintsDAO.HOUR_LIMIT_COLUMN));
        assertTrue(Arrays.asList(projectTimeConstraintsDAO.getColumns()).contains(ProjectTimeConstraintsDAO.STARTS_AT_COLUMN));
        assertTrue(Arrays.asList(projectTimeConstraintsDAO.getColumns()).contains(ProjectTimeConstraintsDAO.ENDS_AT_COLUMN));
    }

    private Cursor aProjectTimeContraintsCursor(int id) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(id);
        when(cursor.getInt(1)).thenReturn(2);
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(4)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor aProjectTimeContraintsCursor(int id, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(id);
        when(cursor.getInt(1)).thenReturn(2);
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(startDateString);
        when(cursor.getString(4)).thenReturn(endDateString);
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }
    private Cursor aCursorWith2Entities() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(1);
        when(cursor.getInt(1)).thenReturn(2);
        when(cursor.getInt(2)).thenReturn(5);
        when(cursor.getString(3)).thenReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        when(cursor.getString(4)).thenReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        return cursor;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}