package com.tastybug.timetracker.extensions.reporting.ui.share;

import android.content.Context;
import android.content.Intent;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extensions.reporting.controller.Report;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.io.File;
import java.io.IOException;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class ShareReportIntentFactory {

    private final Context context;
    private ReportCacheFileWriter reportCacheFileWriter;
    private ShareableReportUriProvider shareableReportUriProvider;

    public ShareReportIntentFactory(Context context) {
        this(context, new ReportCacheFileWriter(context), new ShareableReportUriProvider(context));
    }

    ShareReportIntentFactory(Context context,
                             ReportCacheFileWriter reportCacheFileWriter,
                             ShareableReportUriProvider shareableReportUriProvider) {
        this.context = context;
        this.reportCacheFileWriter = reportCacheFileWriter;
        this.shareableReportUriProvider = shareableReportUriProvider;
    }

    public Intent create(Report report) throws IOException {
        Preconditions.checkNotNull(report);

        File reportFile = reportCacheFileWriter.writeReportToCache(report);

        Intent intent = new Intent();
        intent.setAction(ACTION_SEND);
        intent.setType("text/html");
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(EXTRA_TEXT, getExtraTextForReport(report));
        intent.putExtra(Intent.EXTRA_STREAM, shareableReportUriProvider.getShareableUri(reportFile));
        intent.putExtra(EXTRA_SUBJECT, getExtraSubjectForReport(report));

        return intent;
    }

    private String getExtraSubjectForReport(Report model) {
        return context.getString(R.string.report_for_project_X_with_time_frame_Y_to_Z,
                model.getProjectTitle(),
                DefaultLocaleDateFormatter.date().format(model.getFirstDay()),
                DefaultLocaleDateFormatter.date().format(model.getLastDay()));
    }

    private String getExtraTextForReport(Report model) {
        return context.getString(R.string.report_for_project_X_with_time_frame_Y_to_Z_message_text,
                model.getProjectTitle(),
                DefaultLocaleDateFormatter.date().format(model.getFirstDay()),
                DefaultLocaleDateFormatter.date().format(model.getLastDay()));
    }

}
