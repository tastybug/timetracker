package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.json.JSONUnMarshallingBuilder;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataUnmarshallerTest {

    private static final String JSON_ARRAY_KEY = "JSON_ARRAY_KEY";

    private JSONUnMarshallingBuilder jsonUnmarshaller = mock(JSONUnMarshallingBuilder.class);
    private DataEntryUnmarshaller entryUnmarshaller = mock(DataEntryUnmarshaller.class);

    private JSONArray jsonArray = mock(JSONArray.class);
    private BackupDataInput data = mock(BackupDataInput.class);

    private DataUnmarshaller subject = new DataUnmarshaller(jsonUnmarshaller, entryUnmarshaller);

    @Before
    public void setup() throws Exception {
        String jsonAsString = "{}";

        when(jsonUnmarshaller.withProjectArray((JSONArray) any())).thenReturn(jsonUnmarshaller);
        when(jsonUnmarshaller.withByteArray((byte[]) any())).thenReturn(jsonUnmarshaller);
        when(jsonUnmarshaller.build()).thenReturn(new ArrayList<Project>());
        when(jsonArray.toString()).thenReturn(jsonAsString);

        when(data.readNextHeader()).thenReturn(true, false);
        when(data.getKey()).thenReturn(JSON_ARRAY_KEY);

        when(entryUnmarshaller.getByteArrayFromBackupDataInput(data)).thenReturn("{}".getBytes());
    }

    @Test
    public void unmarshallBackupData_will_iterate_through_all_data_skipping_everything_unknown() throws Exception {
        // given
        BackupDataInput data = mock(BackupDataInput.class);
        when(data.readNextHeader()).thenReturn(true, true, false);
        when(data.getKey()).thenReturn("WRONG-KEY");

        // when
        subject.unmarshallBackupData(data);

        // then
        verify(data, times(3)).readNextHeader();
        verify(data, times(2)).skipEntityData();
    }

    @Test
    public void unmarshallBackupData_will_return_no_projects_when_backup_is_empty() throws Exception {
        when(data.readNextHeader()).thenReturn(false);

        // when
        List<Project> projects = subject.unmarshallBackupData(data);

        // then
        assertTrue(projects.isEmpty());
    }

    @Test
    public void unmarshallBackupData_returns_projects_from_backup_data() throws Exception {
        // given
        byte[] payload = "{}".getBytes();
        when(entryUnmarshaller.getByteArrayFromBackupDataInput(data)).thenReturn(payload);
        when(jsonUnmarshaller.build()).thenReturn(Collections.singletonList(new Project("")));

        // when
        List<Project> projects = subject.unmarshallBackupData(data);

        // then
        assertEquals(1, projects.size());
    }
}