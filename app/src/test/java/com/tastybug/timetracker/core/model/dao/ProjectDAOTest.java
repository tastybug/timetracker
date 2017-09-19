package com.tastybug.timetracker.core.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static com.tastybug.timetracker.core.model.dao.ProjectDAO.CLOSED_COLUMN;
import static com.tastybug.timetracker.core.model.dao.ProjectDAO.CONTRACT_ID_COLUMN;
import static com.tastybug.timetracker.core.model.dao.ProjectDAO.DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.core.model.dao.ProjectDAO.TITLE_COLUMN;
import static com.tastybug.timetracker.core.model.dao.ProjectDAO.UUID_COLUMN;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
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
        Cursor cursor = aProjectCursor("1", "title", "desc", "ABC/123", true);
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), any(String.class), any(String[].class), (String) isNull()))
                .thenReturn(cursor);

        // when
        Project project = projectDAO.get("1").get();

        // then
        assertThat(project).isNotNull();
        assertThat(project.getUuid()).isEqualTo("1");
        assertThat(project.getTitle()).isEqualTo("title");
        assertThat(project.getDescription()).isPresent().contains("desc");
        assertThat(project.getContractId()).isPresent().contains("ABC/123");
        assertThat(project.isClosed()).isTrue();
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
        assertThat(project).isNull();
    }

    @Test
    public void getAllWorksForExistingProjects() {
        // given
        Cursor multipleProjects = aCursorWith2Projects();
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(multipleProjects);

        // when
        List<Project> projects = projectDAO.getAll();

        // then
        assertThat(projects.size()).isEqualTo(2);
    }

    @Test
    public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        when(resolver.query(any(Uri.class), eq(ProjectDAO.COLUMNS), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .thenReturn(noProjects);

        // when
        List<Project> projects = projectDAO.getAll();

        // then
        assertThat(projects).isEmpty();
    }

    @Test
    public void canCreateProject() {
        // given
        Project project = new Project("title");

        // when
        projectDAO.create(project);

        // then
        assertThat(project.getUuid()).isNotNull();
    }

    @Test
    public void canUpdateProject() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        when(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        int updateCount = projectDAO.update(project);

        // then
        assertThat(updateCount).isEqualTo(1);
    }

    @Test
    public void canDeleteProject() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertThat(success).isTrue();
    }

    @Test
    public void canDeleteProjectByUuid() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(1);

        // when
        boolean success = projectDAO.delete(project.getUuid());

        // then
        assertThat(success).isTrue();
    }

    @Test
    public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        Project project = new Project("1", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        when(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).thenReturn(0);

        // when
        boolean success = projectDAO.delete(project);

        // then
        assertThat(success).isFalse();
    }

    @Test
    public void getContentValues_returns_complete_map_with_correct_values() {
        // given
        Project project = new Project("1", "title", Optional.of("desc"), Optional.of("contract"), true);

        // when
        ContentValues cv = projectDAO.getContentValues(project);

        // then
        assertEquals(project.getUuid(), cv.getAsString(UUID_COLUMN));
        assertEquals(project.getTitle(), cv.getAsString(TITLE_COLUMN));
        assertEquals(project.getDescription().get(), cv.getAsString(DESCRIPTION_COLUMN));
        assertEquals(project.getContractId().get(), cv.getAsString(CONTRACT_ID_COLUMN));
        assertEquals(project.isClosed(), cv.getAsBoolean(CLOSED_COLUMN));

        // and
        assertThat(cv.size()).isEqualTo(5);
    }

    @Test
    public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertThat(projectDAO.getPKColumn()).isEqualTo(UUID_COLUMN);
    }

    @Test
    public void knowsAllColumns() {
        // expect
        assertThat(projectDAO.getColumns().length).isEqualTo(5);
        assertThat(projectDAO.getColumns()).contains(UUID_COLUMN, TITLE_COLUMN, DESCRIPTION_COLUMN, CONTRACT_ID_COLUMN, CLOSED_COLUMN);
    }

    private Cursor aProjectCursor(String uuid, String title, String description, String contractId, boolean closed) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn(uuid);
        when(cursor.getString(1)).thenReturn(title);
        when(cursor.getString(2)).thenReturn(description);
        when(cursor.getString(3)).thenReturn(contractId);
        when(cursor.getInt(4)).thenReturn(closed ? 1 : 0);
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
        when(cursor.getString(3)).thenReturn("contract");
        when(cursor.getInt(4)).thenReturn(0);
        when(cursor.moveToNext()).thenReturn(true, true, false);

        return cursor;
    }
}