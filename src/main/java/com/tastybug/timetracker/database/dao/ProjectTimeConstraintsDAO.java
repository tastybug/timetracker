package com.tastybug.timetracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.ProjectTimeConstraints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProjectTimeConstraintsDAO extends EntityDAO<ProjectTimeConstraints> {

    static String UUID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String HOUR_LIMIT_COLUMN = "hour_limit";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";

    static String[] COLUMNS = new String[] {
            UUID_COLUMN,
            PROJECT_UUID_COLUMN,
            HOUR_LIMIT_COLUMN,
            START_DATE_COLUMN,
            END_DATE_COLUMN
    };

    public ProjectTimeConstraintsDAO(Context context) {
        super(context);
    }

    public Optional<ProjectTimeConstraints> getByProjectUuid(String uuid) {
        Preconditions.checkNotNull(uuid, "Cannot get time frames by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), PROJECT_UUID_COLUMN + "=?", new String[]{uuid}, null);
        ProjectTimeConstraints constraints = null;
        if (cursor.moveToFirst()) {
            constraints = createEntityFromCursor(context, cursor);
            cursor.close();
        }

        return Optional.fromNullable(constraints);
    }

    @Override
    protected String getTableName() {
        return "project_time_constraints";
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
    protected ProjectTimeConstraints createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        try {
            Integer hourLimit = cursor.isNull(colsList.indexOf(HOUR_LIMIT_COLUMN))
                    ? null : cursor.getInt(colsList.indexOf(HOUR_LIMIT_COLUMN));
            String startDateString = cursor.isNull(colsList.indexOf(START_DATE_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(START_DATE_COLUMN));
            String endDateString = cursor.isNull(colsList.indexOf(END_DATE_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(END_DATE_COLUMN));
            return new ProjectTimeConstraints(
                    cursor.getString(colsList.indexOf(UUID_COLUMN)),
                    cursor.getString(colsList.indexOf(PROJECT_UUID_COLUMN)),
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
        contentValues.put(UUID_COLUMN, entity.getUuid());
        contentValues.put(HOUR_LIMIT_COLUMN, entity.getHourLimit().orNull());
        contentValues.put(PROJECT_UUID_COLUMN, entity.getProjectUuid());
        contentValues.put(START_DATE_COLUMN, formatDate(entity.getStart()));
        contentValues.put(END_DATE_COLUMN, formatDate(entity.getEnd()));

        return contentValues;
    }

    private String formatDate(Optional<Date> date) {
        if (date.isPresent()) {
            return getIso8601DateFormatter().format(date.get());
        }
        return null;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
