package com.tastybug.timetracker.infrastructure.backup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.in.BackupRestorationService;
import com.tastybug.timetracker.infrastructure.backup.in.BackupRestoredEvent;
import com.tastybug.timetracker.infrastructure.backup.out.BackupCreationService;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.io.IOException;
import java.util.Date;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

/**
 * @link https://developer.android.com/guide/topics/data/backup.html
 */
public class OSFacingBackupAgentHandler extends BackupAgent {

    private BackupCreationService backupCreationService;
    private BackupRestorationService backupRestorationService;
    private BackupConfiguration backupConfiguration;
    private OttoProvider ottoProvider = new OttoProvider();

    public OSFacingBackupAgentHandler() {
        logInfo(getClass().getSimpleName(), "Starting OSFacingBackupAgentHandler");
    }

    public OSFacingBackupAgentHandler(BackupCreationService backupCreationService,
                                      BackupRestorationService backupRestorationService,
                                      BackupConfiguration backupConfiguration) {
        this.backupCreationService = backupCreationService;
        this.backupRestorationService = backupRestorationService;
        this.backupConfiguration = backupConfiguration;
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState,
                         BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        logInfo(getClass().getSimpleName(), "onBackup");
        initDependencies();

        if (!backupConfiguration.isBackupFacilityEnabled()) {
            logDebug(getClass().getSimpleName(), "Backup disabled..");
            return;
        }

        Optional<Date> lastBackupDate = backupCreationService.getLastBackupDate(oldState);
        if (backupCreationService.checkBackupNecessary(lastBackupDate)) {
            backupCreationService.performBackup(oldState, data, newState);
        } else {
            backupCreationService.skipBackup(lastBackupDate, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data,
                          int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        logInfo(getClass().getSimpleName(), "onRestore");
        initDependencies();

        if (!backupConfiguration.isBackupFacilityEnabled()) {
            logDebug(getClass().getSimpleName(), "Backup disabled..");
            return;
        }

        backupRestorationService.performRestore(data, appVersionCode, newState);
        ottoProvider.getSharedBus().post(new BackupRestoredEvent());
    }

    private void initDependencies() {
        if (backupRestorationService == null || backupCreationService == null) {
            this.backupCreationService = new BackupCreationService(getApplicationContext());
            this.backupRestorationService = new BackupRestorationService(getApplicationContext());
        }
        if (backupConfiguration == null) {
            this.backupConfiguration = new BackupConfiguration(getApplicationContext());
        }
    }
}
