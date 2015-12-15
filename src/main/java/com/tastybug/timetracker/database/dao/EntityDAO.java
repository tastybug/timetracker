package com.tastybug.timetracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.tastybug.timetracker.model.Entity;

import java.util.ArrayList;

public abstract class EntityDAO<T extends Entity> {

    static final String AUTHORITY = "com.tastybug.timetracker";

    protected Context context;

    public EntityDAO(Context context) {
        this.context = context;
    }

    protected abstract String getTableName();

    protected Uri getQueryUri() {
        return Uri.parse("content://" + AUTHORITY + "/" + getTableName());
    }

    public T get(int id) {
        Cursor mCursor = context.getContentResolver().query(getQueryUri(), getColumns(), getPKColumn() + "=?", new String[]{id + ""}, null);
        T t = null;
        if (mCursor != null && mCursor.moveToFirst()) {
            t = createEntityFromCursor(context, mCursor);
            mCursor.close();
        }
        return t;
    }

    public ArrayList<T> getAll () {
        Cursor mCursor = context.getContentResolver().query(getQueryUri(), getColumns(), null, null, null);
        ArrayList<T> list = new ArrayList<T>();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                list.add(createEntityFromCursor(context, mCursor));
            }
            mCursor.close();
        }
        return list;
    }

    public Uri create (T entity) {
        Uri uri = context.getContentResolver().insert(getQueryUri(), getContentValues(entity));
        entity.setContext(context);
        return uri;
    }

    public int update(T entity) {
        int rowsUpdated = context.getContentResolver().update(getQueryUri(), getContentValues(entity), getPKColumn() + "=?", new String[]{entity.getUuid()});
        return rowsUpdated;
    }

    public boolean delete(T entity) {
        int deletionCount = context.getContentResolver().delete(getQueryUri(), getPKColumn() + "=?", new String[]{entity.getUuid()});
        return deletionCount == 1;
    }

    public abstract String getPKColumn();

    public abstract String[] getColumns();

    protected abstract T createEntityFromCursor(Context context, Cursor mCursor) throws IllegalArgumentException;

    protected abstract ContentValues getContentValues(T entity);

}
