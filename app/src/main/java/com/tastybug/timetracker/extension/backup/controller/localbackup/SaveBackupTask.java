package com.tastybug.timetracker.extension.backup.controller.localbackup;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

class SaveBackupTask extends TaskPayload {

    private BackupDataCreator backupDataCreator;
    private BackupFileIO backupFileIO;

    SaveBackupTask(Context context) {
        this(context, new BackupDataCreator(context), new BackupFileIO(context));
    }

    private SaveBackupTask(Context context, BackupDataCreator backupDataCreator, BackupFileIO backupFileIO) {
        super(context);
        this.backupDataCreator = backupDataCreator;
        this.backupFileIO = backupFileIO;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        try {
            byte[] data = backupDataCreator.getDataAsByteArray();
            backupFileIO.writeBackup(data);

        } catch (JSONException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to create data export.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write backup data.", e);
        }

        return Collections.emptyList();
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new BackupSavedEvent();
    }

}
