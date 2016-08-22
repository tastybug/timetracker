package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLog;
import com.tastybug.timetracker.model.Project;

import java.io.IOException;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logError;

public class InternalRestoreService {

    private static final String TAG = InternalRestoreService.class.getSimpleName();

    private BackupDateAccessor backupDateAccessor = new BackupDateAccessor();
    private BackupDataReader backupDataReader = new BackupDataReader();
    private BackupDataImporter backupDataImporter;
    private BackupLog backupLog;

    public InternalRestoreService(Context context) {
        this.backupLog = new BackupLog(context);
        this.backupDataImporter = new BackupDataImporter(context);
    }

    public InternalRestoreService(BackupDateAccessor backupDateAccessor,
                                  BackupDataReader backupDataReader,
                                  BackupDataImporter backupDataImporter,
                                  BackupLog backupLog) {
        this.backupDateAccessor = backupDateAccessor;
        this.backupDataReader = backupDataReader;
        this.backupDataImporter = backupDataImporter;
        this.backupLog = backupLog;
    }

    public void performRestore(BackupDataInput data,
                               int appVersionCode,
                               ParcelFileDescriptor newState) throws IOException {
        try {
            List<Project> projects = backupDataReader.readBackup(data);
            backupDataImporter.restoreProjectList(projects);
            backupDateAccessor.writeBackupDate(newState);
            backupLog.logRestoreSuccess(appVersionCode);
        } catch (IOException ioe) {
            logError(TAG, "Error while restoring: " + ioe.getMessage(), ioe);
            backupLog.logRestoreFail(appVersionCode, ioe.getMessage());
            throw ioe;
        } catch (Exception e) {
            logError(TAG, "Error while restoring: " + e.getMessage(), e);
            backupLog.logRestoreFail(appVersionCode, e.getMessage());
        }
    }
}
