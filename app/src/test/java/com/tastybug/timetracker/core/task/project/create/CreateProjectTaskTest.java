package com.tastybug.timetracker.core.task.project.create;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CreateProjectTaskTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);

    @Before
    public void setup() {
        when(projectDAO.getBatchCreate(any(Project.class))).thenReturn(mock(ContentProviderOperation.class));
        when(trackingConfigurationDAO.getBatchCreate(any(TrackingConfiguration.class))).thenReturn(mock(ContentProviderOperation.class));
    }

    @Test
    public void prepareBatchOperations_sets_title_at_the_created_project() {
        // given
        ArgumentCaptor<Project> argumentCaptorForProject = ArgumentCaptor.forClass(Project.class);
        when(projectDAO.getBatchCreate(argumentCaptorForProject.capture())).thenReturn(mock(ContentProviderOperation.class));
        String title = "a title";
        CreateProjectTask task = aTask().withProjectTitle(title);

        // when
        task.prepareBatchOperations();

        // then
        assertEquals(title, argumentCaptorForProject.getValue().getTitle());
    }

    @Test
    public void prepareBatchOperations_returns_create_operations_in_correct_order() {
        // given
        ContentProviderOperation operationForProject = mock(ContentProviderOperation.class);
        when(projectDAO.getBatchCreate(any(Project.class))).thenReturn(operationForProject);
        ContentProviderOperation operationForTrackingConfiguration = mock(ContentProviderOperation.class);
        when(trackingConfigurationDAO.getBatchCreate(any(TrackingConfiguration.class))).thenReturn(operationForTrackingConfiguration);
        CreateProjectTask task = aTask().withProjectTitle("a title");

        // when
        List<ContentProviderOperation> operationList = task.prepareBatchOperations();

        // then
        assertEquals(2, operationList.size());

        // and
        assertEquals(operationList.get(0), (operationForProject));
        assertEquals(operationList.get(1), operationForTrackingConfiguration);
    }

    @Test
    public void preparePostEvent_returns_ProjectCreatedEvent_containing_created_project() throws Exception {
        // given
        ArgumentCaptor<Project> argumentCaptorForProject = ArgumentCaptor.forClass(Project.class);
        when(projectDAO.getBatchCreate(argumentCaptorForProject.capture())).thenReturn(mock(ContentProviderOperation.class));
        CreateProjectTask task = aTask().withProjectTitle("a title");

        // when: doing the actual work
        task.prepareBatchOperations();

        // and: preparing festivities
        ProjectCreatedEvent event = (ProjectCreatedEvent) task.preparePostEvent();

        // then
        assertEquals(argumentCaptorForProject.getValue(), event.getProject());
    }

    @Test(expected = NullPointerException.class)
    public void validate_yields_NPE_on_missing_project_title() {
        // expect
        aTask().validate();
    }

    @NonNull
    private CreateProjectTask aTask() {
        return new CreateProjectTask(mock(Context.class),
                projectDAO,
                trackingConfigurationDAO);
    }
}