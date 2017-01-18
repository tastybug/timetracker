package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class NGAsyncTaskTest {

    private BatchOperationExecutor batchOperationExecutor = mock(BatchOperationExecutor.class);
    private TaskPayload payload = mock(TaskPayload.class);

    @Test
    public void RUN_executes_task_payload_in_correct_order() {
        // given
        NGAsyncTask ngAsyncTask = new NGAsyncTask(batchOperationExecutor, payload);

        // when
        ngAsyncTask.run();

        // then
        InOrder inOrder = inOrder(payload, payload);
        inOrder.verify(payload).prepareBatchOperations();
        inOrder.verify(payload).firePostEvent();
    }

    @Test
    public void RUN_hands_over_prepared_batch_operations_to_executor() {
        // given
        List<ContentProviderOperation> operationList = mock(ArrayList.class);
        NGAsyncTask ngAsyncTask = new NGAsyncTask(batchOperationExecutor, payload);
        when(payload.prepareBatchOperations()).thenReturn(operationList);

        // when
        ngAsyncTask.run();

        // then
        verify(batchOperationExecutor).executeBatch(operationList);

    }

}