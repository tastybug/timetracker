package com.tastybug.timetracker.extension.backup.controller.localbackup;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logError;

public class LocalBackupService {

    private Context context;

    public LocalBackupService(Context context) {
        this.context = context;
    }

    public void performBackup() {
        new SaveBackupTask(context).run();
    }

    public File getBackupFile() {
        return new BackupFileIO(context).getBackupFile();
    }

    public byte[] getBackupData() {
        try {
            return new BackupFileIO(context).readBackup();
        } catch (IOException e) {
            logError(getClass().getSimpleName(), "Problem accessing backup file.", e);
            return new byte[0];
        }
    }
}
