package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BackupDataReader {

    protected static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JSONUnMarshallingBuilder jsonUnMarshallingBuilder;
    private BackupDataEntryReader payloadReader = new BackupDataEntryReader();

    public BackupDataReader() {
        this.jsonUnMarshallingBuilder = new JSONUnMarshallingBuilder();
    }

    public BackupDataReader(JSONUnMarshallingBuilder unMarshallingBuilder,
                            BackupDataEntryReader payloadReader) {
        this.jsonUnMarshallingBuilder = unMarshallingBuilder;
        this.payloadReader = payloadReader;
    }

    public List<Project> readBackup(BackupDataInput data) throws IOException, JSONException, ParseException {
        while (data.readNextHeader()) {
            if (data.getKey().equals(JSON_ARRAY_KEY)) {
                byte[] payload = payloadReader.getPayloadFromBackupData(data);
                return getProjectsFromPayload(payload);
            } else {
                data.skipEntityData();
            }
        }
        return new ArrayList<>();
    }

    // TODO das hier in die Serviceklasse hochziehen. Diese klasse sollte sich nur um den datazugriff kuemmern
    private List<Project> getProjectsFromPayload(byte[] payload) throws JSONException, IOException, ParseException {
        return jsonUnMarshallingBuilder.withByteArray(payload).build();
    }
}
