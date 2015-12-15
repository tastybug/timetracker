package com.tastybug.timetracker.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
public class ProjectDAOTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    ProjectDAO projectDAO = new ProjectDAO(context);

    @Before public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test public void canGetExistingProjectById() {
        // given
        Cursor cursor = aProjectCursor("1", "title", "desc");
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        Project project = projectDAO.get(1);

        // then
        assertNotNull(project);
        assertEquals("1", project.getUuid());
        assertEquals("title", project.getTitle());
        assertEquals("desc", project.getDescription().get());
    }

    @Test public void gettingNonexistingProjectByIdYieldsNull() {
        // given
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(null);

        // when
        Project project = projectDAO.get(1);

        // then
        assertNull(project);
    }

    @Test public void getAllWorksForExistingProjects() {
        // given
        Cursor multipleProjects = aCursorWith2Projects();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(multipleProjects);

        // when
        ArrayList<Project> projects = projectDAO.getAll();

        // then
        assertEquals(2, projects.size());
    }

    @Test public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(noProjects);

        // when
        ArrayList<Project> projects = projectDAO.getAll();

        // then
        assertEquals(0, projects.size());
    }

    @Test public void canCreateProject() {
        // given
        Project project = new Project("title");

        // when
        projectDAO.create(project);

        // then
        assertNotNull(project.getUuid());
        assertNotNull(project.getContext());
    }

    @Test public void canUpdateProject() {
        // given
        Project project = new Project("1", "title", null);
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = projectDAO.update(project);

        // then
        assertEquals(1, updateCount);
    }

    @Test public void canDeleteProject() {
        // given
        Project project = new Project("1", "title", null);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertTrue(success);
    }

    @Test public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        Project project = new Project("1", "title", null);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertFalse(success);
    }

    @Test public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertEquals(ProjectDAO.UUID_COLUMN, projectDAO.getPKColumn());
    }

    @Test public void knowsAllColumns() {
        // expect
        assertEquals(3, projectDAO.getColumns().length);
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.UUID_COLUMN));
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.TITLE_COLUMN));
        assertTrue(Arrays.asList(projectDAO.getColumns()).contains(ProjectDAO.DESCRIPTION_COLUMN));
    }

    private Cursor aProjectCursor(String uuid, String title, String description) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(title);
        when(cursor.getString(2)).thenReturn(description);
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
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }
}