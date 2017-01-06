package com.tastybug.timetracker.infrastructure.filecache;

import android.os.Build;

import com.tastybug.timetracker.util.DateProvider;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CacheCleanerTest {

    private CacheDirectoryProvider cacheDirectoryProvider = mock(CacheDirectoryProvider.class);
    private DateProvider dateProvider = mock(DateProvider.class);
    private File cacheDirectory = mock(File.class);
    private Date NOW = new Date(10000000);

    private CacheCleaner cacheCleaner = new CacheCleaner(cacheDirectoryProvider, dateProvider);

    @Before
    public void setup() {
        when(cacheDirectoryProvider.getCacheDirectory()).thenReturn(cacheDirectory);
        when(dateProvider.getCurrentDate()).thenReturn(NOW);
    }

    @Test
    public void cleanupCache_removes_files_older_that_24h() {
        // given
        File nonPurgeableFile = aNonPurgeableFile();
        File purgeableFile = aPurgeableFile();
        when(cacheDirectory.listFiles()).thenReturn(new File[]{purgeableFile, nonPurgeableFile});

        // when
        cacheCleaner.cleanupCache();

        // then
        verify(nonPurgeableFile, never()).delete();
        verify(purgeableFile).delete();
    }

    private File aNonPurgeableFile() {
        File oldEnough = mock(File.class);
        when(oldEnough.lastModified()).thenReturn(new DateTime(NOW).minusDays(1).toDate().getTime());
        return oldEnough;
    }

    private File aPurgeableFile() {
        File oldEnough = mock(File.class);
        when(oldEnough.lastModified()).thenReturn(new DateTime(NOW).minusDays(1).minusMinutes(1).toDate().getTime());
        return oldEnough;
    }
}