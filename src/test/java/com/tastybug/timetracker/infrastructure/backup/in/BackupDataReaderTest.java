package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BackupDataReaderTest {

    protected static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    JSONUnMarshallingBuilder unmarshaller = mock(JSONUnMarshallingBuilder.class);
    BackupDataEntryReader payloadReader = mock(BackupDataEntryReader.class);

    JSONArray jsonArray = mock(JSONArray.class);
    String jsonAsString = "{}";
    byte[] bytesPayload;
    BackupDataInput data = mock(BackupDataInput.class);

    BackupDataReader subject = new BackupDataReader(unmarshaller, payloadReader);

    @Before
    public void setup() throws Exception {
        bytesPayload = jsonAsString.getBytes("utf-8");

        when(unmarshaller.withProjectArray((JSONArray) any())).thenReturn(unmarshaller);
        when(unmarshaller.withByteArray((byte[]) any())).thenReturn(unmarshaller);
        when(unmarshaller.build()).thenReturn(new ArrayList<Project>());
        when(jsonArray.toString()).thenReturn(jsonAsString);

        when(data.readNextHeader()).thenReturn(true, false);
        when(data.getKey()).thenReturn(JSON_ARRAY_KEY);

        when(payloadReader.getPayloadFromBackupData(data)).thenReturn("{}".getBytes());
    }

    @Test
    public void readBackup_will_iterate_through_all_data_skipping_everything_unknown() throws Exception {
        // given
        BackupDataInput data = mock(BackupDataInput.class);
        when(data.readNextHeader()).thenReturn(true, true, false);
        when(data.getKey()).thenReturn("WRONG-KEY");

        // when
        subject.readBackup(data);

        // then
        verify(data, times(3)).readNextHeader();
        verify(data, times(2)).skipEntityData();
    }

    @Test
    public void readBackup_will_return_no_projects_when_backup_is_empty() throws Exception {
        when(data.readNextHeader()).thenReturn(false);

        // when
        List<Project> projects = subject.readBackup(data);

        // then
        assertTrue(projects.isEmpty());
    }

    @Test
    public void readBackup_returns_projects_from_backup_data() throws Exception {
        // given
        byte[] payload = "{}".getBytes();
        when(payloadReader.getPayloadFromBackupData(data)).thenReturn(payload);
        when(unmarshaller.build()).thenReturn(Arrays.asList(new Project("")));

        // when
        List<Project> projects = subject.readBackup(data);

        // then
        assertEquals(1, projects.size());
    }
}