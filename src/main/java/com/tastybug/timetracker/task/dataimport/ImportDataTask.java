package com.tastybug.timetracker.task.dataimport;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;
import com.tastybug.timetracker.task.AbstractAsyncTask;
import com.tastybug.timetracker.util.ConditionalLog;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;


public class ImportDataTask extends AbstractAsyncTask {

    private static final String DATA_URI = "DATA_URI";

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

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getParcelable(DATA_URI));
    }

    @Override
    protected List<ContentProviderOperation> performBackgroundStuff(Bundle args) {
        byte[] data = getRawData();
        List<Project> importableProjects = convertDataToImportableProjects(data);
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.addAll(dbWipeBatchOpsProvider.getOperations());
        operations.addAll(dbImportBatchOpsProvider.getOperations(importableProjects));

        return operations;
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Imported data from " + arguments.getSerializable(DATA_URI));
        ottoProvider.getSharedBus().post(new ImportedDataEvent());
    }

    private byte[] getRawData() {
        Uri dataUri = arguments.getParcelable(DATA_URI);
        try {
            return uriToByteArrayHelper.readByteArrayFromUri(dataUri);
        } catch (IOException e) {
            ConditionalLog.logError(getClass().getSimpleName(), "Failed to import data.", e);
            throw new RuntimeException("Failed to import data.", e);
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
}
