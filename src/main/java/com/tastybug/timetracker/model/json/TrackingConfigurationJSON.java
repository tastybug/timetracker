package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class TrackingConfigurationJSON extends JSONObject {

    static String UUID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String HOUR_LIMIT_COLUMN = "hour_limit";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String PROMPT_FOR_DESCRIPTION_COLUMN = "prompt_for_description";
    static String ROUNDING_STRATEGY_COLUMN = "rounding_strategy";

    protected TrackingConfigurationJSON(TrackingConfiguration trackingConfiguration) throws JSONException {
        put(UUID_COLUMN, trackingConfiguration.getUuid());
        put(PROJECT_UUID_COLUMN, trackingConfiguration.getProjectUuid());
        if (trackingConfiguration.getHourLimit().isPresent()) {
            put(HOUR_LIMIT_COLUMN, trackingConfiguration.getHourLimit().get());
        }
        if (trackingConfiguration.getStart().isPresent()) {
            put(START_DATE_COLUMN, Formatter.iso8601().format(trackingConfiguration.getStart().get()));
        }
        if (trackingConfiguration.getEnd().isPresent()) {
            put(END_DATE_COLUMN, Formatter.iso8601().format(trackingConfiguration.getEnd().get()));
        }
        put(PROMPT_FOR_DESCRIPTION_COLUMN, trackingConfiguration.isPromptForDescription());
        put(ROUNDING_STRATEGY_COLUMN, trackingConfiguration.getRoundingStrategy().name());
    }

    protected TrackingConfigurationJSON(JSONObject jsonObject) throws JSONException {
        put(UUID_COLUMN, jsonObject.getString(UUID_COLUMN));
        put(PROJECT_UUID_COLUMN, jsonObject.getString(PROJECT_UUID_COLUMN));
        put(HOUR_LIMIT_COLUMN, jsonObject.isNull(HOUR_LIMIT_COLUMN) ? null : jsonObject.getInt(HOUR_LIMIT_COLUMN));
        put(START_DATE_COLUMN, jsonObject.isNull(START_DATE_COLUMN) ? null : jsonObject.getString(START_DATE_COLUMN));
        put(END_DATE_COLUMN, jsonObject.isNull(END_DATE_COLUMN) ? null : jsonObject.getString(END_DATE_COLUMN));
        put(PROMPT_FOR_DESCRIPTION_COLUMN, jsonObject.isNull(PROMPT_FOR_DESCRIPTION_COLUMN) ? null : jsonObject.getBoolean(PROMPT_FOR_DESCRIPTION_COLUMN));
        put(ROUNDING_STRATEGY_COLUMN, jsonObject.getString(ROUNDING_STRATEGY_COLUMN));
    }

    protected TrackingConfiguration toTrackingConfiguration() throws JSONException, ParseException {
        // hier den konstruktor mit den optionals nutzen
        // und in der entity tests fuer diesen Konstruktor nachpflegen
        return new TrackingConfiguration(
                getString(UUID_COLUMN),
                getString(PROJECT_UUID_COLUMN),
                optInt(HOUR_LIMIT_COLUMN),
                isNull(START_DATE_COLUMN) ? null : Formatter.iso8601().parse(getString(START_DATE_COLUMN)),
                isNull(END_DATE_COLUMN) ? null : Formatter.iso8601().parse(getString(END_DATE_COLUMN)),
                getBoolean(PROJECT_UUID_COLUMN),
                RoundingFactory.Strategy.valueOf(getString(ROUNDING_STRATEGY_COLUMN))
        );
    }
}
