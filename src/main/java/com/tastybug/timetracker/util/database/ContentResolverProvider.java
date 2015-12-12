package com.tastybug.timetracker.util.database;

import android.database.Cursor;
import android.net.Uri;

public interface ContentResolverProvider {

    Cursor query(java.lang.String[] projection, java.lang.String selection, java.lang.String[] selectionArgs, java.lang.String sortOrder);

    Uri insert(android.content.ContentValues values);

    int update(android.content.ContentValues values, java.lang.String where, java.lang.String[] selectionArgs);

    int delete(java.lang.String where, java.lang.String[] selectionArgs);
}
