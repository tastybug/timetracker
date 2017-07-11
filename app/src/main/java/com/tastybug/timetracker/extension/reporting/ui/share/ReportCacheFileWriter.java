package com.tastybug.timetracker.extension.reporting.ui.share;

import android.content.Context;

import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;
import com.tastybug.timetracker.infrastructure.filecache.CacheFileWriter;

import java.io.File;
import java.io.IOException;

class ReportCacheFileWriter {

    private CacheFileWriter cacheFileWriter;
    private String fileExtension;

    ReportCacheFileWriter(Context context) {
        this.cacheFileWriter = new CacheFileWriter(context);
    }

    ReportCacheFileWriter(CacheFileWriter cacheFileWriter) {
        this.cacheFileWriter = cacheFileWriter;
    }

    File writeReportToCache(Report report) throws IOException {
        byte[] data = report.getContent().getBytes("UTF-8");
        fileExtension = report.getFileExtension();
        return cacheFileWriter.writeToCache(getSafeFileName(), getExtension(), data);
    }

    String getSafeFileName() {
        return "report";
    }

    String getExtension() {
        return fileExtension;
    }
}
