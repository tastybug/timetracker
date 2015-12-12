package com.tastybug.timetracker.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.tastybug.timetracker.model.Entity;

import java.util.ArrayList;

public abstract class EntityDAO<T extends Entity> {

    static final String AUTHORITY = "com.tastybug.timetracker";

    protected Context context;
    protected ContentResolverProvider contentResolverProvider = new DefaultContentResolverProvider(context);

    public EntityDAO(Context context) {
        this.context = context;
    }

    protected abstract String getTableName();

    protected Uri getQueryUri() {
        return Uri.parse("content://" + AUTHORITY + "/" + getTableName());
    }

    public T get(int id) {
        Cursor mCursor = contentResolverProvider.query(getColumns(), getPKColumn() + "=?", new String[]{id + ""}, null);
        T t = null;
        if (mCursor != null && mCursor.moveToFirst()) {
            t = createEntityFromCursor(context, mCursor);
            mCursor.close();
        }
        return t;
    }

    public ArrayList<T> getAll () {
        Cursor mCursor = contentResolverProvider.query(getColumns(), null, null, null);
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
        Uri uri = contentResolverProvider.insert(getContentValues(entity));
        entity.setId(Integer.parseInt(uri.getLastPathSegment()));
        entity.setContext(context);
        return uri;
    }

    public int update(T entity) {
        int rowsUpdated = contentResolverProvider.update(getContentValues(entity), getPKColumn() + "=?", new String[]{entity.getId() + ""});
        return rowsUpdated;
    }

    public boolean delete(T entity) {
        int deletionCount = contentResolverProvider.delete(getPKColumn() + "=?", new String[]{entity.getId() + ""});
        return deletionCount == 1;
    }

    public abstract String getPKColumn();

    public abstract String[] getColumns();

    protected abstract T createEntityFromCursor(Context context, Cursor mCursor) throws IllegalArgumentException;

    public void setContentResolverProvider(ContentResolverProvider provider) {
        this.contentResolverProvider = provider;
    }

    protected abstract ContentValues getContentValues(T entity);

    private class DefaultContentResolverProvider implements ContentResolverProvider {

        private Context context;

        DefaultContentResolverProvider(Context context) {
            this.context = context;
        }

        public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            return context.getContentResolver().query(getQueryUri(), projection, selection, selectionArgs, sortOrder);
        }

        public Uri insert(ContentValues values) {
            return context.getContentResolver().insert(getQueryUri(), values);
        }

        public int update(ContentValues values, String where, String[] selectionArgs) {
            return context.getContentResolver().update(getQueryUri(), values, where, selectionArgs);
        }

        public int delete(String where, String[] selectionArgs) {
            return context.getContentResolver().delete(getQueryUri(), where, selectionArgs);
        }
    }
}
