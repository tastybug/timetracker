package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataOutput;
import android.content.Context;

import com.tastybug.timetracker.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

class DataMarshaller {

    private static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JsonMarshallingBuilder jsonMarshallingBuilder;

    DataMarshaller(Context context) {
        this.jsonMarshallingBuilder = new JsonMarshallingBuilder(context);
    }

    DataMarshaller(JsonMarshallingBuilder marshallingBuilder) {
        this.jsonMarshallingBuilder = marshallingBuilder;
    }

    void writeBackup(BackupDataOutput data) throws IOException, JSONException {
        JSONArray jsonArray = jsonMarshallingBuilder.build();
        byte[] payload = jsonArray.toString().getBytes("utf-8");
        data.writeEntityHeader(JSON_ARRAY_KEY, payload.length);
        data.writeEntityData(payload, payload.length);
    }
}
