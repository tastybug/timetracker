package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackingConfigurationMarshalling implements JsonMarshaller {

    static String UUID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String HOUR_LIMIT_COLUMN = "hour_limit";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String PROMPT_FOR_DESCRIPTION_COLUMN = "prompt_for_description";
    static String ROUNDING_STRATEGY_COLUMN = "rounding_strategy";

    private TrackingConfigurationDAO trackingConfigurationDAO;

    public TrackingConfigurationMarshalling(Context context) {
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    public TrackingConfigurationMarshalling(TrackingConfigurationDAO trackingConfigurationDAO) {
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    protected JSONObject getAsJson(TrackingConfiguration trackingConfiguration) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(UUID_COLUMN, trackingConfiguration.getUuid());
        json.put(PROJECT_UUID_COLUMN, trackingConfiguration.getProjectUuid());
        if (trackingConfiguration.getHourLimit().isPresent()) {
            json.put(HOUR_LIMIT_COLUMN, trackingConfiguration.getHourLimit().get());
        }
        if (trackingConfiguration.getStart().isPresent()) {
            json.put(START_DATE_COLUMN, Formatter.iso8601().format(trackingConfiguration.getStart().get()));
        }
        if (trackingConfiguration.getEnd().isPresent()) {
            json.put(END_DATE_COLUMN, Formatter.iso8601().format(trackingConfiguration.getEnd().get()));
        }
        json.put(PROMPT_FOR_DESCRIPTION_COLUMN, trackingConfiguration.isPromptForDescription());
        json.put(ROUNDING_STRATEGY_COLUMN, trackingConfiguration.getRoundingStrategy().name());

        return json;
    }

    @Override
    public List<JSONObject> generateJSON() throws JSONException {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        for (TrackingConfiguration tc : trackingConfigurationDAO.getAll()) {
            jsonObjectArrayList.add(getAsJson(tc));
        }
        return jsonObjectArrayList;
    }

    @Override
    public List<JSONObject> generateJSON(String projectUuid) throws JSONException {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        jsonObjectArrayList.add(getAsJson(trackingConfigurationDAO.get(projectUuid).get()));
        return jsonObjectArrayList;
    }
}
