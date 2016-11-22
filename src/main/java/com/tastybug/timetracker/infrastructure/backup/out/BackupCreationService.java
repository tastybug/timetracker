package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataOutput;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLogHelper;

import java.io.IOException;
import java.util.Date;

import static com.tastybug.timetracker.util.ConditionalLog.logError;
import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class BackupCreationService {

    private static final String TAG = BackupCreationService.class.getSimpleName();

    private BackupNecessityIndicator backupNecessityIndicator;
    private DataMarshaller dataMarshaller;
    private BackupDateAccessor backupDateAccessor = new BackupDateAccessor();
    private BackupLogHelper backupLogHelper;

    public BackupCreationService(Context context) {
        this.backupNecessityIndicator = new BackupNecessityIndicator(context);
        this.dataMarshaller = new DataMarshaller(context);
        this.backupLogHelper = new BackupLogHelper(context);
    }

    BackupCreationService(BackupNecessityIndicator backupNecessityIndicator,
                          DataMarshaller dataMarshaller,
                          BackupDateAccessor backupDateAccessor,
                          BackupLogHelper backupLogHelper) {
        this.backupNecessityIndicator = backupNecessityIndicator;
        this.dataMarshaller = dataMarshaller;
        this.backupDateAccessor = backupDateAccessor;
        this.backupLogHelper = backupLogHelper;
    }

    public Optional<Date> getLastBackupDate(ParcelFileDescriptor oldState) throws IOException {
        return backupDateAccessor.readLastBackupDate(oldState);
    }

    public boolean checkBackupNecessary(Optional<Date> lastBackupDate) throws IOException {
        boolean result = !lastBackupDate.isPresent() ||
                backupNecessityIndicator.hasDataChangesSince(lastBackupDate.get());
        if (lastBackupDate.isPresent()) {
            logInfo(TAG, "Last backup was: " + lastBackupDate.get() + ", data " + (backupNecessityIndicator.hasDataChangesSince(lastBackupDate.get()) ? "has changed" : "has not changed"));
        } else {
            logInfo(TAG, "Very first backup commencing now..");
        }
        if (!result) {
            backupLogHelper.logBackupUnnecessary(lastBackupDate);
        }
        return result;
    }

    public void performBackup(ParcelFileDescriptor oldState,
                              BackupDataOutput data,
                              ParcelFileDescriptor newState) throws IOException {
        Optional<Date> lastBackupDate = backupDateAccessor.readLastBackupDate(oldState);
        try {
            dataMarshaller.writeBackup(data);
            backupDateAccessor.writeBackupDate(newState);
            backupLogHelper.logBackupSuccess(lastBackupDate);
        } catch (IOException ioe) {
            logError(TAG, "Error while generating backup: " + ioe.getMessage(), ioe);
            backupLogHelper.logBackupFail(lastBackupDate, ioe.getMessage());
            throw ioe;
        } catch (Exception e) {
            logError(TAG, "Error while generating backup: " + e.getMessage(), e);
            backupLogHelper.logBackupFail(lastBackupDate, e.getMessage());
        }
    }

    public void skipBackup(Optional<Date> lastBackupDate,
                           ParcelFileDescriptor newState) throws IOException {
        backupDateAccessor.writeBackupDate(newState, lastBackupDate.get());
    }
}
