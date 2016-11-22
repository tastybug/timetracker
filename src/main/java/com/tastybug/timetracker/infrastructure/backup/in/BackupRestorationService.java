package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLogHelper;
import com.tastybug.timetracker.model.Project;

import java.io.IOException;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logError;

public class BackupRestorationService {

    private static final String TAG = BackupRestorationService.class.getSimpleName();

    private BackupDateAccessor backupDateAccessor = new BackupDateAccessor();
    private DataUnmarshaller dataUnmarshaller = new DataUnmarshaller();
    private DatabaseRestorationManager databaseRestorationManager;
    private BackupLogHelper backupLogHelper;

    public BackupRestorationService(Context context) {
        this.backupLogHelper = new BackupLogHelper(context);
        this.databaseRestorationManager = new DatabaseRestorationManager(context);
    }

    BackupRestorationService(BackupDateAccessor backupDateAccessor,
                             DataUnmarshaller dataUnmarshaller,
                             DatabaseRestorationManager databaseRestorationManager,
                             BackupLogHelper backupLogHelper) {
        this.backupDateAccessor = backupDateAccessor;
        this.dataUnmarshaller = dataUnmarshaller;
        this.databaseRestorationManager = databaseRestorationManager;
        this.backupLogHelper = backupLogHelper;
    }

    public void performRestore(BackupDataInput data,
                               int appVersionCode,
                               ParcelFileDescriptor newState) throws IOException {
        try {
            List<Project> projects = dataUnmarshaller.unmarshallBackupData(data);
            databaseRestorationManager.restoreProjectList(projects);
            backupDateAccessor.writeBackupDate(newState);
            backupLogHelper.logRestoreSuccess(appVersionCode);
        } catch (IOException ioe) {
            logError(TAG, "Error while restoring: " + ioe.getMessage(), ioe);
            backupLogHelper.logRestoreFail(appVersionCode, ioe.getMessage());
            throw ioe;
        } catch (Exception e) {
            logError(TAG, "Error while restoring: " + e.getMessage(), e);
            backupLogHelper.logRestoreFail(appVersionCode, e.getMessage());
        }
    }
}
