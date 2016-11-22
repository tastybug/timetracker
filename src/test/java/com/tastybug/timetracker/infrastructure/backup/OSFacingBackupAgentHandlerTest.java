package com.tastybug.timetracker.infrastructure.backup;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.backup.in.BackupRestorationService;
import com.tastybug.timetracker.infrastructure.backup.out.BackupCreationService;

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
public class OSFacingBackupAgentHandlerTest {

    private BackupCreationService backupCreationService = mock(BackupCreationService.class);
    private BackupRestorationService backupRestorationService = mock(BackupRestorationService.class);
    private BackupConfiguration backupConfiguration = mock(BackupConfiguration.class);

    private OSFacingBackupAgentHandler subject = new OSFacingBackupAgentHandler(backupCreationService, backupRestorationService, backupConfiguration);

    @Before
    public void setup() {
        when(backupConfiguration.isBackupFacilityEnabled()).thenReturn(true);
    }

    @Test
    public void onBackup_skips_backup_if_not_deemed_necessary() throws Exception {
        // given
        Optional<Date> lastBackupDate = Optional.absent();
        when(backupCreationService.getLastBackupDate((ParcelFileDescriptor) any())).thenReturn(lastBackupDate);
        when(backupCreationService.checkBackupNecessary((Optional<Date>) any())).thenReturn(false);

        // when
        subject.onBackup(null, null, null);

        // then
        verify(backupCreationService, times(0)).performBackup((ParcelFileDescriptor) any(),
                (BackupDataOutput) any(),
                (ParcelFileDescriptor) any());
        verify(backupCreationService, times(1)).skipBackup(eq(lastBackupDate), (ParcelFileDescriptor) any());
    }

    @Test
    public void onBackup_calls_internal_service_to_perform_when_backup_is_deemed_necessary() throws Exception {
        // given
        when(backupCreationService.checkBackupNecessary((Optional<Date>) any())).thenReturn(true);

        // when
        subject.onBackup(null, null, null);

        // then
        verify(backupCreationService, times(1)).performBackup((ParcelFileDescriptor) any(),
                (BackupDataOutput) any(),
                (ParcelFileDescriptor) any());
    }

    @Test
    public void onBackup_is_noop_if_backup_is_disabled() throws Exception {
        // given
        when(backupConfiguration.isBackupFacilityEnabled()).thenReturn(false);

        // when
        subject.onBackup(null, null, null);

        // then
        verifyZeroInteractions(backupCreationService);
    }

    @Test
    public void onRestore_calls_internal_restore_service_for_restore() throws Exception {
        // when
        subject.onRestore(null, 1234, null);

        // then
        verify(backupRestorationService, times(1)).performRestore((BackupDataInput) any(),
                eq(1234),
                (ParcelFileDescriptor) any());
    }

    @Test
    public void onRestore_is_noop_if_backup_is_disabled() throws Exception {
        // given
        when(backupConfiguration.isBackupFacilityEnabled()).thenReturn(false);

        // when
        subject.onRestore(null, 1234, null);

        // then
        verifyZeroInteractions(backupRestorationService);
    }

}