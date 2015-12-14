package com.tastybug.timetracker.model;

import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.util.database.ProjectDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class ProjectTest {

    Context contextMock = mock(Context.class);
    ProjectDAO daoMock = mock(ProjectDAO.class);

    @Test public void canCreateProjectWithTitle() {
        // when
        Project project = new Project("name");

        // then
        assertNotNull(project);
        assertEquals("name", project.getTitle());
    }

    @Test public void noProjectDescriptionIsHandledWell() {
        // given
        Project project = new Project("name");

        // when
        project.setDescription("desc");

        // then
        assertEquals("desc", project.getDescription().get());

        // when
        project.setDescription(null);

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullUuid() {
        // given
        Project project = new Project("name");

        // when
        project.setUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectTitle() {
        // given
        Project project = new Project("name");

        // when
        project.setTitle(null);
    }

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        Project project = new Project("name");
        project.setContext(contextMock);
        project.setDAO(daoMock);

        // when
        project.setUuid("123"); // this does not trigger
        project.setTitle("new title");
        project.setDescription("bla");

        // then
        verify(daoMock, times(2)).update(project);
    }
}