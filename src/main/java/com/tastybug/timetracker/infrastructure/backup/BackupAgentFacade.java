package com.tastybug.timetracker.infrastructure.backup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.in.BackupRestoredEvent;
import com.tastybug.timetracker.infrastructure.backup.in.InternalRestoreService;
import com.tastybug.timetracker.infrastructure.backup.out.InternalBackupService;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.io.IOException;
import java.util.Date;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

/**
 * @link https://developer.android.com/guide/topics/data/backup.html
 */
public class BackupAgentFacade extends BackupAgent {

    private InternalBackupService internalBackupService;
    private InternalRestoreService internalRestoreService;
    private BackupEnabledService backupEnabledService;
    private OttoProvider ottoProvider = new OttoProvider();

    public BackupAgentFacade() {
        logInfo(getClass().getSimpleName(), "Starting BackupAgentFacade");
    }

    public BackupAgentFacade(InternalBackupService internalBackupService,
                             InternalRestoreService internalRestoreService,
                             BackupEnabledService backupEnabledService) {
        this.internalBackupService = internalBackupService;
        this.internalRestoreService = internalRestoreService;
        this.backupEnabledService = backupEnabledService;
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState,
                         BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        logInfo(getClass().getSimpleName(), "onBackup");
        initDependencies();

        if (!backupEnabledService.isBackupFacilityEnabled()) {
            logDebug(getClass().getSimpleName(), "Backup disabled..");
            return;
        }

        Optional<Date> lastBackupDate = internalBackupService.getLastBackupDate(oldState);
        if (internalBackupService.checkBackupNecessary(lastBackupDate)) {
            internalBackupService.performBackup(oldState, data, newState);
        } else {
            internalBackupService.skipBackup(lastBackupDate, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data,
                          int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        logInfo(getClass().getSimpleName(), "onRestore");
        initDependencies();

        if (!backupEnabledService.isBackupFacilityEnabled()) {
            logDebug(getClass().getSimpleName(), "Backup disabled..");
            return;
        }

        internalRestoreService.performRestore(data, appVersionCode, newState);
        ottoProvider.getSharedBus().post(new BackupRestoredEvent());
    }

    private void initDependencies() {
        if (internalRestoreService == null || internalBackupService == null) {
            this.internalBackupService = new InternalBackupService(getApplicationContext());
            this.internalRestoreService = new InternalRestoreService(getApplicationContext());
        }
        if (backupEnabledService == null) {
            this.backupEnabledService = new BackupEnabledService(getApplicationContext());
        }
    }
}
