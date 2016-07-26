package com.tastybug.timetracker.infrastructure.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.base.Optional;
import com.tastybug.timetracker.util.Formatter;

import java.text.ParseException;
import java.util.Date;

public class BackupLog {

    private static final String TAG = BackupLog.class.getSimpleName();

    private static final String FILE_NAME = "backup_log";

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

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private DateProvider dateProvider = new DateProvider();

    public BackupLog(Context context) {
        preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public BackupLog(SharedPreferences preferences, SharedPreferences.Editor editor, DateProvider dateProvider) {
        this.preferences = preferences;
        this.editor = editor;
        this.dateProvider = dateProvider;
    }

    public void logBackupSuccess(Optional<Date> lastBackupDate) {
        storeLastSuccessfulBackupDate(lastBackupDate);
        editor.putString(BACKUP_SUCCESSFUL_DATE, Formatter.iso8601().format(dateProvider.getCurrentDate())).apply();
        Log.i(TAG, "Successfully completed backup.");
    }

    public void logBackupUnnecessary(Optional<Date> lastBackupDate) {
        storeLastSuccessfulBackupDate(lastBackupDate);
        editor.putString(BACKUP_UNNECESSARY_DATE, Formatter.iso8601().format(dateProvider.getCurrentDate())).apply();
        Log.i(TAG, "Skipped backup as not necessary.");
    }

    public void logBackupFail(Optional<Date> lastBackupDate, String message) {
        storeLastSuccessfulBackupDate(lastBackupDate);
        editor.putString(BACKUP_FAILED_DATE, Formatter.iso8601().format(dateProvider.getCurrentDate())).apply();
        editor.putString(BACKUP_FAILED_MSG, message).apply();
        Log.i(TAG, "Failed backup, msg was: " + message);
    }

    public void logRestoreSuccess(int versionCode) {
        editor.putInt(RESTORE_SUCCESSFUL_APP_CODE, versionCode)
                .putString(RESTORE_SUCCESSFUL_DATE, Formatter.iso8601().format(dateProvider.getCurrentDate())).apply();
        Log.i(TAG, "Successfully restored backup!");
    }

    public void logRestoreFail(int versionCode, String message) {
        editor.putInt(RESTORE_FAILED_APP_CODE, versionCode)
                .putString(RESTORE_FAILED_DATE, Formatter.iso8601().format(dateProvider.getCurrentDate()))
                .putString(RESTORE_FAILED_MSG, message).apply();
        Log.i(TAG, "Failed backup restoration, msg was: " + message);
    }

    public Optional<Date> getPreviousSuccessBackupDate() throws ParseException {
        return getDateOptFromNullable(preferences.getString(BACKUP_LAST_SUCCESSFUL_DATE, null));
    }

    public Optional<Date> getSuccessBackupDate() throws ParseException {
        return getDateOptFromNullable(preferences.getString(BACKUP_SUCCESSFUL_DATE, null));
    }

    public Optional<Date> getFailedBackupDate() throws ParseException {
        return getDateOptFromNullable(preferences.getString(BACKUP_FAILED_DATE, null));
    }

    public Optional<String> getFailedBackupMessage() {
        return Optional.fromNullable(preferences.getString(BACKUP_FAILED_MSG, null));
    }

    public Optional<Date> getSuccessRestoreDate() throws ParseException {
        return getDateOptFromNullable(preferences.getString(RESTORE_SUCCESSFUL_DATE, null));
    }

    public Optional<Integer> getSuccessRestoreAppCode() {
        return Optional.fromNullable(preferences.contains(RESTORE_SUCCESSFUL_APP_CODE)
                ? preferences.getInt(RESTORE_SUCCESSFUL_APP_CODE, -1)
                : null);
    }

    public Optional<Date> getFailedRestoreDate() throws ParseException {
        return getDateOptFromNullable(preferences.getString(RESTORE_FAILED_DATE, null));
    }

    public Optional<Integer> getFailedRestoreAppCode() {
        return Optional.fromNullable(preferences.contains(RESTORE_FAILED_APP_CODE)
                ? preferences.getInt(RESTORE_FAILED_APP_CODE, -1)
                : null);
    }

    public Optional<String> getFailedRestoreMessage() {
        return Optional.fromNullable(preferences.getString(RESTORE_FAILED_MSG, null));
    }

    private Optional<Date> getDateOptFromNullable(String dateString) throws ParseException {
        if (dateString != null) {
            return Optional.of(Formatter.iso8601().parse(dateString));
        } else {
            return Optional.absent();
        }
    }

    private void storeLastSuccessfulBackupDate(Optional<Date> lastDateOpt) {
        if (lastDateOpt.isPresent()) {
            editor.putString(BACKUP_LAST_SUCCESSFUL_DATE, Formatter.iso8601().format(lastDateOpt.get())).apply();
        }
    }

    class DateProvider {

        public DateProvider() {}

        public Date getCurrentDate() {
            return new Date();
        }

    }
}
