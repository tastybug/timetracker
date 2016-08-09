package com.tastybug.timetracker.infrastructure.backup;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.in.InternalRestoreService;
import com.tastybug.timetracker.infrastructure.backup.out.InternalBackupService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class BackupAgentFacadeTest {

    InternalBackupService internalBackupService = mock(InternalBackupService.class);
    InternalRestoreService internalRestoreService = mock(InternalRestoreService.class);
    BackupEnabledService backupEnabledService = mock(BackupEnabledService.class);

    BackupAgentFacade subject = new BackupAgentFacade(internalBackupService, internalRestoreService, backupEnabledService);

    @Before
    public void setup() {
        when(backupEnabledService.isBackupFacilityEnabled()).thenReturn(true);
    }

    @Test
    public void onBackup_skips_backup_if_not_deemed_necessary() throws Exception {
        // given
        Optional<Date> lastBackupDate = Optional.absent();
        when(internalBackupService.getLastBackupDate((ParcelFileDescriptor) any())).thenReturn(lastBackupDate);
        when(internalBackupService.checkBackupNecessary((Optional<Date>) any())).thenReturn(false);

        // when
        subject.onBackup(null, null, null);

        // then
        verify(internalBackupService, times(0)).performBackup((ParcelFileDescriptor)any(),
                (BackupDataOutput)any(),
                (ParcelFileDescriptor)any());
        verify(internalBackupService, times(1)).skipBackup(eq(lastBackupDate), (ParcelFileDescriptor) any());
    }

    @Test
    public void onBackup_calls_internal_service_to_perform_when_backup_is_deemed_necessary() throws Exception {
        // given
        when(internalBackupService.checkBackupNecessary((Optional<Date>) any())).thenReturn(true);

        // when
        subject.onBackup(null, null, null);

        // then
        verify(internalBackupService, times(1)).performBackup((ParcelFileDescriptor)any(),
                (BackupDataOutput)any(),
                (ParcelFileDescriptor)any());
    }

    @Test
    public void onBackup_is_noop_if_backup_is_disabled() throws Exception {
        // given
        when(backupEnabledService.isBackupFacilityEnabled()).thenReturn(false);

        // when
        subject.onBackup(null, null, null);

        // then
        verifyZeroInteractions(internalBackupService);
    }

    @Test
    public void onRestore_calls_internal_restore_service_for_restore() throws Exception {
        // when
        subject.onRestore(null, 1234, null);

        // then
        verify(internalRestoreService, times(1)).performRestore((BackupDataInput)any(),
                eq(1234),
                (ParcelFileDescriptor)any());
    }

    @Test
    public void onRestore_is_noop_if_backup_is_disabled() throws Exception {
        // given
        when(backupEnabledService.isBackupFacilityEnabled()).thenReturn(false);

        // when
        subject.onRestore(null, 1234, null);

        // then
        verifyZeroInteractions(internalRestoreService);
    }

}