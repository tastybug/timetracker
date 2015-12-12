package com.tastybug.timetracker.util.database;

import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.Project;

import java.util.Arrays;
import java.util.List;

public class ProjectDAO extends EntityDAO<Project> {

    static String ID_COLUMN = "_ID";
    static String TITLE_COLUMN = "TITLE";
    static String DESCRIPTION_COLUMN = "DESCRIPTION";

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
        return "PROJECT";
    }

    @Override
    public String getPKColumn() {
        return "_id";
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
}
