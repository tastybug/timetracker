package com.tastybug.timetracker.extensions.reporting.ui.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extensions.reporting.controller.Report;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ShareReportIntentFactoryTest {

    private Context context = mock(Context.class);
    private ReportCacheFileWriter reportCacheFileWriter = mock(ReportCacheFileWriter.class);
    private ShareableReportUriProvider shareableReportUriProvider = mock(ShareableReportUriProvider.class);
    private Report defaultReport = mock(Report.class);

    private ShareReportIntentFactory subject = new ShareReportIntentFactory(context, reportCacheFileWriter, shareableReportUriProvider);

    @Before
    public void setup() throws IOException {
        when(defaultReport.getFirstDay()).thenReturn(new Date());
        when(defaultReport.getLastDay()).thenReturn(new Date());
    }

    @Test(expected = NullPointerException.class)
    public void create_throws_NPE_when_given_null_ReportModel() throws IOException {
        // expect
        subject.create(null);
    }

    @Test
    public void create_produces_intent_containing_localized_EXTRA_TEXT() throws IOException {
        // given
        when(context.getString(eq(R.string.report_for_project_X_with_time_frame_Y_to_Z_message_text), eq("title"), anyString(), anyString())).thenReturn("REPORT_EXTRA_TEXT");
        when(defaultReport.getProjectTitle()).thenReturn("title");
        when(defaultReport.getFirstDay()).thenReturn(new Date());
        when(defaultReport.getLastDay()).thenReturn(new Date());

        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals("REPORT_EXTRA_TEXT", intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Test
    public void create_produces_intent_containing_localized_EXTRA_SUBJECT() throws IOException {
        // given
        when(context.getString(eq(R.string.report_for_project_X_with_time_frame_Y_to_Z), eq("title"), anyString(), anyString())).thenReturn("REPORT_EXTRA_SUBJECT");
        when(defaultReport.getProjectTitle()).thenReturn("title");
        when(defaultReport.getFirstDay()).thenReturn(new Date());
        when(defaultReport.getLastDay()).thenReturn(new Date());

        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals("REPORT_EXTRA_SUBJECT", intent.getStringExtra(Intent.EXTRA_SUBJECT));
    }

    @Test
    public void create_produces_intent_with_correct_action_type() throws IOException {
        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals(Intent.ACTION_SEND, intent.getAction());
    }

    @Test
    public void create_produces_intent_with_mime_type_html() throws IOException {
        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals("text/html", intent.getType());
    }

    @Test
    public void create_produces_intent_that_allows_access_to_the_shared_file() throws IOException {
        // given
        when(shareableReportUriProvider.getShareableUri(any(File.class))).thenReturn(mock(Uri.class));

        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals(Intent.FLAG_GRANT_READ_URI_PERMISSION, intent.getFlags());
    }

    @Test
    public void create_produces_intent_containing_shareable_URI_pointing_to_report_file() throws IOException {
        // given
        File reportTempFile = mock(File.class);
        Uri shareableUri = mock(Uri.class);
        when(reportCacheFileWriter.writeReportToCache(eq(defaultReport))).thenReturn(reportTempFile);
        when(shareableReportUriProvider.getShareableUri(reportTempFile)).thenReturn(shareableUri);

        // when
        Intent intent = subject.create(defaultReport);

        // then
        assertEquals(shareableUri, intent.getParcelableExtra(Intent.EXTRA_STREAM));
    }
}
