package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

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
}
