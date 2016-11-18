package com.tastybug.timetracker.infrastructure.filecache;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CacheFileWriterTest {

    @Rule
    public TemporaryFolder pseudoCacheDir = new TemporaryFolder();

    private CacheDirectoryProvider cacheDirectoryProvider = mock(CacheDirectoryProvider.class);
    private CacheFileWriter subject = new CacheFileWriter(cacheDirectoryProvider);

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_null_data() throws IOException {
        // expect
        subject.writeToCache("filename", "txt", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_empty_data() throws IOException {
        // expect
        subject.writeToCache("filename", "txt", new byte[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_null_filename() throws IOException {
        // expect
        subject.writeToCache(null, "txt", new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_empty_filename() throws IOException {
        // expect
        subject.writeToCache("", "txt", new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_null_extension() throws IOException {
        // expect
        subject.writeToCache("filename", null, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToCache_throws_IllegalArgument_on_empty_extension() throws IOException {
        // expect
        subject.writeToCache("filename", "", new byte[]{1, 2, 3});
    }

    @Test
    public void writeToCache_writes_data_to_file() throws IOException {
        // given
        byte[] data = new byte[]{1, 2, 3};

        // when
        File file = subject.writeToCache("filename", "txt", data);

        // then
        assertArrayEquals(IOUtils.toByteArray(file.toURI()), data);
    }

    @Test
    public void writeToCache_writes_file_into_temp_folder_provided_by_CacheDirectoryProvider() throws IOException {
        // given
        byte[] data = new byte[]{1, 2, 3};
        File tempFolder = pseudoCacheDir.newFolder();
        when(cacheDirectoryProvider.getCacheDirectory()).thenReturn(tempFolder);

        // when
        File tempFile = subject.writeToCache("filename", "txt", data);

        // then
        assertEquals(tempFile.getParentFile(), tempFolder);
    }
}