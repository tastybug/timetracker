package com.tastybug.timetracker.ui.manualbackup.share;

import android.content.Context;

import com.tastybug.timetracker.infrastructure.filecache.CacheFileWriter;

import java.io.File;
import java.io.IOException;

class DataExportFileWriter {

    private CacheFileWriter cacheFileWriter;

    DataExportFileWriter(Context context) {
        this.cacheFileWriter = new CacheFileWriter(context);
    }

    DataExportFileWriter(CacheFileWriter cacheFileWriter) {
        this.cacheFileWriter = cacheFileWriter;
    }

    File writeToCache(byte[] data) throws IOException {
        return cacheFileWriter.writeToCache(getSafeFileName(), getExtension(), data);
    }

    String getSafeFileName() {
        return "data-export";
    }

    String getExtension() {
        return "json";
    }
}
