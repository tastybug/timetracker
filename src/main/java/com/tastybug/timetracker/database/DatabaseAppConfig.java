package com.tastybug.timetracker.database;

import android.content.Context;

import com.tastybug.timetracker.util.AbstractAppConfig;

public class DatabaseAppConfig extends AbstractAppConfig {

    private static final String DATABASE_SCHEMA_VERSION_CURRENT              = "database.schema.version.current";

    private static final String DATABASE_NAME                                = "database.name";

    private static final String DATABASE_SCRIPTS_FOLDER 			         = "database.scripts.folder";
    private static final String DATABASE_SCRIPTS_MODEL_PREFIX 	             = "database.scripts.prefix.model";
    private static final String DATABASE_SCRIPTS_DATA_PREFIX 	             = "database.scripts.prefix.data";

    public DatabaseAppConfig(Context context) {
        super(context);
    }

    public int getCurrentSchemaVersion() {
        return getIntValue(DATABASE_SCHEMA_VERSION_CURRENT);
    }

    public String getDatabaseFileName() {
        return getStringValue(DATABASE_NAME);
    }

    public String getDatabaseScriptsFolder() {
        return getStringValue(DATABASE_SCRIPTS_FOLDER);
    }

    public String getDatabaseScriptsModelPrefix() {
        return getStringValue(DATABASE_SCRIPTS_MODEL_PREFIX);
    }

    public String getDatabaseScriptsDataPrefix() {
        return getStringValue(DATABASE_SCRIPTS_DATA_PREFIX);
    }
}
