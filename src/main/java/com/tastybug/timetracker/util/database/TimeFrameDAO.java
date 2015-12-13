package com.tastybug.timetracker.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tastybug.timetracker.model.TimeFrame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class TimeFrameDAO extends EntityDAO<TimeFrame> {

    static String ID_COLUMN = "_id";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";

    static String[] COLUMNS = new String[] {
            ID_COLUMN,
            START_DATE_COLUMN,
            END_DATE_COLUMN
    };

    public TimeFrameDAO(Context context) {
        super(context);
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
            int id = cursor.getInt(colsList.indexOf(ID_COLUMN));
            String startAsString = cursor.getString(colsList.indexOf(START_DATE_COLUMN));
            String endAsString = cursor.getString(colsList.indexOf(END_DATE_COLUMN));
            return new TimeFrame(
                    id,
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
        contentValues.put(ID_COLUMN, entity.getId());
        contentValues.put(START_DATE_COLUMN, entity.hasStart() ? getIso8601DateFormatter().format(entity.getStart()) : null);
        contentValues.put(END_DATE_COLUMN, entity.hasEnd() ? getIso8601DateFormatter().format(entity.getEnd()) : null);

        return contentValues;
    }

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
