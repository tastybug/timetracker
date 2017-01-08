package com.tastybug.timetracker.task.dataexport;

import android.content.Context;

import com.tastybug.timetracker.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

class DataExportCreator {

    private JsonMarshallingBuilder jsonMarshallingBuilder;

    DataExportCreator(Context context) {
        this.jsonMarshallingBuilder = new JsonMarshallingBuilder(context);
    }

    DataExportCreator(JsonMarshallingBuilder jsonMarshallingBuilder) {
        this.jsonMarshallingBuilder = jsonMarshallingBuilder;
    }

    byte[] getDataAsByteArray() throws JSONException, UnsupportedEncodingException {
        JSONArray jsonArray = jsonMarshallingBuilder.build();
        return jsonArray.toString().getBytes("utf-8");
    }
}
