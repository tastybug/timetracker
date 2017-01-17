package com.tastybug.timetracker.task.project.config;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.squareup.otto.Bus;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ConfigureProjectTaskTest {


    private Context context = mock(Context.class);
    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private Project project = new Project("1", "title", Optional.<String>absent());
    private TrackingConfiguration trackingConfiguration = new TrackingConfiguration("1");

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(mock(ContentResolver.class));
        when(projectDAO.get(project.getUuid())).thenReturn(Optional.of(project));
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(trackingConfiguration));
        when(projectDAO.getBatchUpdate(any(Project.class))).thenReturn(mock(ContentProviderOperation.class));
        when(trackingConfigurationDAO.getBatchUpdate(any(TrackingConfiguration.class))).thenReturn(mock(ContentProviderOperation.class));
    }

    @Test(expected = NullPointerException.class)
    public void validateArguments_yields_NPE_on_null_project_uuid() {
        // given
        ConfigureProjectTask task = new ConfigureProjectTask(context);

        // expect
        task.execute();
    }

    @Test
    public void every_run_leads_to_update_of_project_and_tracking_configuration() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1");

        // when
        subject.execute();

        // then
        verify(projectDAO).getBatchUpdate(isA(Project.class));
        verify(trackingConfigurationDAO).getBatchUpdate(isA(TrackingConfiguration.class));
    }

    @Test
    public void every_run_leads_is_announced_via_otto_event_that_points_to_affected_project() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        OttoProvider ottoProvider = mock(OttoProvider.class);
        Bus bus = mock(Bus.class);
        when(ottoProvider.getSharedBus()).thenReturn(bus);
        subject = subject.withProjectUuid("1");
        subject.setOttoProvider(ottoProvider);

        // when
        subject.execute();

        // then
        ArgumentCaptor<ProjectConfiguredEvent> savedCaptor = ArgumentCaptor.forClass(ProjectConfiguredEvent.class);
        verify(bus).post(savedCaptor.capture());
        assertEquals("1", savedCaptor.getValue().getProjectUuid());
    }

    @Test
    public void can_change_project_title() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withProjectTitle("some other title");

        // when
        subject.execute();

        // then
        assertEquals(project.getTitle(), "some other title");
    }

    @Test
    public void can_change_project_description() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withProjectDescription("new description");

        // when
        subject.execute();

        // then
        assertEquals(project.getDescription().get(), "new description");
    }

    @Test
    public void can_remove_project_description() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withoutProjectDescription();

        // when
        subject.execute();

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test
    public void can_alter_hour_limit() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withHourLimit(133337);

        // when
        subject.execute();

        // then
        assertEquals(133337, (long) trackingConfiguration.getHourLimit().get());
    }

    @Test
    public void can_remove_hour_limit() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withoutHourLimit();

        // when
        subject.execute();

        // then
        assertFalse(trackingConfiguration.getHourLimit().isPresent());
    }

    @Test
    public void can_alter_start_date() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        subject = subject.withProjectUuid("1")
                .withStartDate(new Date(5));

        // when
        subject.execute();

        // then
        assertEquals(new Date(5), trackingConfiguration.getStart().get());
    }

    @Test
    public void can_alter_end_date() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        DateTime inclusiveEndDate = new DateTime(1000000);
        subject = subject.withProjectUuid("1")
                .withInclusiveEndDate(inclusiveEndDate.toDate());

        // when
        subject.execute();

        // then
        assertEquals(inclusiveEndDate.plusDays(1).toDate(), trackingConfiguration.getEnd().get());
    }

    @Test
    public void can_alter_prompt_for_description() {
        // given
        ConfigureProjectTask subject = new ConfigureProjectTask(context, projectDAO, trackingConfigurationDAO);
        boolean newState = !trackingConfiguration.isPromptForDescription();
        subject = subject.withProjectUuid("1")
                .withPromptForDescription(newState);

        // when
        subject.execute();

        // then
        assertEquals(newState, trackingConfiguration.isPromptForDescription());
    }
}