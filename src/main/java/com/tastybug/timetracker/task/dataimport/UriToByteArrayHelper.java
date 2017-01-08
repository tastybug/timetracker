package com.tastybug.timetracker.task.dataimport;

import android.content.ContentResolver;
import android.net.Uri;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class UriToByteArrayHelper {

    private ContentResolver contentResolver;

    public UriToByteArrayHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public byte[] readByteArrayFromUri(Uri uri) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            return IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
    }
}
