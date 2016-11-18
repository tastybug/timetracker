package com.tastybug.timetracker.infrastructure.backup.out;

import android.content.Context;

import com.tastybug.timetracker.infrastructure.db.DatabaseConfig;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataChangeIndicatorTest {

    DatabaseConfig databaseConfig = mock(DatabaseConfig.class);
    Context context = mock(Context.class);
    File databaseFile = mock(File.class);

    DataChangeIndicator subject = new DataChangeIndicator(context, databaseConfig);

    @Before
    public void setup() {
        when(databaseConfig.getDatabaseFileName()).thenReturn("a-filename");
        when(context.getDatabasePath(databaseConfig.getDatabaseFileName())).thenReturn(databaseFile);
        when(databaseFile.exists()).thenReturn(true);
    }

    @Test
    public void hasDataChangesSince_returns_false_when_db_file_has_been_modified_before_reference_date() {
        // given
        Date dbFileModifiedOn = new LocalDate(2016, 1, 14).toDate();
        Date referenceDate = new LocalDate(2016, 1, 15).toDate();
        when(databaseFile.lastModified()).thenReturn(dbFileModifiedOn.getTime());

        // when
        boolean result = subject.hasDataChangesSince(referenceDate);

        // then
        assertFalse(result);
    }

    @Test
    public void hasDataChangesSince_returns_true_when_db_file_has_been_modified_after_reference_date() {
        // given
        Date dbFileModifiedOn = new LocalDate(2016, 1, 16).toDate();
        Date referenceDate = new LocalDate(2016, 1, 15).toDate();
        when(databaseFile.lastModified()).thenReturn(dbFileModifiedOn.getTime());

        // when
        boolean result = subject.hasDataChangesSince(referenceDate);

        // then
        assertTrue(result);
    }

    @Test(expected = NullPointerException.class)
    public void hasDataChangesSince_throws_NPE_on_null_reference_date() {
        // expect
        subject.hasDataChangesSince(null);
    }

    // this might be an error state or the backup has been started before the application
    // is fully set up
    @Test
    public void hasDataChangesSince_returns_false_if_no_db_file_was_found() {
        // when
        when(databaseFile.exists()).thenReturn(false);

        // when
        boolean result = subject.hasDataChangesSince(new Date());

        // then
        assertFalse(result);
    }
}