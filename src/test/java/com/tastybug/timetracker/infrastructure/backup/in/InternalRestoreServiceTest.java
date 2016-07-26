package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLog;
import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class InternalRestoreServiceTest {

    BackupDateAccessor backupDateAccessor = mock(BackupDateAccessor.class);
    BackupDataReader backupDataReader = mock(BackupDataReader.class);
    BackupDataImporter backupDataImporter = mock(BackupDataImporter.class);
    BackupLog backupLog = mock(BackupLog.class);

    InternalRestoreService subject = new InternalRestoreService(backupDateAccessor,
            backupDataReader,
            backupDataImporter,
            backupLog);

    @Before
    public void setup() {
        when(backupDateAccessor.readLastBackupDate((ParcelFileDescriptor) any())).thenReturn(Optional.of(new Date()));
    }

    @Test
    public void onRestore_calls_BackupDataAccessor_to_read_data() throws Exception {
        // given
        BackupDataInput data = mock(BackupDataInput.class);

        // when
        subject.performRestore(data, 1, null);

        // then
        verify(backupDataReader, times(1)).readBackup(data);
    }

    @Test
    public void onRestore_calls_BackupDataAccessor_to_store_the_new_backup_date() throws Exception {
        // given
        ParcelFileDescriptor newState = mock(ParcelFileDescriptor.class);

        // when
        subject.performRestore(null, 1, newState);

        // then
        verify(backupDateAccessor, times(1)).writeBackupDate(newState);
    }

    @Test
    public void onRestore_calls_BackupDataImporter_to_import_projects_from_backup() throws Exception {
        // given
        List<Project> projectsToImport = Collections.singletonList(new Project("123"));
        when(backupDataReader.readBackup((BackupDataInput) any())).thenReturn(projectsToImport);

        // when
        subject.performRestore(null, 1, null);

        // then
        verify(backupDataImporter, times(1)).restoreProjectList(projectsToImport);
    }

    @Test
    public void onRestore_correctly_logs_on_backups_success() throws Exception {
        // given
        BackupDataInput data = mock(BackupDataInput.class);

        // when
        subject.performRestore(data, 1234, null);

        // then
        verify(backupLog, times(1)).logRestoreSuccess(1234);
    }

    @Test(expected = IOException.class)
    public void onRestore_correctly_logs_on_restores_that_fail_due_to_exception() throws Exception {
        try {
            // given
            doThrow(new IOException("this breaks the backup")).when(backupDataReader).readBackup((BackupDataInput) any());

            // when
            subject.performRestore(null, 1234, null);

            fail();
        } catch (IOException ioe) {
            // then
            verify(backupLog, times(1)).logRestoreFail(eq(1234), eq("this breaks the backup"));
            throw ioe;
        }
    }
}