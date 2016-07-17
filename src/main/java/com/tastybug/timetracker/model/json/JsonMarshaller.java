package com.tastybug.timetracker.model.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface JsonMarshaller {

    List<JSONObject> generateJSON() throws JSONException;

    List<JSONObject> generateJSON(String projectUuid) throws JSONException;
}
