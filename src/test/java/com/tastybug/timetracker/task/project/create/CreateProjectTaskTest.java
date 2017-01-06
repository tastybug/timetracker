package com.tastybug.timetracker.task.project.create;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CreateProjectTaskTest {

    private Context context = mock(Context.class);
    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private ProjectFactory projectFactory = mock(ProjectFactory.class);
    private TrackingConfigurationFactory trackingConfigurationFactory = mock(TrackingConfigurationFactory.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(mock(ContentResolver.class));
        when(projectFactory.aProject(anyString())).thenReturn(new Project(""));
        when(trackingConfigurationFactory.aTrackingConfiguration(anyString())).thenReturn(new TrackingConfiguration(""));
        when(projectDAO.getBatchCreate(any(Project.class))).thenReturn(mock(ContentProviderOperation.class));
        when(trackingConfigurationDAO.getBatchCreate(any(TrackingConfiguration.class))).thenReturn(mock(ContentProviderOperation.class));
    }

    @Test
    public void happyPath_creates_entities_and_prepares_batch_saves() {
        // given
        Project project = new Project("a title");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid());
        when(projectFactory.aProject("a title")).thenReturn(project);
        when(trackingConfigurationFactory.aTrackingConfiguration(project.getUuid())).thenReturn(trackingConfiguration);

        CreateProjectTask task = aTask().withProjectTitle("a title");

        // when
        task.execute();

        // then: objects by factory are mapped to operations which in turn are run later on
        verify(projectDAO).getBatchCreate(project);
        verify(trackingConfigurationDAO).getBatchCreate(trackingConfiguration);
    }

    @Test
    public void happyPath_is_announced_via_otto() throws Exception {
        // given
        Bus ottoBus = mock(Bus.class);
        OttoProvider ottoProvider = mock(OttoProvider.class);
        when(ottoProvider.getSharedBus()).thenReturn(ottoBus);
        CreateProjectTask task = aTask().withProjectTitle("a title");
        task.setOttoProvider(ottoProvider);

        // when
        task.execute();

        // then
        verify(ottoBus).post(isA(ProjectCreatedEvent.class));
    }

    @Test(expected = NullPointerException.class)
    public void lack_of_project_title_yields_NPE() {
        // expect
        aTask().execute();
    }

    @NonNull
    private CreateProjectTask aTask() {
        return new CreateProjectTask(context,
                projectDAO,
                trackingConfigurationDAO,
                projectFactory,
                trackingConfigurationFactory);
    }
}