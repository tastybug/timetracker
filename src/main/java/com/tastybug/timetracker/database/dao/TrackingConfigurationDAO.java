package com.tastybug.timetracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackingConfigurationDAO extends EntityDAO<TrackingConfiguration> {

    static String UUID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String HOUR_LIMIT_COLUMN = "hour_limit";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String ROUNDING_STRATEGY_COLUMN = "rounding_strategy";

    static String[] COLUMNS = new String[] {
            UUID_COLUMN,
            PROJECT_UUID_COLUMN,
            HOUR_LIMIT_COLUMN,
            START_DATE_COLUMN,
            END_DATE_COLUMN,
            ROUNDING_STRATEGY_COLUMN
    };

    public TrackingConfigurationDAO(Context context) {
        super(context);
    }

    public Optional<TrackingConfiguration> getByProjectUuid(String uuid) {
        Preconditions.checkNotNull(uuid, "Cannot get tracking records by project uuid, null given!");

        Cursor cursor = context.getContentResolver().query(getQueryUri(), getColumns(), PROJECT_UUID_COLUMN + "=?", new String[]{uuid}, null);
        TrackingConfiguration trackingConfiguration = null;
        if (cursor.moveToFirst()) {
            trackingConfiguration = createEntityFromCursor(context, cursor);
            cursor.close();
        }

        return Optional.fromNullable(trackingConfiguration);
    }

    @Override
    protected String getTableName() {
        return "tracking_configuration";
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
    protected TrackingConfiguration createEntityFromCursor(Context context, Cursor cursor) {
        List<String> colsList = Arrays.asList(COLUMNS);
        try {
            Integer hourLimit = cursor.isNull(colsList.indexOf(HOUR_LIMIT_COLUMN))
                    ? null : cursor.getInt(colsList.indexOf(HOUR_LIMIT_COLUMN));
            String startDateString = cursor.isNull(colsList.indexOf(START_DATE_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(START_DATE_COLUMN));
            String endDateString = cursor.isNull(colsList.indexOf(END_DATE_COLUMN))
                    ? null : cursor.getString(colsList.indexOf(END_DATE_COLUMN));
            return new TrackingConfiguration(
                    cursor.getString(colsList.indexOf(UUID_COLUMN)),
                    cursor.getString(colsList.indexOf(PROJECT_UUID_COLUMN)),
                    hourLimit,
                    startDateString != null ? getIso8601DateFormatter().parse(startDateString) : null,
                    endDateString != null ? getIso8601DateFormatter().parse(endDateString) : null,
                    RoundingFactory.Strategy.valueOf(cursor.getString(colsList.indexOf(ROUNDING_STRATEGY_COLUMN)))
            );
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Problem parsing date.", pe);
        }
    }

    @Override
    protected ContentValues getContentValues(TrackingConfiguration entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UUID_COLUMN, entity.getUuid());
        contentValues.put(HOUR_LIMIT_COLUMN, entity.getHourLimit().orNull());
        contentValues.put(PROJECT_UUID_COLUMN, entity.getProjectUuid());
        contentValues.put(START_DATE_COLUMN, formatDate(entity.getStart()));
        contentValues.put(END_DATE_COLUMN, formatDate(entity.getEnd()));
        contentValues.put(ROUNDING_STRATEGY_COLUMN, entity.getRoundingStrategy().name());

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
