package com.tastybug.timetracker.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

import static com.tastybug.timetracker.model.dao.ProjectDAO.CLOSED_COLUMN;
import static com.tastybug.timetracker.model.dao.ProjectDAO.DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.model.dao.ProjectDAO.TITLE_COLUMN;
import static com.tastybug.timetracker.model.dao.ProjectDAO.UUID_COLUMN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectDAOTest {

    private Context context = mock(Context.class);
    private ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    private ProjectDAO projectDAO = new ProjectDAO(context);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test
    public void canGetExistingProjectById() {
        // given
        Cursor cursor = aProjectCursor("1", "title", "desc", true);
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        Project project = projectDAO.get("1").get();

        // then
        assertNotNull(project);
        assertEquals("1", project.getUuid());
        assertEquals("title", project.getTitle());
        assertEquals("desc", project.getDescription().get());
        assertEquals(true, project.isClosed());

    }

    @Test
    public void gettingNonExistingProjectByIdYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        Project project = projectDAO.get("1").orNull();

        // then
        assertNull(project);
    }

    @Test
    public void getAllWorksForExistingProjects() {
        // given
        Cursor multipleProjects = aCursorWith2Projects();
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(multipleProjects);

        // when
        ArrayList<Project> projects = projectDAO.getAll();

        // then
        assertEquals(2, projects.size());
    }

    @Test
    public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(noProjects);

        // when
        ArrayList<Project> projects = projectDAO.getAll();

        // then
        assertEquals(0, projects.size());
    }

    @Test
    public void canCreateProject() {
        // given
        Project project = new Project("title");

        // when
        projectDAO.create(project);

        // then
        assertNotNull(project.getUuid());
    }

    @Test
    public void canUpdateProject() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), false);
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = projectDAO.update(project);

        // then
        assertEquals(1, updateCount);
    }

    @Test
    public void canDeleteProject() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertTrue(success);
    }

    @Test
    public void canDeleteProjectByUuid() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectDAO.delete(project.getUuid());

        // then
        assertTrue(success);
    }

    @Test
    public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertFalse(success);
    }

    @Test
    public void getContentValues_returns_complete_map_with_correct_values() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), true);

        // when
        ContentValues cv = projectDAO.getContentValues(project);

        // then
        assertEquals(project.getUuid(), cv.getAsString(UUID_COLUMN));
        assertEquals(project.getTitle(), cv.getAsString(TITLE_COLUMN));
        assertEquals(project.getDescription().orNull(), cv.getAsString(DESCRIPTION_COLUMN));
        assertEquals(project.isClosed(), cv.getAsBoolean(CLOSED_COLUMN));

        // and
        assertEquals(4, cv.size());
    }

    @Test
    public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(UUID_COLUMN, projectDAO.getPKColumn());
    }

    @Test
    public void knowsAllColumns() {
        // expect
        assertEquals(4, projectDAO.getColumns().length);
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(UUID_COLUMN));
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.TITLE_COLUMN));
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.DESCRIPTION_COLUMN));
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.CLOSED_COLUMN));
    }

    private Cursor aProjectCursor(String uuid, String title, String description, boolean closed) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(title);
        when(cursor.getString(2)).thenReturn(description);
        when(cursor.getInt(3)).thenReturn(closed ? 1 : 0);
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        return cursor;
    }

    private Cursor aCursorWith2Projects() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(1);
        when(cursor.getString(1)).thenReturn("title");
        when(cursor.getString(2)).thenReturn("desc");
        when(cursor.getInt(3)).thenReturn(0);
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }
}