package com.tastybug.timetracker.task.dataexport;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;

import com.tastybug.timetracker.task.AbstractAsyncTask;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

// TODO rename to ExportDataTask, also other classes
public class CreateDataExportTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private DataExportCreator dataExportCreator;
    private byte[] data;

    public CreateDataExportTask(Context context) {
        this(context, new DataExportCreator(context));
    }

    CreateDataExportTask(Context context, DataExportCreator dataExportCreator) {
        super(context);
        this.dataExportCreator = dataExportCreator;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
    }

    @Override
    protected List<ContentProviderOperation> performBackgroundStuff(Bundle args) {
        try {
            this.data = dataExportCreator.getDataAsByteArray();
        } catch (JSONException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to create data export.", e);
        }

        return Collections.emptyList();
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Deleted project " + arguments.getString(PROJECT_UUID));
        ottoProvider.getSharedBus().post(new DataExportedEvent(data));
    }

}
