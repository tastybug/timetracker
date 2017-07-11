package com.tastybug.timetracker.core.task.project.update;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class UpdateProjectTaskTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private UpdateProjectTask subject = new UpdateProjectTask(mock(Context.class),
            projectDAO,
            trackingConfigurationDAO).withProjectUuid("1");
    private Project project = new Project("1", "title", Optional.<String>absent(), Optional.<String>absent(), false);
    private TrackingConfiguration trackingConfiguration = new TrackingConfiguration("1");

    @Before
    public void setup() {
        when(projectDAO.get(project.getUuid())).thenReturn(Optional.of(project));
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(trackingConfiguration));
        when(projectDAO.getBatchUpdate(any(Project.class))).thenReturn(mock(ContentProviderOperation.class));
        when(trackingConfigurationDAO.getBatchUpdate(any(TrackingConfiguration.class))).thenReturn(mock(ContentProviderOperation.class));
    }

    @Test(expected = NullPointerException.class)
    public void validateArguments_yields_NPE_on_null_project_uuid() {
        // given
        UpdateProjectTask task = new UpdateProjectTask(mock(Context.class),
                projectDAO,
                trackingConfigurationDAO);

        // expect
        task.validate();
    }

    @Test
    public void prepareBatchOperations_leads_to_update_of_project_and_tracking_configuration() {
        // when
        subject.prepareBatchOperations();

        // then
        verify(projectDAO).getBatchUpdate(isA(Project.class));
        verify(trackingConfigurationDAO).getBatchUpdate(isA(TrackingConfiguration.class));
    }

    @Test
    public void preparePostEvent_returns_event_containing_affected_project_uuid() {
        // when
        subject.prepareBatchOperations();

        // and
        UpdateProjectEvent event = (UpdateProjectEvent) subject.preparePostEvent();

        // then
        assertEquals(project.getUuid(), event.getProjectUuid());
    }

    @Test
    public void can_change_project_title() {
        // given
        subject = subject.withProjectTitle("some other title");

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(project.getTitle(), "some other title");
    }

    @Test
    public void can_change_project_closed_state() {
        // given
        subject = subject.withClosureState(!project.isClosed());
        boolean wasClosed = project.isClosed();

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(!wasClosed, project.isClosed());
    }

    @Test
    public void can_change_project_description() {
        // given
        subject = subject.withProjectDescription("new description");

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(project.getDescription().get(), "new description");
    }

    @Test
    public void can_change_contract_id() {
        // given
        subject = subject.withContractId("new contract id");

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(project.getContractId().get(), "new contract id");
    }

    @Test
    public void can_remove_project_description() {
        // given
        subject = subject.withoutProjectDescription();

        // when
        subject.prepareBatchOperations();

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test
    public void can_remove_contract_id() {
        // given
        subject = subject.withoutContractId();

        // when
        subject.prepareBatchOperations();

        // then
        assertFalse(project.getContractId().isPresent());
    }

    @Test
    public void can_alter_hour_limit() {
        // given
        subject = subject.withHourLimit(133337);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(133337, (long) trackingConfiguration.getHourLimit().get());
    }

    @Test
    public void can_remove_hour_limit() {
        // given
        subject = subject.withoutHourLimit();

        // when
        subject.prepareBatchOperations();

        // then
        assertFalse(trackingConfiguration.getHourLimit().isPresent());
    }

    @Test
    public void can_alter_start_date() {
        // given
        subject = subject.withStartDate(new Date(5));

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(new Date(5), trackingConfiguration.getStart().get());
    }

    @Test
    public void can_alter_end_date() {
        // given
        DateTime inclusiveEndDate = new DateTime(1000000);
        subject = subject.withInclusiveEndDate(inclusiveEndDate.toDate());

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(inclusiveEndDate.plusDays(1).toDate(), trackingConfiguration.getEnd().get());
    }

    @Test
    public void can_alter_prompt_for_description() {
        // given
        boolean newState = !trackingConfiguration.isPromptForDescription();
        subject = subject.withPromptForDescription(newState);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(newState, trackingConfiguration.isPromptForDescription());
    }
}