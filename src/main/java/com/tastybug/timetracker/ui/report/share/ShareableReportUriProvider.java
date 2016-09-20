package com.tastybug.timetracker.ui.report.share;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

class ShareableReportUriProvider {

    private Context context;

    ShareableReportUriProvider(Context context) {
        this.context = context;
    }

    Uri getShareableUri(File file) {
        return FileProvider.getUriForFile(context, "com.tastybug.timetracker.files", file);
    }
}
