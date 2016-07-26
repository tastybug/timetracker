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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BackupDataWriterTest {

    protected static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    JsonMarshallingBuilder marshaller = mock(JsonMarshallingBuilder.class);

    JSONArray jsonArray = mock(JSONArray.class);
    String jsonAsString = "{}";
    byte[] bytesPayload;
    BackupDataInput data = mock(BackupDataInput.class);

    BackupDataWriter subject = new BackupDataWriter(marshaller);

    @Before
    public void setup() throws Exception {
        bytesPayload = jsonAsString.getBytes("utf-8");

        when(marshaller.build()).thenReturn(jsonArray);
        when(jsonArray.toString()).thenReturn(jsonAsString);

        when(data.readNextHeader()).thenReturn(true, false);
        when(data.getKey()).thenReturn(JSON_ARRAY_KEY);
    }

    @Test
    public void writeBackup_calls_marshaller_for_a_full_data_export() throws JSONException, IOException {
        // when
        subject.writeBackup(mock(BackupDataOutput.class));

        // then
        verify(marshaller, times(1)).build();
        verify(marshaller, never()).withProjectUuid(anyString());
    }

    @Test
    public void writeBackup_writes_payload_with_correct_length_using_correct_key() throws JSONException, IOException {
        // given
        BackupDataOutput output = mock(BackupDataOutput.class);

        // when
        subject.writeBackup(output);

        // then
        verify(output, times(1)).writeEntityHeader(JSON_ARRAY_KEY, bytesPayload.length);
        verify(output, times(1)).writeEntityData(bytesPayload, bytesPayload.length);
    }

}