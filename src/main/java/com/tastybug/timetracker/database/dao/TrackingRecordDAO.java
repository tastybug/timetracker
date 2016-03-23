package com.tastybug.timetracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackingRecordDAO extends EntityDAO<TrackingRecord> {

    static String ID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String DESCRIPTION_COLUMN = "description";

    static String[] COLUMNS = new String[] {
            ID_COLUMN,
            PROJECT_UUID_COLUMN,
            START_DATE_COLUMN,
            END_DATE_COLUMN,
            DESCRIPTION_COLUMN
    };

    public TrackingRecordDAO(Context context) {
        super(context);
    }

    public Optional<TrackingRecord> getLatestByStartDateForProjectUuid(String projectUuid) {
        Preconditions.checkNotNull(projectUuid, "Cannot get tracking records by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(),
                getColumns(),
                PROJECT_UUID_COLUMN + "=?",
                new String[]{projectUuid},
                START_DATE_COLUMN + " DESC");
        TrackingRecord record = null;
        if (cursor.moveToFirst()) {
            record = createEntityFromCursor(context, cursor);
        }
        cursor.close();
        return Optional.fromNullable(record);
    }

    public ArrayList<TrackingRecord> getByProjectUuid(String uuid) {
        Preconditions.checkNotNull(uuid, "Cannot get tracking records by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), PROJECT_UUID_COLUMN + "=?", new String[]{uuid}, null);
        ArrayList<TrackingRecord> list = new ArrayList<TrackingRecord>();
        while (cursor.moveToNext()) {
            list.add(createEntityFromCursor(context, cursor));
        }
        cursor.close();
        return list;
    }

    public Optional<TrackingRecord> getRunning(String projectUuid) {
        Preconditions.checkNotNull(projectUuid, "Cannot get tracking records by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(),
                getColumns(),
                PROJECT_UUID_COLUMN + "=? AND " + END_DATE_COLUMN + " IS NULL",
                new String[]{projectUuid},
                null);
        TrackingRecord trackingRecord = null;
        if (cursor.moveToNext()) {
            trackingRecord = createEntityFromCursor(context, cursor);
        }
        cursor.close();
        return Optional.fromNullable(trackingRecord);
    }

    @Override
    protected String getTableName() {
        return "tracking_record";
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
    protected TrackingRecord createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        try {
            String uuid = cursor.getString(colsList.indexOf(ID_COLUMN));
            String projectUuid = cursor.getString(colsList.indexOf(PROJECT_UUID_COLUMN));
            String startAsString = cursor.getString(colsList.indexOf(START_DATE_COLUMN));
            String endAsString = cursor.getString(colsList.indexOf(END_DATE_COLUMN));
            String description = cursor.getString(colsList.indexOf(DESCRIPTION_COLUMN));
            return new TrackingRecord(
                    uuid,
                    projectUuid,
                    startAsString != null ? getIso8601DateFormatter().parse(startAsString) : null,
                    endAsString != null ? getIso8601DateFormatter().parse(endAsString) : null,
                    description
            );
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Problem parsing date.", pe);
        }
    }

    @Override
    protected ContentValues getContentValues(TrackingRecord entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN, entity.getUuid());
        contentValues.put(PROJECT_UUID_COLUMN, entity.getProjectUuid());
        contentValues.put(START_DATE_COLUMN, formatDate(entity.getStart()));
        contentValues.put(END_DATE_COLUMN, formatDate(entity.getEnd()));
        contentValues.put(DESCRIPTION_COLUMN, entity.getDescription().orNull());

        return contentValues;
    }

    private String formatDate(Optional<Date> date) {
        if (date.isPresent()) {
            return getIso8601DateFormatter().format(date.get());
        }
        return null;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }
}
