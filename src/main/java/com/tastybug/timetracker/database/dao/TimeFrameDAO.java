package com.tastybug.timetracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TimeFrame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeFrameDAO extends EntityDAO<TimeFrame> {

    static String ID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";

    static String[] COLUMNS = new String[] {
            ID_COLUMN,
            PROJECT_UUID_COLUMN,
            START_DATE_COLUMN,
            END_DATE_COLUMN
    };

    public TimeFrameDAO(Context context) {
        super(context);
    }

    public ArrayList<TimeFrame> getByProjectUuid(String uuid) {
        Preconditions.checkNotNull(uuid, "Cannot get time frames by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), PROJECT_UUID_COLUMN + "=?", new String[]{uuid}, null);
        ArrayList<TimeFrame> list = new ArrayList<TimeFrame>();
        while (cursor.moveToNext()) {
            list.add(createEntityFromCursor(context, cursor));
        }
        cursor.close();
        return list;
    }

    @Override
    protected String getTableName() {
        return "time_frame";
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
    protected TimeFrame createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        try {
            String uuid = cursor.getString(colsList.indexOf(ID_COLUMN));
            String projectUuid = cursor.getString(colsList.indexOf(PROJECT_UUID_COLUMN));
            String startAsString = cursor.getString(colsList.indexOf(START_DATE_COLUMN));
            String endAsString = cursor.getString(colsList.indexOf(END_DATE_COLUMN));
            return new TimeFrame(
                    uuid,
                    projectUuid,
                    startAsString != null ? getIso8601DateFormatter().parse(startAsString) : null,
                    endAsString != null ? getIso8601DateFormatter().parse(endAsString) : null
            );
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Problem parsing date.", pe);
        }
    }

    @Override
    protected ContentValues getContentValues(TimeFrame entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN, entity.getUuid());
        contentValues.put(PROJECT_UUID_COLUMN, entity.getProjectUuid());
        contentValues.put(START_DATE_COLUMN, formatDate(entity.getStart()));
        contentValues.put(END_DATE_COLUMN, formatDate(entity.getEnd()));

        return contentValues;
    }

    private String formatDate(Optional<Date> date) {
        if (date.isPresent()) {
            return getIso8601DateFormatter().format(date);
        }
        return null;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }
}
