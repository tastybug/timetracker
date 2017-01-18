package com.tastybug.timetracker.task.tracking.checkout;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CheckOutTaskTest {

    private TrackingRecord trackingRecordToBeStopped = mock(TrackingRecord.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private CheckOutTask subject = new CheckOutTask(mock(Context.class), mock(OttoProvider.class), trackingRecordDAO)
            .withProjectUuid("1");

    @Before
    public void setup() {
        when(trackingRecordDAO.getRunning("1")).thenReturn(Optional.of(trackingRecordToBeStopped));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgumentException_on_missing_project_uuid() {
        // given
        CheckOutTask subject = new CheckOutTask(mock(Context.class), mock(OttoProvider.class), trackingRecordDAO);

        // when
        subject.validate();
    }

    @Test
    public void prepareBatchOperations_stops_running_TrackingRecord() {
        /// when
        subject.prepareBatchOperations();

        // then
        verify(trackingRecordToBeStopped).stop();
    }

    @Test
    public void prepareBatchOperations_returns_update_operation_for_altered_TrackingRecord() {
        // given
        ContentProviderOperation updateOperation = mock(ContentProviderOperation.class);
        when(trackingRecordDAO.getBatchUpdate(trackingRecordToBeStopped)).thenReturn(updateOperation);

        // when
        List<ContentProviderOperation> operationList = subject.prepareBatchOperations();

        // then
        assertEquals(1, operationList.size());

        // and
        assertEquals(updateOperation, operationList.get(0));
    }

    @Test
    public void preparePostEvent_returns_CheckOutEvent_containing_affected_TrackingRecord() {
        // when
        subject.prepareBatchOperations();

        // and
        CheckOutEvent event = (CheckOutEvent) subject.preparePostEvent();

        // then
        assertNotNull(event.getTrackingRecord());
    }
}