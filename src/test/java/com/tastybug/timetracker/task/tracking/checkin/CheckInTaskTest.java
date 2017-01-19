package com.tastybug.timetracker.task.tracking.checkin;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CheckInTaskTest {

    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private CheckInTask subject = new CheckInTask(mock(Context.class), trackingRecordDAO).withProjectUuid("1");

    @Before
    public void setup() {
        when(trackingRecordDAO.getRunning("1")).thenReturn(Optional.<TrackingRecord>absent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgumentException_on_missing_project_uuid() {
        // given
        CheckInTask subject = new CheckInTask(mock(Context.class), trackingRecordDAO);

        // when
        subject.validate();
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