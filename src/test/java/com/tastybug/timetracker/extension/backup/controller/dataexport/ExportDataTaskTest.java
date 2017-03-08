package com.tastybug.timetracker.extension.backup.controller.dataexport;

import android.content.Context;

import org.json.JSONException;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExportDataTaskTest {

    DataExportCreator dataExportCreator = mock(DataExportCreator.class);

    ExportDataTask subject = new ExportDataTask(mock(Context.class), dataExportCreator);

    @Test
    public void prepareBatchOperations_creates_bytearray_via_DataExportCreator() throws Exception {
        // when
        subject.prepareBatchOperations();

        // then
        verify(dataExportCreator).getDataAsByteArray();
    }

    @Test(expected = RuntimeException.class)
    public void prepareBatchOperations_yields_RuntimeException_on_JsonException_from_DataExportCreator() throws Exception {
        // given
        when(dataExportCreator.getDataAsByteArray()).thenThrow(new JSONException(""));

        // when
        subject.prepareBatchOperations();
    }

    @Test(expected = RuntimeException.class)
    public void prepareBatchOperations_yields_RuntimeException_on_UnsupportedEncodingException_from_DataExportCreator() throws Exception {
        // given
        when(dataExportCreator.getDataAsByteArray()).thenThrow(new UnsupportedEncodingException(""));

        // when
        subject.prepareBatchOperations();
    }

    @Test
    public void preparePostEvent_returns_DataExportedEvent() throws Exception {
        // when
        DataExportedEvent event = (DataExportedEvent) subject.preparePostEvent();

        // then
        assertNotNull(event);
    }
}