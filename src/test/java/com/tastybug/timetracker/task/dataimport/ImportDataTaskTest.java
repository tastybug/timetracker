package com.tastybug.timetracker.task.dataimport;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ImportDataTaskTest {

    private DbWipeBatchOpsProvider dbWipeBatchOpsProvider = mock(DbWipeBatchOpsProvider.class);
    private DbImportBatchOpsProvider dbImportBatchOpsProvider = mock(DbImportBatchOpsProvider.class);
    private JSONUnMarshallingBuilder jsonUnMarshallingBuilder = mock(JSONUnMarshallingBuilder.class);
    private UriToByteArrayHelper uriToByteArrayHelper = mock(UriToByteArrayHelper.class);

    private ImportDataTask subject = new ImportDataTask(mock(Context.class),
            dbWipeBatchOpsProvider,
            dbImportBatchOpsProvider,
            uriToByteArrayHelper,
            jsonUnMarshallingBuilder).withDataUri(mock(Uri.class));

    @Before
    public void setup() throws Exception {
        when(uriToByteArrayHelper.readByteArrayFromUri(any(Uri.class))).thenReturn(new byte[]{1, 2, 3});
        when(jsonUnMarshallingBuilder.withByteArray(new byte[]{1, 2, 3})).thenReturn(jsonUnMarshallingBuilder);
        ArrayList<Project> importableProjects = new ArrayList<>();
        when(jsonUnMarshallingBuilder.build()).thenReturn(importableProjects);

    }

    @Test(expected = NullPointerException.class)
    public void validate_yields_NPE_if_no_data_uri_is_given() {
        // given
        ImportDataTask subject = new ImportDataTask(mock(Context.class), dbWipeBatchOpsProvider, dbImportBatchOpsProvider, uriToByteArrayHelper, jsonUnMarshallingBuilder);

        // when
        subject.validate();
    }

    @Test
    public void prepareBatchOperations_returns_wipe_and_create_operations_in_correct_order() throws Exception {
        // given
        ContentProviderOperation wipeOperation = mock(ContentProviderOperation.class);
        when(dbWipeBatchOpsProvider.getOperations()).thenReturn(Arrays.asList(wipeOperation));
        ContentProviderOperation createOperation = mock(ContentProviderOperation.class);
        when(dbImportBatchOpsProvider.getOperations(anyList())).thenReturn(Arrays.asList(createOperation));

        // when
        List<ContentProviderOperation> operationList = subject.prepareBatchOperations();

        // then
        assertEquals(2, operationList.size());

        // and
        assertEquals(wipeOperation, operationList.get(0));
        assertEquals(createOperation, operationList.get(1));
    }
}