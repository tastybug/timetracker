package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class TrackingRecordJSON extends JSONObject {

    static String ID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String DESCRIPTION_COLUMN = "description";

    protected TrackingRecordJSON(TrackingRecord trackingRecord) throws JSONException {
        put(ID_COLUMN, trackingRecord.getUuid());
        put(PROJECT_UUID_COLUMN, trackingRecord.getProjectUuid());
        if (trackingRecord.getStart().isPresent()) {
            put(START_DATE_COLUMN, Formatter.iso8601().format(trackingRecord.getStart().get()));
        }
        if (trackingRecord.getEnd().isPresent()) {
            put(END_DATE_COLUMN, Formatter.iso8601().format(trackingRecord.getEnd().get()));
        }
        if (trackingRecord.getDescription().isPresent()) {
            put(DESCRIPTION_COLUMN, trackingRecord.getDescription().get());
        }
    }

    protected TrackingRecordJSON(JSONObject jsonObject) throws JSONException {
        put(ID_COLUMN, jsonObject.get(ID_COLUMN));
        put(PROJECT_UUID_COLUMN, jsonObject.get(PROJECT_UUID_COLUMN));
        put(START_DATE_COLUMN, jsonObject.optString(START_DATE_COLUMN));
        put(END_DATE_COLUMN, jsonObject.optString(END_DATE_COLUMN));
        put(DESCRIPTION_COLUMN, jsonObject.optString(DESCRIPTION_COLUMN));
    }

    protected TrackingRecord getTrackingRecord() throws JSONException, ParseException {
        return new TrackingRecord(
                getString(ID_COLUMN),
                getString(PROJECT_UUID_COLUMN),
                isNull(START_DATE_COLUMN) ? null : Formatter.iso8601().parse(getString(START_DATE_COLUMN)),
                isNull(END_DATE_COLUMN) ? null : Formatter.iso8601().parse(getString(END_DATE_COLUMN)),
                optString(DESCRIPTION_COLUMN)
        );
    }
}
