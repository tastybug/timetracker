package com.tastybug.timetracker.extension.backup.controller.localbackup;

import android.content.Context;

import com.tastybug.timetracker.core.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

class BackupDataCreator {

    private JsonMarshallingBuilder jsonMarshallingBuilder;

    BackupDataCreator(Context context) {
        this(new JsonMarshallingBuilder(context));
    }

    BackupDataCreator(JsonMarshallingBuilder jsonMarshallingBuilder) {
        this.jsonMarshallingBuilder = jsonMarshallingBuilder;
    }

    byte[] getDataAsByteArray() throws JSONException, UnsupportedEncodingException {
        JSONArray jsonArray = jsonMarshallingBuilder.build();
        return jsonArray.toString().getBytes("utf-8");
    }
}
