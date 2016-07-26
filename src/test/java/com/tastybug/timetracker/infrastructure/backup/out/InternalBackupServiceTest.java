package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataOutput;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLog;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class InternalBackupServiceTest {

    DataChangeIndicator dataChangeIndicator = mock(DataChangeIndicator.class);
    BackupDateAccessor backupDateAccessor = mock(BackupDateAccessor.class);
    BackupDataWriter backupDataWriter = mock(BackupDataWriter.class);
    BackupLog backupLog = mock(BackupLog.class);

    InternalBackupService subject = new InternalBackupService(dataChangeIndicator,
            backupDataWriter,
            backupDateAccessor,
            backupLog);

    @Test
    public void checkBackupNecessary_returns_true_on_first_backup() throws IOException {
        // given
        when(dataChangeIndicator.hasDataChangesSince((Date) any())).thenReturn(false);
        when(backupDateAccessor.readLastBackupDate((ParcelFileDescriptor) any())).thenReturn(Optional.<Date>absent());

        // when
        boolean result = subject.checkBackupNecessary(Optional.<Date>absent());

        // then
        assertTrue(result);
    }

    @Test
    public void checkBackupNecessary_returns_true_if_data_has_changed_since_last_backup() throws IOException {
        // given
        when(dataChangeIndicator.hasDataChangesSince((Date) any())).thenReturn(true);
        when(backupDateAccessor.readLastBackupDate((ParcelFileDescriptor) any())).thenReturn(Optional.of(new Date()));

        // when
        boolean result = subject.checkBackupNecessary(Optional.of(new Date()));

        // then
        assertTrue(result);
    }

    @Test
    public void checkBackupNecessary_logs_skipped_backups() throws IOException, JSONException {
        // given
        when(dataChangeIndicator.hasDataChangesSince((Date) any())).thenReturn(false);
        when(backupDateAccessor.readLastBackupDate((ParcelFileDescriptor) any())).thenReturn(Optional.of(new Date()));

        // when
        subject.checkBackupNecessary(Optional.of(new Date()));

        // then
        verify(backupLog, times(1)).logBackupUnnecessary(any(Optional.class));
    }

    @Test
    public void performBackup_calls_backupDataWriter_for_actual_backup() throws IOException, JSONException {
        // when
        subject.performBackup(null, null, null);

        // then
        verify(backupDataWriter, times(1)).writeBackup(any(BackupDataOutput.class));
    }

    @Test
    public void performBackup_calls_backupdateaccessor_to_store_the_new_backup_date() throws IOException, JSONException {
        // when
        subject.performBackup(null, null, null);

        // then
        verify(backupDateAccessor, times(1)).writeBackupDate(any(ParcelFileDescriptor.class));
    }

    @Test
    public void performBackup_wont_store_backup_date_after_json_exception() throws IOException, JSONException {
        // given
        doThrow(new JSONException("this breaks the backup")).when(backupDataWriter).writeBackup((BackupDataOutput) any());

        // when
        subject.performBackup(null, null, null);

        // then: this backup is deemed failed, so do not remember this date
        verify(backupDateAccessor, never()).writeBackupDate(any(ParcelFileDescriptor.class));
    }

    @Test
    public void performBackup_logs_after_successful_backups() throws IOException, JSONException {
        // when
        subject.performBackup(null, null, null);

        // then
        verify(backupLog, times(1)).logBackupSuccess(any(Optional.class));
    }

    @Test(expected = IOException.class)
    public void performBackup_logs_failed_backups_after_io_exception() throws IOException, JSONException {
        try {
            // given
            doThrow(new IOException("this breaks the backup")).when(backupDataWriter).writeBackup((BackupDataOutput) any());

            // when
            subject.performBackup(null, null, null);

            fail();
        } catch (IOException ioe) {
            // then
            verify(backupLog, times(1)).logBackupFail(any(Optional.class), eq("this breaks the backup"));
            throw ioe;
        }
    }

    @Test
    public void performBackup_logs_failed_backups_after_unexpected_exception() throws Exception {
        // given
        doThrow(new IllegalArgumentException("this breaks the backup")).when(backupDataWriter).writeBackup((BackupDataOutput) any());

        // when
        subject.performBackup(null, null, null);

        // then
        verify(backupLog, times(1)).logBackupFail(any(Optional.class), eq("this breaks the backup"));
    }

    @Test
    public void skipBackup_ensures_last_backup_date_is_kept_as_new_state() throws IOException {
        // given
        Optional<Date> lastBackupDate = Optional.of(new Date());

        // when
        subject.skipBackup(lastBackupDate, mock(ParcelFileDescriptor.class));

        // then
        verify(backupDateAccessor, times(1)).writeBackupDate((ParcelFileDescriptor) any(),
                eq(lastBackupDate.get()));
    }
}