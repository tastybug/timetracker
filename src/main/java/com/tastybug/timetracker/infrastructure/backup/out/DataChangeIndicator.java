package com.tastybug.timetracker.infrastructure.backup.out;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.db.DatabaseAppConfig;

import java.io.File;
import java.util.Date;

/**
 * This class tells whether a data change has happened since a given date.
 * Based on this indication backups will happen or be omitted.
 */
public class DataChangeIndicator {

    private DatabaseAppConfig databaseAppConfig;
    private Context context;

    public DataChangeIndicator(Context context) {
        this.context = context;
        this.databaseAppConfig = new DatabaseAppConfig(context);
    }

    public DataChangeIndicator(Context context,
                               DatabaseAppConfig databaseAppConfig) {
        this.context = context;
        this.databaseAppConfig = databaseAppConfig;
    }

    public boolean hasDataChangesSince(Date referenceDate) {
        Preconditions.checkNotNull(referenceDate);
        Optional<Date> lastModification = getLastModificationDate();
        return lastModification.isPresent() && referenceDate.before(lastModification.get());
    }

    private Optional<Date> getLastModificationDate() {
        File dbFile = context.getDatabasePath(databaseAppConfig.getDatabaseFileName());
        return dbFile.exists() ? Optional.of(new Date(dbFile.lastModified())) : Optional.<Date>absent();
    }
}
