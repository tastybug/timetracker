package com.tastybug.timetracker.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.Project;

import java.util.Arrays;
import java.util.List;

public class ProjectDAO extends EntityDAO<Project> {

    static String UUID_COLUMN = "uuid";
    static String TITLE_COLUMN = "title";
    static String DESCRIPTION_COLUMN = "description";

    static String[] COLUMNS = new String[] {
            UUID_COLUMN,
            TITLE_COLUMN,
            DESCRIPTION_COLUMN
    };


    public ProjectDAO(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return "project";
    }

    @Override
    public String getPKColumn() {
        return UUID_COLUMN;
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }

    @Override
    protected Project createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        return new Project(
                cursor.getString(colsList.indexOf(UUID_COLUMN)),
                cursor.getString(colsList.indexOf(TITLE_COLUMN)),
                cursor.getString(colsList.indexOf(DESCRIPTION_COLUMN))
                );
    }

    @Override
    protected ContentValues getContentValues(Project entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UUID_COLUMN, entity.getUuid());
        contentValues.put(TITLE_COLUMN, entity.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, entity.getDescription().orNull());

        return contentValues;
    }
}
