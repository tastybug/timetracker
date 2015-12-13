package com.tastybug.timetracker.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.Project;

import java.util.Arrays;
import java.util.List;

public class ProjectDAO extends EntityDAO<Project> {

    static String ID_COLUMN = "_id";
    static String TITLE_COLUMN = "title";
    static String DESCRIPTION_COLUMN = "description";

    static String[] COLUMNS = new String[] {
            ID_COLUMN,
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
        return ID_COLUMN;
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }

    @Override
    protected Project createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        return new Project(
                cursor.getInt(colsList.indexOf(ID_COLUMN)),
                cursor.getString(colsList.indexOf(TITLE_COLUMN)),
                cursor.getString(colsList.indexOf(DESCRIPTION_COLUMN))
                );
    }

    @Override
    protected ContentValues getContentValues(Project entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN, entity.getId());
        contentValues.put(TITLE_COLUMN, entity.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, entity.getDescription().orNull());

        return contentValues;
    }
}
