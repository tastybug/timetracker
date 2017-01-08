package com.tastybug.timetracker.ui.manualbackup.share;

import android.content.Context;
import android.content.Intent;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.filecache.ShareableFileUriProvider;
import com.tastybug.timetracker.util.DateProvider;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.io.File;
import java.io.IOException;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class ShareManualBackupIntentFactory {

    private final Context context;
    private DateProvider dateProvider = new DateProvider();
    private DataExportFileWriter dataExportFileWriter;
    private ShareableFileUriProvider shareableFileUriProvider;

    public ShareManualBackupIntentFactory(Context context) {
        this(context, new DataExportFileWriter(context), new ShareableFileUriProvider(context));
    }

    ShareManualBackupIntentFactory(Context context,
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
        intent.putExtra(EXTRA_TEXT, getExtraTextForReport());
        intent.putExtra(Intent.EXTRA_STREAM, shareableFileUriProvider.getShareableUri(reportFile));
        intent.putExtra(EXTRA_SUBJECT, getExtraSubjectForReport());

        return intent;
    }

    private String getExtraSubjectForReport() {
        return context.getString(R.string.extra_text_for_app_X_on_date_Y,
                context.getString(R.string.app_name),
                DefaultLocaleDateFormatter.dateTime().format(dateProvider.getCurrentDate()));
    }

    private String getExtraTextForReport() {
        return context.getString(R.string.extra_text_for_app_X_on_date_Y,
                context.getString(R.string.app_name),
                DefaultLocaleDateFormatter.dateTime().format(dateProvider.getCurrentDate()));
    }

}
