package com.tastybug.timetracker.task.tracking.delete;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DeleteTrackingRecordTaskTest {

    String affectedTrackingRecordUuid = "123";
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private DeleteTrackingRecordTask subject = new DeleteTrackingRecordTask(
            mock(Context.class),
            trackingRecordDAO).withTrackingRecordUuid(affectedTrackingRecordUuid);

    @Test(expected = NullPointerException.class)
    public void validate_yields_NPE_on_missing_TrackingRecord_uuid() {
        // given
        DeleteTrackingRecordTask subject = new DeleteTrackingRecordTask(
                mock(Context.class),
                trackingRecordDAO);

        // when
        subject.validate();
    }

    @Test
    public void prepareBatchOperations_deletes_requested_TrackingRecord_right_away() {
        // when
        subject.prepareBatchOperations();

        // then
        verify(trackingRecordDAO).delete(affectedTrackingRecordUuid);
    }

    @Test
    public void prepareBatchOperations_returns_no_batch_operations() {
        // when
        List<ContentProviderOperation> operationList = subject.prepareBatchOperations();

        // then
        assertTrue(operationList.isEmpty());
    }

    @Test
    public void preparePostEvent_returns_DeletedTrackingRecordEvent_containing_affected_TrackingRecord_uuid() {
        // when
        subject.prepareBatchOperations();

        // and
        DeletedTrackingRecordEvent event = (DeletedTrackingRecordEvent) subject.preparePostEvent();

        // then
        assertEquals(event.getTrackingRecordUuid(), affectedTrackingRecordUuid);
    }
}