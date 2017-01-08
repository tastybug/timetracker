package com.tastybug.timetracker.task.dataimport;

import android.content.ContentResolver;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ImportDataTaskTest {

    private Context context = mock(Context.class);
    private DbWipeBatchOpsProvider dbWipeBatchOpsProvider = mock(DbWipeBatchOpsProvider.class);
    private DbImportBatchOpsProvider dbImportBatchOpsProvider = mock(DbImportBatchOpsProvider.class);
    private JSONUnMarshallingBuilder jsonUnMarshallingBuilder = mock(JSONUnMarshallingBuilder.class);
    private UriToByteArrayHelper uriToByteArrayHelper = mock(UriToByteArrayHelper.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(mock(ContentResolver.class));
    }

    @Test(expected = NullPointerException.class)
    public void not_setting_data_uri_yields_NPE() {
        // given
        ImportDataTask subject = new ImportDataTask(context, dbWipeBatchOpsProvider, dbImportBatchOpsProvider, uriToByteArrayHelper, jsonUnMarshallingBuilder);

        // when
        subject.execute();
    }

    @Test
    public void performBackgroundStuff_happy_path() throws Exception {
        // given
        ImportDataTask subject = new ImportDataTask(context, dbWipeBatchOpsProvider, dbImportBatchOpsProvider, uriToByteArrayHelper, jsonUnMarshallingBuilder);
        when(uriToByteArrayHelper.readByteArrayFromUri(any(Uri.class))).thenReturn(new byte[]{1, 2, 3});
        when(jsonUnMarshallingBuilder.withByteArray(new byte[]{1, 2, 3})).thenReturn(jsonUnMarshallingBuilder);
        ArrayList<Project> importableProjects = new ArrayList<>();
        when(jsonUnMarshallingBuilder.build()).thenReturn(importableProjects);
        subject = subject.withDataUri(mock(Uri.class));

        // when
        subject.execute();

        // then
        verify(dbWipeBatchOpsProvider).getOperations();
        verify(dbImportBatchOpsProvider).getOperations(importableProjects);
    }
}