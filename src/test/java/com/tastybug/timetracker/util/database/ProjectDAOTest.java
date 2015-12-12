package com.tastybug.timetracker.util.database;

import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectDAOTest {

    Context context = mock(Context.class);
    ContentResolverProvider provider = mock(ContentResolverProvider.class);

    // test subject
    ProjectDAO projectDAO;

    @Before public void setup() {
        projectDAO = new ProjectDAO(context);
        projectDAO.setContentResolverProvider(provider);
    }

    @Test public void canGetExistingProjectById() {
        // given
        Cursor cursor = aProjectCursor(1, "title", "desc");
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(cursor);

        // when
        Project project = projectDAO.get(1);

        // then
        assertNotNull(project);
        assertEquals(1, project.getId().intValue());
        assertEquals("title", project.getTitle());
        assertEquals("desc", project.getDescription().get());
    }

    @Test public void gettingNonexistingProjectByIdYieldsNull() {
        // given
        when(provider.query(any(String[].class),any(String.class),any(String[].class),any(String.class)))
                .thenReturn(null);

        // when
        Project project = projectDAO.get(1);

        // then
        assertNull(project);
    }

    private Cursor aProjectCursor(int id, String title, String description) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getInt(0)).thenReturn(id);
        when(cursor.getString(1)).thenReturn(title);
        when(cursor.getString(2)).thenReturn(description);
        when(cursor.moveToFirst()).thenReturn(true);

        return cursor;
    }
}