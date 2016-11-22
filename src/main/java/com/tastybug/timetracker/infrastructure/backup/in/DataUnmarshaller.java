package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class DataUnmarshaller {

    private static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JSONUnMarshallingBuilder jsonUnMarshallingBuilder;
    private DataEntryUnmarshaller dataEntryUnmarshaller = new DataEntryUnmarshaller();

    DataUnmarshaller() {
        this.jsonUnMarshallingBuilder = new JSONUnMarshallingBuilder();
    }

    DataUnmarshaller(JSONUnMarshallingBuilder unMarshallingBuilder,
                     DataEntryUnmarshaller dataEntryUnmarshaller) {
        this.jsonUnMarshallingBuilder = unMarshallingBuilder;
        this.dataEntryUnmarshaller = dataEntryUnmarshaller;
    }

    List<Project> unmarshallBackupData(BackupDataInput data) throws IOException, JSONException, ParseException {
        while (data.readNextHeader()) {
            if (data.getKey().equals(JSON_ARRAY_KEY)) {
                byte[] payload = dataEntryUnmarshaller.getByteArrayFromBackupDataInput(data);
                return getProjectsFromPayload(payload);
            } else {
                data.skipEntityData();
            }
        }
        return new ArrayList<>();
    }

    private List<Project> getProjectsFromPayload(byte[] payload) throws JSONException, IOException, ParseException {
        return jsonUnMarshallingBuilder.withByteArray(payload).build();
    }
}
