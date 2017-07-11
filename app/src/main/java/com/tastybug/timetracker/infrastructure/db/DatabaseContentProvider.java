package com.tastybug.timetracker.infrastructure.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * This does NOT close dbConnections on its own, see here:
 * http://stackoverflow.com/questions/4547461/closing-the-database-in-a-contentprovider
 */
public class DatabaseContentProvider extends android.content.ContentProvider {

    protected DatabaseHelper databaseHelper;

    static String getTableByUri(@NonNull Uri uri) {
        return uri.getPathSegments().get(0);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(getTableByUri(uri));

        Cursor cursor = queryBuilder.query(databaseHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return databaseHelper.getWritableDatabase().delete(getTableByUri(uri), selection, selectionArgs);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long returnVal = databaseHelper.getWritableDatabase().insert(getTableByUri(uri), null, values);
        return ContentUris.withAppendedId(uri, returnVal);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return databaseHelper.getWritableDatabase().update(getTableByUri(uri), values, selection, selectionArgs);
    }
}