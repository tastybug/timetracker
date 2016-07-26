package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataOutput;
import android.content.Context;

import com.tastybug.timetracker.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class BackupDataWriter {

    protected static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JsonMarshallingBuilder jsonMarshallingBuilder;

    public BackupDataWriter(Context context) {
        this.jsonMarshallingBuilder = new JsonMarshallingBuilder(context);
    }

    public BackupDataWriter(JsonMarshallingBuilder marshallingBuilder) {
        this.jsonMarshallingBuilder = marshallingBuilder;
    }

    public void writeBackup(BackupDataOutput data) throws IOException, JSONException {
        JSONArray jsonArray = jsonMarshallingBuilder.build();
        byte[] payload = jsonArray.toString().getBytes("utf-8");
        data.writeEntityHeader(JSON_ARRAY_KEY, payload.length);
        data.writeEntityData(payload, payload.length);
    }
}
