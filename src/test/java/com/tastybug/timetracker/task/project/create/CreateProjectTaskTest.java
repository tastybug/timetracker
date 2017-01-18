package com.tastybug.timetracker.task.project.create;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CreateProjectTaskTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private ProjectFactory projectFactory = mock(ProjectFactory.class);
    private TrackingConfigurationFactory trackingConfigurationFactory = mock(TrackingConfigurationFactory.class);

    @Before
    public void setup() {
        when(projectFactory.aProject(anyString())).thenReturn(new Project(""));
        when(trackingConfigurationFactory.aTrackingConfiguration(anyString())).thenReturn(new TrackingConfiguration(""));
        when(projectDAO.getBatchCreate(any(Project.class))).thenReturn(mock(ContentProviderOperation.class));
        when(trackingConfigurationDAO.getBatchCreate(any(TrackingConfiguration.class))).thenReturn(mock(ContentProviderOperation.class));
    }

    @Test
    public void prepareBatchOperations_returns_batch_saves_describing_created_entities() {
        // given
        Project project = new Project("a title");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid());
        when(projectFactory.aProject("a title")).thenReturn(project);
        when(trackingConfigurationFactory.aTrackingConfiguration(project.getUuid())).thenReturn(trackingConfiguration);
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
    public void preparePostEvent_returns_event_containing_created_project() throws Exception {
        // given
        Project createdProject = mock(Project.class);
        CreateProjectTask task = aTask().withProjectTitle("a title");
        when(projectFactory.aProject("a title")).thenReturn(createdProject);

        // when: doing the actual work
        task.prepareBatchOperations();

        // and: preparing festivities
        ProjectCreatedEvent event = (ProjectCreatedEvent) task.preparePostEvent();

        // then
        assertEquals(createdProject, event.getProject());
    }

    @Test(expected = NullPointerException.class)
    public void validate_yields_NPE_on_missing_project_title() {
        // expect
        aTask().validate();
    }

    @NonNull
    private CreateProjectTask aTask() {
        return new CreateProjectTask(mock(Context.class),
                mock(OttoProvider.class),
                projectDAO,
                trackingConfigurationDAO,
                projectFactory,
                trackingConfigurationFactory);
    }
}