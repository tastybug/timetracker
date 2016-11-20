package com.tastybug.timetracker.infrastructure.filecache;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CacheDirectoryProviderTest {

    static final String APP_CACHE_FOLDER_NAME = "appCacheFolder";

    @Rule
    public TemporaryFolder rootCacheFolder = new TemporaryFolder();
    private Context context = mock(Context.class);
    private CacheDirectoryProvider subject = new CacheDirectoryProvider(context);

    @Test
    public void getCacheDirectory_returns_cache_directory_based_on_root_cache_folder() {
        // given
        when(context.getCacheDir()).thenReturn(rootCacheFolder.getRoot());

        // when
        File returnedCacheDir = subject.getCacheDirectory();

        // then
        assertEquals(rootCacheFolder.getRoot(), returnedCacheDir.getParentFile());
        assertEquals(returnedCacheDir.getName(), APP_CACHE_FOLDER_NAME);
    }

    @Test(expected = IllegalStateException.class)
    public void getCacheDirectory_throws_IllegalState_if_applications_cache_dir_is_null() {
        // given
        when(context.getCacheDir()).thenReturn(null);

        // when
        subject.getCacheDirectory();
    }

    @Test(expected = IllegalStateException.class)
    public void getCacheDirectory_throws_IllegalState_if_rootcache_dir_isnt_null_but_doesnt_exist() {
        // given
        File cacheDir = mock(File.class);
        when(context.getCacheDir()).thenReturn(cacheDir);
        when(cacheDir.exists()).thenReturn(false);

        // when
        subject.getCacheDirectory();
    }
}