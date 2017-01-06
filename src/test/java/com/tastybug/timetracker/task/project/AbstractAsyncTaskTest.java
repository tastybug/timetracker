package com.tastybug.timetracker.task.project;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;

import com.tastybug.timetracker.task.AbstractAsyncTask;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractAsyncTaskTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test(expected = RuntimeException.class)
    public void OperationApplicationExceptionDuringPersistLeadToRuntimeException() throws Exception {
        // given
        TestTask task = new TestTask(context);
        when(resolver.applyBatch(any(String.class), any(ArrayList.class))).thenThrow(OperationApplicationException.class);

        // when
        task.execute();
    }

    @Test(expected = RuntimeException.class)
    public void RemoteExceptionDuringPersistLeadToRuntimeException() throws Exception {
        // given
        TestTask task = new TestTask(context);
        when(resolver.applyBatch(any(String.class), any(ArrayList.class))).thenThrow(RemoteException.class);

        // when
        task.execute();
    }

    static class TestTask extends AbstractAsyncTask {

        TestTask(Context context) {
            super(context);
        }

        @Override
        protected void validateArguments() throws NullPointerException {

        }

        @Override
        protected List<ContentProviderOperation> performBackgroundStuff(Bundle args) {
            return Collections.emptyList();
        }
    }
}