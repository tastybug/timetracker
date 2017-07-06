package com.tastybug.timetracker.extension.backup.ui;

import android.content.Context;

import com.tastybug.timetracker.infrastructure.filecache.CacheFileWriter;

import java.io.File;
import java.io.IOException;

class DataExportFileWriter {

    private CacheFileWriter cacheFileWriter;

    DataExportFileWriter(Context context) {
        this.cacheFileWriter = new CacheFileWriter(context);
    }

    File writeToCache(byte[] data) throws IOException {
        return cacheFileWriter.writeToCache(getSafeFileName(), getExtension(), data);
    }

    private String getSafeFileName() {
        return "data-export";
    }

    String getExtension() {
        return "json";
    }
}
