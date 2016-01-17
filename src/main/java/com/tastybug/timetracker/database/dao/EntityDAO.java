package com.tastybug.timetracker.database.dao;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Entity;

import java.util.ArrayList;

public abstract class EntityDAO<T extends Entity> {

    public static final String AUTHORITY = "com.tastybug.timetracker";

    protected Context context;

    public EntityDAO(Context context) {
        this.context = context;
    }

    protected abstract String getTableName();

    public Uri getQueryUri() {
        return Uri.parse("content://" + AUTHORITY + "/" + getTableName());
    }
    public Uri getUpdateUri(T entity) {
        return Uri.parse("content://" + AUTHORITY + "/" + getTableName() + "/" + entity.getUuid());
    }

    public Optional<T> get(String uuid) {
        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), getPKColumn() + "=?", new String[]{uuid}, null);
        T t = null;
        if (cursor.moveToFirst()) {
            t = createEntityFromCursor(context, cursor);
            cursor.close();
        }
        return Optional.fromNullable(t);
    }

    public ArrayList<T> getAll () {
        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), null, null, null);
        ArrayList<T> list = new ArrayList<T>();
        while (cursor.moveToNext()) {
            list.add(createEntityFromCursor(context, cursor));
        }
        cursor.close();
        return list;
    }

    public Uri create (T entity) {
        Uri uri = context.getContentResolver().insert(getQueryUri(), getContentValues(entity));
        entity.setContext(context);
        return uri;
    }

    public int update(T entity) {
        int rowsUpdated = context.getContentResolver().update(getUpdateUri(entity), getContentValues(entity), getPKColumn() + "=?", new String[]{entity.getUuid()});
        return rowsUpdated;
    }

    public boolean delete(T entity) {
        int deletionCount = context.getContentResolver().delete(getQueryUri(), getPKColumn() + "=?", new String[]{entity.getUuid()});
        return deletionCount == 1;
    }

    public boolean delete(String uuid) {
        int deletionCount = context.getContentResolver().delete(getQueryUri(), getPKColumn() + "=?", new String[]{uuid});
        return deletionCount == 1;
    }

    public ContentProviderOperation getBatchCreate(T entity) {
        return ContentProviderOperation.newInsert(getQueryUri()).withValues(getContentValues(entity)).build();
    }

    public ContentProviderOperation getBatchUpdate(T entity) {
        return ContentProviderOperation.newUpdate(getUpdateUri(entity)).withValues(getContentValues(entity)).withSelection(getPKColumn() + "=?", new String[]{entity.getUuid()}).build();
    }

    public abstract String getPKColumn();

    public abstract String[] getColumns();

    protected abstract T createEntityFromCursor(Context context, Cursor mCursor) throws IllegalArgumentException;

    protected abstract ContentValues getContentValues(T entity);

}
