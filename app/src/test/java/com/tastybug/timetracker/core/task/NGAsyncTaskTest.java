package com.tastybug.timetracker.core.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.squareup.otto.Bus;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class NGAsyncTaskTest {

    private Context context = mock(Context.class);
    private OttoProvider ottoProvider = mock(OttoProvider.class);
    private Bus ottoBus = mock(Bus.class);
    private BatchOperationExecutor batchOperationExecutor = mock(BatchOperationExecutor.class);
    private TaskPayload payload = mock(TaskPayload.class);
    private NGAsyncTask subject = new NGAsyncTask(context, ottoProvider, batchOperationExecutor, payload);

    @Before
    public void setup() {
        when(ottoProvider.getSharedBus()).thenReturn(ottoBus);
        when(payload.preparePostEvent()).thenReturn(mock(OttoEvent.class));
    }

    @Test
    public void RUN_executes_task_payload_in_correct_order() {
        // when
        subject.run();

        // then
        InOrder inOrder = inOrder(payload, payload, ottoBus);
        inOrder.verify(payload).prepareBatchOperations();
        inOrder.verify(payload).preparePostEvent();
        inOrder.verify(ottoBus).post(any(OttoEvent.class));
    }

    @Test
    public void RUN_hands_over_prepared_batch_operations_to_executor() {
        // given
        List<ContentProviderOperation> operationList = new ArrayList<>();
        when(payload.prepareBatchOperations()).thenReturn(operationList);

        // when
        subject.run();

        // then
        verify(batchOperationExecutor).executeBatch(operationList);
    }

    @Test
    public void RUN_will_broadcast_lifecycle_event_depending_on_otto_event_type() {
        // given
        LifecycleEvent lifeCycleOttoEvent = mock(LifecycleEvent.class);
        Intent intent = mock(Intent.class);
        when(lifeCycleOttoEvent.getAsBroadcastEvent()).thenReturn(intent);
        when(payload.preparePostEvent()).thenReturn(lifeCycleOttoEvent);


        // when
        subject.run();

        // then
        verify(context).sendBroadcast(eq(intent));
    }

}