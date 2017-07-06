package com.tastybug.timetracker.extension.backup.ui;

import android.content.Context;
import android.content.Intent;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.filecache.ShareableFileUriProvider;
import com.tastybug.timetracker.infrastructure.util.DateProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

class ShareManualBackupIntentFactory {

    private final Context context;
    private DateProvider dateProvider = new DateProvider();
    private DataExportFileWriter dataExportFileWriter;
    private ShareableFileUriProvider shareableFileUriProvider;

    ShareManualBackupIntentFactory(Context context) {
        this(context, new DataExportFileWriter(context), new ShareableFileUriProvider(context));
    }

    private ShareManualBackupIntentFactory(Context context,
                                           DataExportFileWriter dataExportFileWriter,
                                           ShareableFileUriProvider shareableFileUriProvider) {
        this.context = context;
        this.dataExportFileWriter = dataExportFileWriter;
        this.shareableFileUriProvider = shareableFileUriProvider;
    }

    public Intent create(byte[] data) throws IOException {
        Preconditions.checkArgument(data != null && data.length > 0);

        File reportFile = dataExportFileWriter.writeToCache(data);

        Intent intent = new Intent();
        intent.setAction(ACTION_SEND);
        intent.setType("application/json");
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(EXTRA_TEXT, getExtraTextForBackup());
        intent.putExtra(Intent.EXTRA_STREAM, shareableFileUriProvider.getShareableUri(reportFile));
        intent.putExtra(EXTRA_SUBJECT, getExtraSubjectForBackup());

        return intent;
    }

    private String getExtraSubjectForBackup() {
        return context.getString(R.string.extra_text_for_backup_app_X_on_date_Y,
                context.getString(R.string.app_name),
                getDateTimeString());
    }

    private String getExtraTextForBackup() {
        return context.getString(R.string.extra_text_for_backup_app_X_on_date_Y,
                context.getString(R.string.app_name),
                getDateTimeString());
    }

    private String getDateTimeString() {
        Date now = dateProvider.getCurrentDate();
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(now);
    }

}
