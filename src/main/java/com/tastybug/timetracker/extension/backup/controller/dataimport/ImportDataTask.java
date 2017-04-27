package com.tastybug.timetracker.extension.backup.controller.dataimport;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.json.JSONUnMarshallingBuilder;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.util.ConditionalLog;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class ImportDataTask extends TaskPayload {

    private static final String DATA_URI = "DATA_URI";
    private static final String DATA_BYTE = "DATA_BYTE";

    private DbWipeBatchOpsProvider dbWipeBatchOpsProvider;
    private DbImportBatchOpsProvider dbImportBatchOpsProvider;
    private JSONUnMarshallingBuilder jsonUnMarshallingBuilder;
    private UriToByteArrayHelper uriToByteArrayHelper;

    public ImportDataTask(Context context) {
        this(context,
                new DbWipeBatchOpsProvider(context),
                new DbImportBatchOpsProvider(context),
                new UriToByteArrayHelper(context.getContentResolver()),
                new JSONUnMarshallingBuilder());
    }

    ImportDataTask(Context context,
                   DbWipeBatchOpsProvider dbWipeBatchOpsProvider,
                   DbImportBatchOpsProvider dbImportBatchOpsProvider,
                   UriToByteArrayHelper uriToByteArrayHelper,
                   JSONUnMarshallingBuilder jsonUnMarshallingBuilder) {
        super(context);
        this.dbWipeBatchOpsProvider = dbWipeBatchOpsProvider;
        this.dbImportBatchOpsProvider = dbImportBatchOpsProvider;
        this.uriToByteArrayHelper = uriToByteArrayHelper;
        this.jsonUnMarshallingBuilder = jsonUnMarshallingBuilder;
    }

    public ImportDataTask withDataUri(Uri dataUri) {
        Preconditions.checkNotNull(dataUri);
        arguments.putParcelable(DATA_URI, dataUri);
        return this;
    }

    public ImportDataTask withData(byte[] data) {
        Preconditions.checkArgument(data.length > 0);
        arguments.putByteArray(DATA_BYTE, data);
        return this;
    }

    private byte[] getRawData() {
        if (arguments.containsKey(DATA_URI)) {
            Uri dataUri = arguments.getParcelable(DATA_URI);
            try {
                return uriToByteArrayHelper.readByteArrayFromUri(dataUri);
            } catch (IOException e) {
                ConditionalLog.logError(getClass().getSimpleName(), "Failed to import data.", e);
                throw new RuntimeException("Failed to import data.", e);
            }
        } else {
            return arguments.getByteArray(DATA_BYTE);
        }
    }

    private List<Project> convertDataToImportableProjects(byte[] data) {
        try {
            return jsonUnMarshallingBuilder.withByteArray(data).build();
        } catch (ParseException | JSONException | UnsupportedEncodingException e) {
            ConditionalLog.logError(getClass().getSimpleName(), "Failed to import data.", e);
            throw new RuntimeException("Failed to import data.", e);
        }
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(arguments.getParcelable(DATA_URI) != null || arguments.containsKey(DATA_BYTE));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        byte[] data = getRawData();

        List<Project> importableProjects = convertDataToImportableProjects(data);
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.addAll(dbWipeBatchOpsProvider.getOperations());
        operations.addAll(dbImportBatchOpsProvider.getOperations(importableProjects));

        return operations;
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new ImportedDataEvent();
    }
}
