package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.BackupDateAccessor;
import com.tastybug.timetracker.infrastructure.backup.BackupLogHelper;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class BackupRestorationServiceTest {

    private BackupDateAccessor backupDateAccessor = mock(BackupDateAccessor.class);
    private DataUnmarshaller dataUnmarshaller = mock(DataUnmarshaller.class);
    private DatabaseRestorationManager databaseRestorationManager = mock(DatabaseRestorationManager.class);
    private BackupLogHelper backupLogHelper = mock(BackupLogHelper.class);

    private BackupRestorationService subject = new BackupRestorationService(backupDateAccessor,
            dataUnmarshaller,
            databaseRestorationManager,
            backupLogHelper);

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
        verify(dataUnmarshaller).unmarshallBackupData(data);
    }

    @Test
    public void onRestore_calls_BackupDataAccessor_to_store_the_new_backup_date() throws Exception {
        // given
        ParcelFileDescriptor newState = mock(ParcelFileDescriptor.class);

        // when
        subject.performRestore(null, 1, newState);

        // then
        verify(backupDateAccessor).writeBackupDate(newState);
    }

    @Test
    public void onRestore_calls_BackupDataImporter_to_import_projects_from_backup() throws Exception {
        // given
        List<Project> projectsToImport = Collections.singletonList(new Project("123"));
        when(dataUnmarshaller.unmarshallBackupData((BackupDataInput) any())).thenReturn(projectsToImport);

        // when
        subject.performRestore(null, 1, null);

        // then
        verify(databaseRestorationManager).restoreProjectList(projectsToImport);
    }

    @Test
    public void onRestore_correctly_logs_on_backups_success() throws Exception {
        // given
        BackupDataInput data = mock(BackupDataInput.class);

        // when
        subject.performRestore(data, 1234, null);

        // then
        verify(backupLogHelper).logRestoreSuccess(1234);
    }

    @Test(expected = IOException.class)
    public void onRestore_correctly_logs_on_restores_that_fail_due_to_exception() throws Exception {
        try {
            // given
            doThrow(new IOException("this breaks the backup")).when(dataUnmarshaller).unmarshallBackupData((BackupDataInput) any());

            // when
            subject.performRestore(null, 1234, null);

            fail();
        } catch (IOException ioe) {
            // then
            verify(backupLogHelper).logRestoreFail(eq(1234), eq("this breaks the backup"));
            throw ioe;
        }
    }
}