package com.tastybug.timetracker.core.model.json;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

class TrackingRecordJSON extends JSONObject {

    static String ID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String DESCRIPTION_COLUMN = "description";
    static String ROUNDING_STRATEGY_COLUMN = "rounding_strategy";

    TrackingRecordJSON(TrackingRecord trackingRecord) throws JSONException {
        put(ID_COLUMN, trackingRecord.getUuid());
        put(PROJECT_UUID_COLUMN, trackingRecord.getProjectUuid());
        if (trackingRecord.getStart().isPresent()) {
            put(START_DATE_COLUMN, DefaultLocaleDateFormatter.iso8601().format(trackingRecord.getStart().get()));
        }
        if (trackingRecord.getEnd().isPresent()) {
            put(END_DATE_COLUMN, DefaultLocaleDateFormatter.iso8601().format(trackingRecord.getEnd().get()));
        }
        if (trackingRecord.getDescription().isPresent()) {
            put(DESCRIPTION_COLUMN, trackingRecord.getDescription().get());
        }
        put(ROUNDING_STRATEGY_COLUMN, trackingRecord.getRoundingStrategy().name());
    }

    TrackingRecordJSON(JSONObject jsonObject) throws JSONException {
        put(ID_COLUMN, jsonObject.get(ID_COLUMN));
        put(PROJECT_UUID_COLUMN, jsonObject.get(PROJECT_UUID_COLUMN));
        put(START_DATE_COLUMN, jsonObject.isNull(START_DATE_COLUMN) ? null : jsonObject.getString(START_DATE_COLUMN));
        put(END_DATE_COLUMN, jsonObject.isNull(END_DATE_COLUMN) ? null : jsonObject.getString(END_DATE_COLUMN));
        put(DESCRIPTION_COLUMN, jsonObject.isNull(DESCRIPTION_COLUMN) ? null : jsonObject.getString(DESCRIPTION_COLUMN));
        put(ROUNDING_STRATEGY_COLUMN, jsonObject.getString(ROUNDING_STRATEGY_COLUMN));
    }

    TrackingRecord toTrackingRecord() throws JSONException, ParseException {
        return new TrackingRecord(
                getString(ID_COLUMN),
                getString(PROJECT_UUID_COLUMN),
                isNull(START_DATE_COLUMN) ? Optional.<Date>absent() : Optional.of(DefaultLocaleDateFormatter.iso8601().parse(getString(START_DATE_COLUMN))),
                isNull(END_DATE_COLUMN) ? Optional.<Date>absent() : Optional.of(DefaultLocaleDateFormatter.iso8601().parse(getString(END_DATE_COLUMN))),
                Optional.fromNullable(optString(DESCRIPTION_COLUMN, null)),
                Rounding.Strategy.valueOf(getString(ROUNDING_STRATEGY_COLUMN))
        );
    }
}
