package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

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
}
