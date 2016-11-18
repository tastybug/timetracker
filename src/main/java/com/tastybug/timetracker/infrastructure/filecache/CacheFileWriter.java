package com.tastybug.timetracker.infrastructure.filecache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.util.ConditionalLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CacheFileWriter {
    //thread safe
    private static final Random tempFileRandom = new Random();

    private CacheDirectoryProvider cacheDirectoryProvider;

    public CacheFileWriter(Context context) {
        this.cacheDirectoryProvider = new CacheDirectoryProvider(context);
    }

    CacheFileWriter(CacheDirectoryProvider cacheDirectoryProvider) {
        this.cacheDirectoryProvider = cacheDirectoryProvider;
    }

    public File writeToCache(String fileName, String extension, byte[] data) throws IOException {
        Preconditions.checkArgument(data != null && data.length > 0, "Given data is null or empty.");
        Preconditions.checkArgument(fileName != null && fileName.length() > 0, "Given name is null or empty.");
        Preconditions.checkArgument(extension != null && extension.length() > 0, "Given extension is null or empty.");

        File file = getNewTempFileInCacheFolder(fileName, extension);
        return writeDataToFile(data, file);
    }

    @NonNull
    private File getNewTempFileInCacheFolder(String name, String extension) {
        return new File(cacheDirectoryProvider.getCacheDirectory(),
                name + "." + tempFileRandom.nextInt(100000) + "." + extension);
    }

    private File writeDataToFile(byte[] data, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        try {
            outputStream.write(data);
            outputStream.close();
        } finally {
            try {
                outputStream.close();
            } catch (IOException ioe2) {
                ConditionalLog.logError(CacheFileWriter.class.getSimpleName(), "Failed to close out stream for " + file.getAbsolutePath());
            }
        }
        return file;
    }
}
