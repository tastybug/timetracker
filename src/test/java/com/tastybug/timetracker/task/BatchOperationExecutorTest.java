package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.Build;
import android.os.RemoteException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class BatchOperationExecutorTest {

    private static final String AUTHORITY = "com.tastybug.timetracker";

    private ContentResolver contentResolver = mock(ContentResolver.class);
    private BatchOperationExecutor subject = new BatchOperationExecutor(contentResolver);

    @Test
    public void executeBatch_hands_over_operation_list_to_contentresolver() throws Exception {
        // when
        subject.executeBatch(Arrays.asList(mock(ContentProviderOperation.class)));

        // then
        verify(contentResolver).applyBatch(eq(AUTHORITY), isA(ArrayList.class));
    }

    @Test(expected = RuntimeException.class)
    public void executeBatch_rethrows_RemoteExceptions_as_RuntimeException() throws Exception {
        // given
        when(contentResolver.applyBatch(eq(AUTHORITY), any(ArrayList.class))).thenThrow(new RemoteException());

        // when
        subject.executeBatch(Arrays.asList(mock(ContentProviderOperation.class)));
    }

    @Test(expected = RuntimeException.class)
    public void executeBatch_rethrows_OperationApplicationException_as_RuntimeException() throws Exception {
        // given
        when(contentResolver.applyBatch(eq(AUTHORITY), any(ArrayList.class))).thenThrow(new OperationApplicationException());

        // when
        subject.executeBatch(Arrays.asList(mock(ContentProviderOperation.class)));
    }
}