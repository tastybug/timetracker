package com.tastybug.timetracker.extension.backup.controller.dataexport;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class ExportDataTask extends TaskPayload {

    private DataExportCreator dataExportCreator;
    private byte[] data;

    public ExportDataTask(Context context) {
        this(context, new DataExportCreator(context));
    }

    ExportDataTask(Context context, DataExportCreator dataExportCreator) {
        super(context);
        this.dataExportCreator = dataExportCreator;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        try {
            this.data = dataExportCreator.getDataAsByteArray();
        } catch (JSONException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to create data export.", e);
        }

        return Collections.emptyList();
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new DataExportedEvent(data);
    }
}
