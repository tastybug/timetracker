package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataOutput;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLog;

import java.io.IOException;
import java.util.Date;

public class InternalBackupService {

    private static final String TAG = InternalBackupService.class.getSimpleName();

    private DataChangeIndicator dataChangeIndicator;
    private BackupDataWriter backupDataWriter;
    private BackupDateAccessor backupDateAccessor = new BackupDateAccessor();
    private BackupLog backupLog;

    public InternalBackupService(Context context) {
        this.dataChangeIndicator = new DataChangeIndicator(context);
        this.backupDataWriter = new BackupDataWriter(context);
        this.backupLog = new BackupLog(context);
    }

    public InternalBackupService(DataChangeIndicator dataChangeIndicator,
                                 BackupDataWriter backupDataWriter,
                                 BackupDateAccessor backupDateAccessor,
                                 BackupLog backupLog) {
        this.dataChangeIndicator = dataChangeIndicator;
        this.backupDataWriter = backupDataWriter;
        this.backupDateAccessor = backupDateAccessor;
        this.backupLog = backupLog;
    }

    public Optional<Date> getLastBackupDate(ParcelFileDescriptor oldState) throws IOException {
        return backupDateAccessor.readLastBackupDate(oldState);
    }

    public boolean checkBackupNecessary(Optional<Date> lastBackupDate) throws IOException {
        boolean result = !lastBackupDate.isPresent() ||
                dataChangeIndicator.hasDataChangesSince(lastBackupDate.get());
        if (lastBackupDate.isPresent()) {
            Log.i(TAG, "Last backup was: " + lastBackupDate.get() + ", data " + (dataChangeIndicator.hasDataChangesSince(lastBackupDate.get()) ? "has changed" : "has not changed"));
        } else {
            Log.i(TAG, "Very first backup commencing now..");
        }
        if (!result) {
            backupLog.logBackupUnnecessary(lastBackupDate);
        }
        return result;
    }

    public void performBackup(ParcelFileDescriptor oldState,
                              BackupDataOutput data,
                              ParcelFileDescriptor newState) throws IOException {
        Optional<Date> lastBackupDate = backupDateAccessor.readLastBackupDate(oldState);
        try {
            backupDataWriter.writeBackup(data);
            backupDateAccessor.writeBackupDate(newState);
            backupLog.logBackupSuccess(lastBackupDate);
        } catch (IOException ioe) {
            Log.e(TAG, "Error while generating backup: " + ioe.getMessage(), ioe);
            backupLog.logBackupFail(lastBackupDate, ioe.getMessage());
            throw ioe;
        } catch (Exception e) {
            Log.e(TAG, "Error while generating backup: " + e.getMessage(), e);
            backupLog.logBackupFail(lastBackupDate, e.getMessage());
        }
    }

    public void skipBackup(Optional<Date> lastBackupDate,
                           ParcelFileDescriptor newState) throws IOException {
        backupDateAccessor.writeBackupDate(newState, lastBackupDate.get());
    }
}
