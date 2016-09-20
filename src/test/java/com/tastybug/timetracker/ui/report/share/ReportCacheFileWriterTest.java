package com.tastybug.timetracker.ui.report.share;

import android.os.Build;

import com.tastybug.timetracker.infrastructure.filecache.CacheFileWriter;
import com.tastybug.timetracker.report.Report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ReportCacheFileWriterTest {

    private CacheFileWriter cacheFileWriter = mock(CacheFileWriter.class);

    private ReportCacheFileWriter reportCacheFileWriter = new ReportCacheFileWriter(cacheFileWriter);

    @Test
    public void writeReportToCache_writes_report_to_cache_file_and_returns_proper_URI() throws IOException {
        // given
        File expectedReportFile = mock(File.class);
        String htmlContent = "zeHtml";
        Report report = mock(Report.class);
        when(report.getHtml()).thenReturn(htmlContent);
        when(cacheFileWriter.writeToCache(eq("shareable_reports"), eq(reportCacheFileWriter.getSafeFileName()), eq(reportCacheFileWriter.getExtension()), aryEq(htmlContent.getBytes("UTF-8"))))
                .thenReturn(expectedReportFile);

        // when
        File actualReportFile = reportCacheFileWriter.writeReportToCache(report);

        // then
        assertEquals(expectedReportFile, actualReportFile);
    }
}