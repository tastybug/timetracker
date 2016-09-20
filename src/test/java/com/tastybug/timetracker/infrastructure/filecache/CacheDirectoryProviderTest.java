package com.tastybug.timetracker.infrastructure.filecache;

import android.content.Context;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CacheDirectoryProviderTest {

    private Context context = mock(Context.class);
    private CacheDirectoryProvider subject = new CacheDirectoryProvider(context);

    @Test
    public void getCacheDir_returns_cache_dir_from_context() {
        // given
        File cacheDir = mock(File.class);
        when(context.getCacheDir()).thenReturn(cacheDir);
        when(cacheDir.exists()).thenReturn(true);

        // when
        File returnedCacheDir = subject.getCacheDir();

        // then
        assertEquals(cacheDir, returnedCacheDir);
    }

    @Test(expected = IllegalStateException.class)
    public void getCacheDir_throws_IllegalState_if_applications_cache_dir_is_null() {
        // given
        when(context.getCacheDir()).thenReturn(null);

        // when
        subject.getCacheDir();
    }

    @Test(expected = IllegalStateException.class)
    public void getCacheDir_throws_IllegalState_if_applications_cache_dir_isnt_null_but_doesnt_exist() {
        // given
        File cacheDir = mock(File.class);
        when(context.getCacheDir()).thenReturn(cacheDir);
        when(cacheDir.exists()).thenReturn(false);

        // when
        subject.getCacheDir();
    }
}