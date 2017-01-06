package com.tastybug.timetracker.infrastructure.backup.out;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;

import com.tastybug.timetracker.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataMarshallerTest {

    private static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JsonMarshallingBuilder jsonMarshaller = mock(JsonMarshallingBuilder.class);

    private JSONArray jsonArray = mock(JSONArray.class);
    private byte[] bytesPayload;
    private BackupDataInput data = mock(BackupDataInput.class);

    private DataMarshaller subject = new DataMarshaller(jsonMarshaller);

    @Before
    public void setup() throws Exception {
        String jsonAsString = "{}";
        bytesPayload = jsonAsString.getBytes("utf-8");

        when(jsonMarshaller.build()).thenReturn(jsonArray);
        when(jsonArray.toString()).thenReturn(jsonAsString);

        when(data.readNextHeader()).thenReturn(true, false);
        when(data.getKey()).thenReturn(JSON_ARRAY_KEY);
    }

    @Test
    public void writeBackup_calls_marshaller_for_a_full_data_export() throws JSONException, IOException {
        // when
        subject.writeBackup(mock(BackupDataOutput.class));

        // then
        verify(jsonMarshaller).build();
        verify(jsonMarshaller, never()).withProjectUuid(anyString());
    }

    @Test
    public void writeBackup_writes_payload_with_correct_length_using_correct_key() throws JSONException, IOException {
        // given
        BackupDataOutput output = mock(BackupDataOutput.class);

        // when
        subject.writeBackup(output);

        // then
        verify(output).writeEntityHeader(JSON_ARRAY_KEY, bytesPayload.length);
        verify(output).writeEntityData(bytesPayload, bytesPayload.length);
    }

}