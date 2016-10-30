package com.tastybug.timetracker.ui.report.share;

import android.content.Context;

import com.tastybug.timetracker.infrastructure.filecache.CacheFileWriter;
import com.tastybug.timetracker.report.Report;

import java.io.File;
import java.io.IOException;

public class ReportCacheFileWriter {

    private static final String SHAREABLE_REPORT_DIR_NAME = "shareable_reports";

    private CacheFileWriter cacheFileWriter;

    ReportCacheFileWriter(Context context) {
        this.cacheFileWriter = new CacheFileWriter(context);
    }

    ReportCacheFileWriter(CacheFileWriter cacheFileWriter) {
        this.cacheFileWriter = cacheFileWriter;
    }

    File writeReportToCache(Report report) throws IOException {
        byte[] data = report.getHtml().getBytes("UTF-8");

        return cacheFileWriter.writeToCache(SHAREABLE_REPORT_DIR_NAME, getSafeFileName(), getExtension(), data);
    }

    String getSafeFileName() {
        return "report";
    }

    String getExtension() {
        return "html";
    }
}
