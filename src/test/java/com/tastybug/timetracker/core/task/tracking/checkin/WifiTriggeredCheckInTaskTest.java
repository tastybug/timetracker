package com.tastybug.timetracker.core.task.tracking.checkin;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class WifiTriggeredCheckInTaskTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private CheckInTask subject = new CheckInTask(mock(Context.class), projectDAO, trackingRecordDAO).withProjectUuid("1");

    @Before
    public void setup() {
        Project project = mock(Project.class);
        when(project.isClosed()).thenReturn(false);
        when(projectDAO.get(anyString())).thenReturn(Optional.of(project));
        when(trackingRecordDAO.getRunning("1")).thenReturn(Optional.<TrackingRecord>absent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgumentException_on_missing_project_uuid() {
        // given
        CheckInTask subject = new CheckInTask(mock(Context.class), projectDAO, trackingRecordDAO);

        // when
        subject.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void prepareBatchOperations_throws_IllegalArgument_on_project_that_is_closed() {
        // given
        Project project = mock(Project.class);
        when(project.isClosed()).thenReturn(true);
        when(projectDAO.get(anyString())).thenReturn(Optional.of(project));

        // when
        subject.prepareBatchOperations();
    }

    @Test(expected = IllegalArgumentException.class)
    public void prepareBatchOperations_throws_IllegalArgument_on_project_that_is_already_running() {
        // given
        when(trackingRecordDAO.getRunning("1")).thenReturn(Optional.of(new TrackingRecord("123")));

        // when
        subject.prepareBatchOperations();
    }

    @Test
    public void prepareBatchOperations_returns_creation_operation_for_new_TrackingRecord() {
        // given
        ContentProviderOperation createOperation = mock(ContentProviderOperation.class);
        when(trackingRecordDAO.getBatchCreate(any(TrackingRecord.class))).thenReturn(createOperation);

        // when
        List<ContentProviderOperation> operationList = subject.prepareBatchOperations();

        // then
        assertEquals(1, operationList.size());

        // and
        assertEquals(createOperation, operationList.get(0));
    }

    @Test
    public void prepareBatchOperations_starts_the_TrackingRecord_that_is_to_be_created() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));

        // when
        subject.prepareBatchOperations();

        // then
        assertTrue(argumentCaptor.getValue().isRunning());
    }

    @Test
    public void preparePostEvent_returns_CheckInEvent_containing_affected_TrackingRecord() {
        // when
        subject.prepareBatchOperations();

        // and
        CheckInEvent event = (CheckInEvent) subject.preparePostEvent();

        // then
        assertNotNull(event.getTrackingRecord());
    }
}