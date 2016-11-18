package com.tastybug.timetracker.infrastructure.backup.out;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.db.DatabaseConfig;

import java.io.File;
import java.util.Date;

/**
 * This class tells whether a data change has happened since a given date.
 * Based on this indication backups will happen or be omitted.
 */
class DataChangeIndicator {

    private DatabaseConfig databaseConfig;
    private Context context;

    DataChangeIndicator(Context context) {
        this.context = context;
        this.databaseConfig = new DatabaseConfig(context);
    }

    DataChangeIndicator(Context context,
                        DatabaseConfig databaseConfig) {
        this.context = context;
        this.databaseConfig = databaseConfig;
    }

    boolean hasDataChangesSince(Date referenceDate) {
        Preconditions.checkNotNull(referenceDate);
        Optional<Date> lastModification = getLastModificationDate();
        return lastModification.isPresent() && referenceDate.before(lastModification.get());
    }

    private Optional<Date> getLastModificationDate() {
        File dbFile = context.getDatabasePath(databaseConfig.getDatabaseFileName());
        return dbFile.exists() ? Optional.of(new Date(dbFile.lastModified())) : Optional.<Date>absent();
    }
}
