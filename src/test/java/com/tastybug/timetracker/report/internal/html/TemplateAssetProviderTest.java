package com.tastybug.timetracker.report.internal.html;

import android.content.res.AssetManager;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TemplateAssetProviderTest {

    private final String assetLocation = "report/report.html";
    private AssetManager assetManager = mock(AssetManager.class);
    private TemplateAssetProvider templateAssetProvider = new TemplateAssetProvider(assetManager);

    private String template = "Zeile 1\nZeile 2\nZeile 3\nZeile 4\n\n\nZeile5\n";

    @Test(expected = IOException.class)
    public void getReportTemplate_rethrows_FileNotFoundException_when_asset_is_missing() throws IOException {
        // given
        when(assetManager.open(anyString())).thenThrow(new FileNotFoundException("xx"));

        // when
        templateAssetProvider.getReportTemplate();
    }

    @Test
    public void getReportTemplate_uses_correct_path_to_lookup_asset() throws IOException {
        // given
        when(assetManager.open(assetLocation)).thenReturn(anInputStreamOf(template));

        // expect: no exception
        templateAssetProvider.getReportTemplate();
    }

    @Test
    public void getReportTemplate_reads_template_correctly() throws IOException {
        // given
        when(assetManager.open(assetLocation)).thenReturn(anInputStreamOf(template));

        // when
        String templateResult = templateAssetProvider.getReportTemplate();

        // then
        assertEquals(template, templateResult);
    }

    @Test
    public void getReportTemplate_correctly_returns_empty_templates() throws IOException {
        // given
        when(assetManager.open(assetLocation)).thenReturn(anInputStreamOf(""));

        // when
        String templateResult = templateAssetProvider.getReportTemplate();

        // then
        assertEquals("", templateResult);
    }

    private InputStream anInputStreamOf(String templateToStream) throws IOException {
        return new ByteArrayInputStream(templateToStream.getBytes("UTF-8"));
    }
}