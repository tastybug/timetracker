package com.tastybug.timetracker.infrastructure.filecache;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

public class ShareableFileUriProvider {

    private Context context;

    public ShareableFileUriProvider(Context context) {
        this.context = context;
    }

    public Uri getShareableUri(File file) {
        return FileProvider.getUriForFile(context, "com.tastybug.timetracker.files", file);
    }
}
