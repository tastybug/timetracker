package com.tastybug.timetracker.core.task.project.delete;

import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.core.model.dao.ProjectDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DeleteProjectTaskTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);

    private String projectUuid = "123";
    private DeleteProjectTask subject;

    @Before
    public void setup() {
        when(projectDAO.delete(anyString())).thenReturn(true);
        subject = new DeleteProjectTask(mock(Context.class), projectDAO).withProjectUuid(projectUuid);
    }

    @Test
    public void prepareBatchOperations_leads_to_project_deletion() throws Exception {
        // when
        subject.prepareBatchOperations();

        // then
        verify(projectDAO).delete(projectUuid);
    }

    @Test(expected = RuntimeException.class)
    public void prepareBatchOperations_throws_RuntimeException_on_unsucessful_project_deletion() throws Exception {
        // given
        when(projectDAO.delete(projectUuid)).thenReturn(false);

        // when
        subject.prepareBatchOperations();
    }

    @Test
    public void preparePostEvent_fires_ProjectDeletionEvent() throws Exception {
        // when
        ProjectDeletedEvent event = (ProjectDeletedEvent) subject.preparePostEvent();

        // then
        assertEquals(event.getProjectUuid(), projectUuid);
    }

    @Test(expected = NullPointerException.class)
    public void validate_yields_NPE_without_given_project_uuid() {
        // expect
        new DeleteProjectTask(mock(Context.class), projectDAO).validate();
    }
}
