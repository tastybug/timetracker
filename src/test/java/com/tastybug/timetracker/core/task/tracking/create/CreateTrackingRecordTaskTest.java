package com.tastybug.timetracker.core.task.tracking.create;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CreateTrackingRecordTaskTest {

    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private CreateTrackingRecordTask subject = aCompleteTaskInstance();

    private CreateTrackingRecordTask aCompleteTaskInstance() {
        return new CreateTrackingRecordTask(mock(Context.class),
                trackingRecordDAO).withProjectUuid("1").withStartDate(new Date(1)).withEndDate(new Date(100000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgument_on_missing_project_uuid() {
        // given
        CreateTrackingRecordTask subject = new CreateTrackingRecordTask(mock(Context.class),
                trackingRecordDAO).withStartDate(new Date()).withEndDate(new Date()).withRoundingStrategy(Rounding.Strategy.NO_ROUNDING);

        // when
        subject.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgument_on_missing_start_date() {
        // given
        CreateTrackingRecordTask subject = new CreateTrackingRecordTask(mock(Context.class),
                trackingRecordDAO).withProjectUuid("1").withEndDate(new Date()).withRoundingStrategy(Rounding.Strategy.NO_ROUNDING);

        // when
        subject.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgument_on_missing_end_date() {
        // given
        CreateTrackingRecordTask subject = new CreateTrackingRecordTask(mock(Context.class),
                trackingRecordDAO).withStartDate(new Date()).withProjectUuid("1").withRoundingStrategy(Rounding.Strategy.NO_ROUNDING);

        // when
        subject.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_throws_IllegalArgument_on_missing_rounding_strategy() {
        // given
        CreateTrackingRecordTask subject = new CreateTrackingRecordTask(mock(Context.class),
                trackingRecordDAO).withStartDate(new Date()).withEndDate(new Date()).withProjectUuid("1");

        // when
        subject.validate();
    }

    @Test
    public void can_set_description() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));
        String expectedDescription = "a description";
        subject = subject.withDescription(Optional.of(expectedDescription));

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(argumentCaptor.getValue().getDescription().get(), expectedDescription);
    }

    @Test
    public void will_set_project_uuid_on_created_TrackingRecord() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));
        String expectedProjectUuid = "123";
        subject = subject.withProjectUuid(expectedProjectUuid);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(argumentCaptor.getValue().getProjectUuid(), expectedProjectUuid);
    }

    @Test
    public void will_set_start_date_on_created_TrackingRecord() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));
        Date expectedStartDate = new Date(2);
        subject = subject.withStartDate(expectedStartDate);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(argumentCaptor.getValue().getStart().get(), expectedStartDate);
    }

    @Test
    public void will_set_end_date_on_created_TrackingRecord() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));
        Date expectedEndDate = new Date(5);
        subject = subject.withEndDate(expectedEndDate);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(argumentCaptor.getValue().getEnd().get(), expectedEndDate);
    }

    @Test
    public void will_set_rounding_strategy_on_created_TrackingRecord() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));
        Rounding.Strategy expectedRoundingStrategy = Rounding.Strategy.NO_ROUNDING;
        subject = subject.withRoundingStrategy(expectedRoundingStrategy);

        // when
        subject.prepareBatchOperations();

        // then
        assertEquals(argumentCaptor.getValue().getRoundingStrategy(), expectedRoundingStrategy);
    }

    @Test
    public void prepareBatchOperations_returns_create_operation_for_new_TrackingRecord() {
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
    public void preparePostEvent_returns_CreatedTrackingRecordEvent_containing_created_TrackingRecord() {
        // given
        ArgumentCaptor<TrackingRecord> argumentCaptor = ArgumentCaptor.forClass(TrackingRecord.class);
        when(trackingRecordDAO.getBatchCreate(argumentCaptor.capture())).thenReturn(mock(ContentProviderOperation.class));

        // when
        subject.prepareBatchOperations();

        // and
        CreatedTrackingRecordEvent event = (CreatedTrackingRecordEvent) subject.preparePostEvent();

        // then
        assertEquals(event.getTrackingRecord(), argumentCaptor.getValue());
    }
}