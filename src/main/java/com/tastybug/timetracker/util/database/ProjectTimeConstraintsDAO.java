package com.tastybug.timetracker.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.ProjectTimeConstraints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProjectTimeConstraintsDAO extends EntityDAO<ProjectTimeConstraints> {

    static String ID_COLUMN = "_id";
    static String PROJECT_FK_COLUMN = "project_fk";
    static String HOUR_LIMIT_COLUMN = "hour_limit";
    static String STARTS_AT_COLUMN = "starts_at";
    static String ENDS_AT_COLUMN = "ends_at";

    static String[] COLUMNS = new String[] {
            ID_COLUMN,
            PROJECT_FK_COLUMN,
            HOUR_LIMIT_COLUMN,
            STARTS_AT_COLUMN,
            ENDS_AT_COLUMN
    };

    public ProjectTimeConstraintsDAO(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return "project_time_constraints";
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
    protected ProjectTimeConstraints createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        try {
            Integer hourLimit = cursor.isNull(colsList.indexOf(HOUR_LIMIT_COLUMN))
                    ? null : cursor.getInt(colsList.indexOf(HOUR_LIMIT_COLUMN));
            String startDateString = cursor.isNull(colsList.indexOf(STARTS_AT_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(STARTS_AT_COLUMN));
            String endDateString = cursor.isNull(colsList.indexOf(ENDS_AT_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(ENDS_AT_COLUMN));
            return new ProjectTimeConstraints(
                    cursor.getInt(colsList.indexOf(ID_COLUMN)),
                    cursor.getInt(colsList.indexOf(PROJECT_FK_COLUMN)),
                    hourLimit,
                    startDateString != null ? getIso8601DateFormatter().parse(startDateString) : null,
                    endDateString != null ? getIso8601DateFormatter().parse(endDateString) : null
            );
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Problem parsing date.", pe);
        }
    }

    @Override
    protected ContentValues getContentValues(ProjectTimeConstraints entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN, entity.getId());
        contentValues.put(PROJECT_FK_COLUMN, entity.getProjectId());
        contentValues.put(HOUR_LIMIT_COLUMN, entity.getHourLimit().orNull());
        contentValues.put(STARTS_AT_COLUMN, formatDate(entity.getStart()));
        contentValues.put(ENDS_AT_COLUMN, formatDate(entity.getEnd()));

        return contentValues;
    }

    private String formatDate(Optional<Date> date) {
        if (date.isPresent()) {
            return getIso8601DateFormatter().format(date);
        }
        return null;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
