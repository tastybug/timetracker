package com.tastybug.timetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

	private Logger logger = LoggerFactory.getLogger(getClass());


	private static DatabaseHelper sharedInstance;

    private DatabaseAppConfig appConfig;
	private int dbVersion;
	private Context context;


	public static synchronized DatabaseHelper getInstance (Context context) {
		if (sharedInstance == null) {
            DatabaseAppConfig appConfig = new DatabaseAppConfig(context);
            sharedInstance = new DatabaseHelper(context, appConfig);
        }
		return sharedInstance;
	}

	private DatabaseHelper(Context context, DatabaseAppConfig config) {
		super(context,
				config.getDatabaseFileName(),
				null,
				config.getCurrentSchemaVersion());
		this.dbVersion = config.getCurrentSchemaVersion();
		this.context = context;
        this.appConfig = config;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        logger.info("Starting database creation..");
    	db.beginTransaction(); // unlike onUpgrade, onCreate doesnt execute within in transaction implicitly
		db.execSQL("PRAGMA foreign_keys=ON;");
		performDbUpgrade(db, 0, dbVersion); // treat creation as an upgrade from version 0
		db.setTransactionSuccessful();
		db.endTransaction();
        logger.info(".. database creation finished successfully.");
	}

	@Override
	// http://stackoverflow.com/questions/2545558/foreign-key-constraints-in-android-using-sqlite-on-delete-cascade
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		logger.info("Upgrading database from version " + oldVersion + " to " + newVersion + ".");

		performDbUpgrade(db, oldVersion, newVersion);
	}

	private void performDbUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
		String scriptFolder = appConfig.getDatabaseScriptsFolder();
		String modelScriptPrefix = appConfig.getDatabaseScriptsModelPrefix();
		String defaultScriptPrefix = appConfig.getDatabaseScriptsDataPrefix();
		try {
			for (; oldVersion < newVersion; oldVersion++) {
				performIterativeSQLUpgrade(context, db, scriptFolder + File.separator + modelScriptPrefix, oldVersion + 1);
				performIterativeSQLUpgrade(context, db, scriptFolder + File.separator + defaultScriptPrefix, oldVersion + 1);

				// if possible, do a localized upgrade as well
				if (!defaultScriptPrefix.equals(getBestDataFilePrefix(context, scriptFolder, defaultScriptPrefix, newVersion))) {
					performIterativeSQLUpgrade(context,
							db,
							scriptFolder + File.separator + getBestDataFilePrefix(context, scriptFolder, defaultScriptPrefix, newVersion),
							oldVersion + 1);
				}
			}
		} catch (IOException ioe) {
            // to prevent successful completion of the surrounding transaction, we need an unchecked exception
			throw new RuntimeException("Failed db upgrade from " + oldVersion + " to " + newVersion + " due to " + ioe.toString() + ".");
		}
	}

	private String getBestDataFilePrefix (Context context, String scriptFolder, String defaultDataPrefix, int newVersion) {
		try {
			List<String> assets = Arrays.asList(context.getAssets().list(scriptFolder));
			String localizedName = Locale.getDefault().getCountry().toLowerCase(Locale.ENGLISH) + "_" + defaultDataPrefix;
			if (assets.contains(localizedName + newVersion))
				return localizedName;
			else
				return defaultDataPrefix;
		} catch (IOException ioe) {
			logger.error("Error while looking up database script folder: " + ioe.getMessage());
	    	return defaultDataPrefix;
	    }
	}

	/**
	 * This reads a file from assets, executing every line as a SQL-Statement.
	 *
	 * @param context				the context
	 * @param db					the database
	 * @param pathBase				the file prefix (sql/db_ or sql/data_)
	 * @param versionToUpgradeTo	the new db version
	 *
	 * @return number of executed calls
	 */
	private int performIterativeSQLUpgrade (Context context, SQLiteDatabase db, String pathBase, int versionToUpgradeTo) throws IOException {
	    try {
			logger.debug(DatabaseHelper.class.getName(), "Calling database upgrade/creation script for version " + versionToUpgradeTo + ": " + pathBase + versionToUpgradeTo);
		    int result = 0;

		    InputStream myInput = context.getAssets().open(pathBase + versionToUpgradeTo);
		    BufferedReader insertReader = new BufferedReader(new InputStreamReader(myInput));

		    String insertStmt;
		    while (insertReader.ready()) {
		        insertStmt = insertReader.readLine();
		        if (!insertStmt.trim().startsWith("--") && insertStmt.length() > 0) {
		        	db.execSQL(insertStmt);
		        	result++;
		        }
		    }
		    insertReader.close();
			logger.debug("Executed " + result + " statements, not counting empty lines and comments.");
		    return result;
	    } catch (IOException ioe) {
			logger.error("Error while performing iterative database upgrade: " + ioe.toString());
	    	throw ioe;
	    }
	}
}