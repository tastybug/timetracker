package com.tastybug.timetracker.infrastructure.backup;

import android.content.SharedPreferences;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.util.Formatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class BackupLogTest {

    private static final String BACKUP_LAST_SUCCESSFUL_DATE = "BACKUP_LAST_SUCCESSFUL_DATE";

    private static final String BACKUP_SUCCESSFUL_DATE = "BACKUP_SUCCESSFUL_DATE";
    private static final String BACKUP_UNNECESSARY_DATE = "BACKUP_UNNECESSARY_DATE";
    private static final String BACKUP_FAILED_DATE = "BACKUP_FAILED_DATE";
    private static final String BACKUP_FAILED_MSG = "BACKUP_FAILED_MSG";

    private static final String RESTORE_SUCCESSFUL_DATE = "RESTORE_SUCCESSFUL_DATE";
    private static final String RESTORE_SUCCESSFUL_APP_CODE = "RESTORE_SUCCESSFUL_APP_CODE";

    private static final String RESTORE_FAILED_APP_CODE = "RESTORE_FAILED_APP_CODE";
    private static final String RESTORE_FAILED_DATE = "RESTORE_FAILED_DATE";
    private static final String RESTORE_FAILED_MSG = "RESTORE_FAILED_MSG";

    SharedPreferences preferences = mock(SharedPreferences.class);
    SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
    BackupLog.DateProvider dateProvider = mock(BackupLog.DateProvider.class);

    BackupLog subject = new BackupLog(preferences, editor, dateProvider);

    Date currentDate = new Date(1);

    @Before
    public void setup() {
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
        when(dateProvider.getCurrentDate()).thenReturn(currentDate);
    }

    @Test
    public void logBackupSuccess_saves_the_last_backup_date_as_an_iso8601_string() {
        // given
        Date date = new Date();

        // when
        subject.logBackupSuccess(Optional.of(date));

        // then
        verify(editor, times(1)).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), eq(Formatter.iso8601().format(date)));
    }

    @Test
    public void logBackupSuccess_skips_saving_old_backup_date_if_there_is_none() {
        // when
        subject.logBackupSuccess(Optional.<Date>absent());

        // then
        verify(editor, never()).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), anyString());
    }

    @Test
    public void logBackupSuccess_saves_the_current_backup_date_as_an_iso8601_string() {
        // when
        subject.logBackupSuccess(Optional.of(new Date()));

        // then
        verify(editor, times(1)).putString(eq(BACKUP_SUCCESSFUL_DATE), eq(Formatter.iso8601().format(currentDate)));
    }

    @Test
    public void logBackupUnnecessary_saves_the_last_backup_date_as_an_iso8601_string() {
        // given
        Date date = new Date();

        // when
        subject.logBackupUnnecessary(Optional.of(date));

        // then
        verify(editor, times(1)).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), eq(Formatter.iso8601().format(date)));
    }

    @Test
    public void logBackupUnnecessary_skips_saving_old_backup_date_if_there_is_none() {
        // when
        subject.logBackupUnnecessary(Optional.<Date>absent());

        // then
        verify(editor, never()).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), anyString());
    }

    @Test
    public void logBackupUnnecessary_saves_the_current_date_as_an_iso8601_string() {
        // when
        subject.logBackupUnnecessary(Optional.of(new Date()));

        // then
        verify(editor, times(1)).putString(eq(BACKUP_UNNECESSARY_DATE), eq(Formatter.iso8601().format(currentDate)));
    }

    @Test
    public void logBackupFail_saves_the_last_backup_date_as_an_iso8601_string() {
        // given
        Date date = new Date();

        // when
        subject.logBackupUnnecessary(Optional.of(date));

        // then
        verify(editor, times(1)).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), eq(Formatter.iso8601().format(date)));
    }

    @Test
    public void logBackupFail_skips_saving_old_backup_date_if_there_is_none() {
        // when
        subject.logBackupFail(Optional.<Date>absent(), "asd");

        // then
        verify(editor, never()).putString(eq(BACKUP_LAST_SUCCESSFUL_DATE), anyString());
    }

    @Test
    public void logBackupFail_saves_the_current_date_as_an_iso8601_string() {
        // when
        subject.logBackupFail(Optional.of(new Date()), "asd");

        // then
        verify(editor, times(1)).putString(eq(BACKUP_FAILED_DATE), eq(Formatter.iso8601().format(currentDate)));
    }

    @Test
    public void logBackupFail_saves_the_error_message() {
        // when
        subject.logBackupFail(Optional.of(new Date()), "error");

        // then
        verify(editor, times(1)).putString(eq(BACKUP_FAILED_MSG), eq("error"));
    }

    @Test
    public void logRestoreSuccess_saves_the_current_date_as_an_iso8601_string() {
        // when
        subject.logRestoreSuccess(123);

        // then
        verify(editor, times(1)).putString(eq(RESTORE_SUCCESSFUL_DATE), eq(Formatter.iso8601().format(currentDate)));
    }

    @Test
    public void logRestoreSuccess_saves_the_application_code_that_was_successfully_imported_from() {
        // when
        subject.logRestoreSuccess(123);

        // then
        verify(editor, times(1)).putInt(eq(RESTORE_SUCCESSFUL_APP_CODE), eq(123));
    }

    @Test
    public void logRestoreFail_saves_the_current_date_as_an_iso8601_string() {
        // when
        subject.logRestoreFail(123, "asd");

        // then
        verify(editor, times(1)).putString(eq(RESTORE_FAILED_DATE), eq(Formatter.iso8601().format(currentDate)));
    }

    @Test
    public void logRestoreFail_saves_the_application_code_that_we_failed_to_imported_from() {
        // when
        subject.logRestoreFail(456, "error");

        // then
        verify(editor, times(1)).putInt(eq(RESTORE_FAILED_APP_CODE), eq(456));
    }

    @Test
    public void logRestoreFail_saves_the_error_message() {
        // when
        subject.logRestoreFail(456, "error");

        // then
        verify(editor, times(1)).putString(eq(RESTORE_FAILED_MSG), eq("error"));
    }

    @Test
    public void getPreviousSuccessBackupDate_returns_date_if_available() throws Exception {
        // given
        Date storedDate = new Date(123456);
        when(preferences.getString(eq(BACKUP_LAST_SUCCESSFUL_DATE), (String) isNull())).thenReturn(Formatter.iso8601().format(storedDate));

        // when
        Optional<Date> dateOptional = subject.getPreviousSuccessBackupDate();

        // then
        assertEquals(storedDate, dateOptional.get());
    }

    @Test
    public void getPreviousSuccessBackupDate_returns_empty_optional_if_no_date_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(BACKUP_LAST_SUCCESSFUL_DATE), (String) isNull())).thenReturn(null);

        // when
        Optional<Date> date = subject.getPreviousSuccessBackupDate();

        // then
        assertFalse(date.isPresent());
    }

    @Test
    public void getSuccessBackupDate_returns_date_if_available() throws Exception {
        // given
        Date storedDate = new Date(123456);
        when(preferences.getString(eq(BACKUP_SUCCESSFUL_DATE), (String) isNull())).thenReturn(Formatter.iso8601().format(storedDate));

        // when
        Optional<Date> dateOptional = subject.getSuccessBackupDate();

        // then
        assertEquals(storedDate, dateOptional.get());
    }

    @Test
    public void getSuccessBackupDate_returns_empty_optional_if_no_date_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(BACKUP_SUCCESSFUL_DATE), (String) isNull())).thenReturn(null);

        // when
        Optional<Date> date = subject.getSuccessBackupDate();

        // then
        assertFalse(date.isPresent());
    }

    @Test
    public void getFailedBackupDate_returns_date_if_available() throws Exception {
        // given
        Date storedDate = new Date(123456);
        when(preferences.getString(eq(BACKUP_FAILED_DATE), (String) isNull())).thenReturn(Formatter.iso8601().format(storedDate));

        // when
        Optional<Date> dateOptional = subject.getFailedBackupDate();

        // then
        assertEquals(storedDate, dateOptional.get());
    }

    @Test
    public void getFailedBackupDate_returns_empty_optional_if_no_date_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(BACKUP_FAILED_DATE), (String) isNull())).thenReturn(null);

        // when
        Optional<Date> date = subject.getFailedBackupDate();

        // then
        assertFalse(date.isPresent());
    }

    @Test
    public void getFailedBackupMessage_returns_message_if_available() throws Exception {
        // given
        String msg = "error";
        when(preferences.getString(eq(BACKUP_FAILED_MSG), (String) isNull())).thenReturn(msg);

        // when
        Optional<String> messageOptional = subject.getFailedBackupMessage();

        // then
        assertEquals(msg, messageOptional.get());
    }

    @Test
    public void getFailedBackupMessage_returns_empty_optional_if_no_message_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(BACKUP_FAILED_MSG), (String) isNull())).thenReturn(null);

        // when
        Optional<String> messageOptional = subject.getFailedBackupMessage();

        // then
        assertFalse(messageOptional.isPresent());
    }

    @Test
    public void getSuccessRestoreDate_returns_date_if_available() throws Exception {
        // given
        Date storedDate = new Date(123456);
        when(preferences.getString(eq(RESTORE_SUCCESSFUL_DATE), (String) isNull())).thenReturn(Formatter.iso8601().format(storedDate));

        // when
        Optional<Date> dateOptional = subject.getSuccessRestoreDate();

        // then
        assertEquals(storedDate, dateOptional.get());
    }

    @Test
    public void getSuccessRestoreDate_returns_empty_optional_if_no_date_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(RESTORE_SUCCESSFUL_DATE), (String) isNull())).thenReturn(null);

        // when
        Optional<Date> date = subject.getSuccessRestoreDate();

        // then
        assertFalse(date.isPresent());
    }

    @Test
    public void getSuccessRestoreAppCode_returns_app_code_if_available() throws Exception {
        // given
        when(preferences.contains(eq(RESTORE_SUCCESSFUL_APP_CODE))).thenReturn(true);
        when(preferences.getInt(eq(RESTORE_SUCCESSFUL_APP_CODE), eq(-1))).thenReturn(1234);

        // when
        Optional<Integer> appCodeOptional = subject.getSuccessRestoreAppCode();

        // then
        assertEquals(1234, (int)appCodeOptional.get());
    }

    @Test
    public void getSuccessRestoreAppCode_returns_empty_optional_if_no_app_code_is_stored() throws Exception {
        // given
        when(preferences.contains(eq(RESTORE_SUCCESSFUL_APP_CODE))).thenReturn(false);
        when(preferences.getInt(eq(RESTORE_SUCCESSFUL_APP_CODE), eq(-1))).thenReturn(-1);

        // when
        Optional<Integer> appCodeOptional = subject.getSuccessRestoreAppCode();

        // then
        assertFalse(appCodeOptional.isPresent());
    }

    @Test
    public void getFailedRestoreDate_returns_date_if_available() throws Exception {
        // given
        Date storedDate = new Date(123456);
        when(preferences.getString(eq(RESTORE_FAILED_DATE), (String) isNull())).thenReturn(Formatter.iso8601().format(storedDate));

        // when
        Optional<Date> dateOptional = subject.getFailedRestoreDate();

        // then
        assertEquals(storedDate, dateOptional.get());
    }

    @Test
    public void getFailedRestoreDate_returns_empty_optional_if_no_date_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(RESTORE_FAILED_DATE), (String) isNull())).thenReturn(null);

        // when
        Optional<Date> date = subject.getFailedRestoreDate();

        // then
        assertFalse(date.isPresent());
    }

    @Test
    public void getFailedRestoreAppCode_returns_app_code_if_available() throws Exception {
        // given
        when(preferences.contains(eq(RESTORE_FAILED_APP_CODE))).thenReturn(true);
        when(preferences.getInt(eq(RESTORE_FAILED_APP_CODE), eq(-1))).thenReturn(1234);

        // when
        Optional<Integer> appCodeOptional = subject.getFailedRestoreAppCode();

        // then
        assertEquals(1234, (int)appCodeOptional.get());
    }

    @Test
    public void getFailedRestoreAppCode_returns_empty_optional_if_no_app_code_is_stored() throws Exception {
        // given
        when(preferences.contains(eq(RESTORE_FAILED_APP_CODE))).thenReturn(false);
        when(preferences.getInt(eq(RESTORE_FAILED_APP_CODE), eq(-1))).thenReturn(-1);

        // when
        Optional<Integer> appCodeOptional = subject.getFailedRestoreAppCode();

        // then
        assertFalse(appCodeOptional.isPresent());
    }

    @Test
    public void getFailedRestoreMessage_returns_message_if_available() throws Exception {
        // given
        String msg = "error";
        when(preferences.getString(eq(RESTORE_FAILED_MSG), (String) isNull())).thenReturn(msg);

        // when
        Optional<String> messageOptional = subject.getFailedRestoreMessage();

        // then
        assertEquals(msg, messageOptional.get());
    }

    @Test
    public void getFailedRestoreMessage_returns_empty_optional_if_no_message_is_stored() throws Exception {
        // given
        when(preferences.getString(eq(RESTORE_FAILED_MSG), (String) isNull())).thenReturn(null);

        // when
        Optional<String> messageOptional = subject.getFailedRestoreMessage();

        // then
        assertFalse(messageOptional.isPresent());
    }

}