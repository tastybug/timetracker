package com.tastybug.timetracker.extensions.reporting.controller.internal.html;

import android.content.res.AssetManager;

import com.tastybug.timetracker.util.ConditionalLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

class TemplateAssetProvider {

    private static final String REPORT_ASSET_LOCATION = "report/report.html";
    private AssetManager assetManager;

    TemplateAssetProvider(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    String getReportTemplate() throws IOException {
        StringBuilder templateBuilder = new StringBuilder();
        InputStream inStream = assetManager.open(REPORT_ASSET_LOCATION);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));

        try {
            char[] buffer = new char[4096];
            int bufferLength;
            while ((bufferLength = bufferedReader.read(buffer)) != -1) {
                templateBuilder.append(Arrays.copyOfRange(buffer, 0, bufferLength));
            }
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ioe) {
                ConditionalLog.logError(TemplateAssetProvider.class.getSimpleName(),
                        "Problem closing reader.",
                        ioe);
            }
        }

        return templateBuilder.toString();
    }
}
