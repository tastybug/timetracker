package com.tastybug.timetracker.core.task.tracking.update;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class UpdateTrackingRecordTaskTest {


    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private TrackingRecord affectedTrackingRecord = new TrackingRecord();
    private UpdateTrackingRecordTask subject = aValidTask();

    private UpdateTrackingRecordTask aValidTask() {
        return new UpdateTrackingRecordTask(mock(Context.class), trackingRecordDAO).withTrackingRecordUuid("123");
    }

    @Before
    public void setup() {
        when(trackingRecordDAO.get(anyString())).thenReturn(Optional.of(affectedTrackingRecord));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_yields_IllegalArgument_on_missing_TrackingRecord_uuid() {
        // given
        UpdateTrackingRecordTask task = new UpdateTrackingRecordTask(mock(Context.class), trackingRecordDAO);

        // when
        task.validate();
    }

    @Test
    public void can_change_start_date() {
        // given
        Date newStartDate = new Date(123);
        subject = subject.withStartDate(newStartDate);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(newStartDate, affectedTrackingRecord.getStart().get());
    }

    @Test
    public void can_change_end_date() {
        // given
        Date newEndDate = new Date(123);
        subject = subject.withEndDate(newEndDate);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(newEndDate, affectedTrackingRecord.getEnd().get());
    }

    @Test
    public void can_change_description() {
        // given
        String newDescription = "aaasdasd";
        subject = subject.withDescription(Optional.of(newDescription));

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(newDescription, affectedTrackingRecord.getDescription().get());
    }

    @Test
    public void prepareBatchOperations_returns_update_batch_operation_for_affected_TrackingRecord() {
        // given
        ContentProviderOperation updateOperation = mock(ContentProviderOperation.class);
        when(trackingRecordDAO.getBatchUpdate(affectedTrackingRecord)).thenReturn(updateOperation);

        // when
        List<ContentProviderOperation> operationList = subject.prepareBatchOperations();

        // then
        assertEquals(1, operationList.size());

        // and
        assertEquals(updateOperation, operationList.get(0));
    }

    @Test
    public void preparePostEvent_returns_ModifiedTrackingRecordEvent_containing_affected_TrackingRecord() {
        // when
        subject.prepareBatchOperations();

        // and
        UpdateTrackingRecordEvent event = (UpdateTrackingRecordEvent) subject.preparePostEvent();

        // then
        assertEquals(affectedTrackingRecord, event.getTrackingRecord());
    }

    @Test
    public void preparePostEvent_can_tell_if_TrackingRecord_was_just_stopped() {
        // given
        subject = subject.withEndDate(new DateTime().plusDays(1).toDate());
        affectedTrackingRecord.start(); // must be running to be stoppable

        // when
        subject.prepareBatchOperations();

        // and
        UpdateTrackingRecordEvent event = (UpdateTrackingRecordEvent) subject.preparePostEvent();

        // then
        assertTrue(event.wasStopped());
    }

    @Test
    public void preparePostEvent_can_tell_if_TrackingRecord_was_NOT_just_stopped() {
        // given
        subject = subject.withStartDate(new Date()).withDescription(Optional.of("asdasd"));

        // when
        subject.prepareBatchOperations();

        // and
        UpdateTrackingRecordEvent event = (UpdateTrackingRecordEvent) subject.preparePostEvent();

        // then
        assertFalse(event.wasStopped());
    }
}