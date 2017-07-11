package com.tastybug.timetracker.extension.wifitracking.controller;

import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class SessionsLogTest {

    private SharedPreferences preferences = mock(SharedPreferences.class);
    private SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
    private SessionsLog.SessionLogEntryFactory factory = mock(SessionsLog.SessionLogEntryFactory.class);


    private SessionsLog sessionsLog = new SessionsLog(preferences, factory);

    @Before
    public void setup() {
        when(preferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
    }

    @Test
    public void addSession_stores_a_trigger_as_json() {
        // given
        SessionsLog.Entry entry = mock(SessionsLog.Entry.class);
        JSONObject jsonObject = mock(JSONObject.class);
        when(jsonObject.toString()).thenReturn("jaaaason");
        when(entry.toJSON()).thenReturn(jsonObject);
        when(factory.create(anyString(), anyString())).thenReturn(entry);

        // when
        sessionsLog.addSession("some uuid", "some uuid");

        // then
        verify(editor).putString("some uuid", "jaaaason");
        verify(editor).apply();
    }

    @Test
    public void getSessions_returns_all_sessions_found() {
        // given
        Map map = new HashMap<>();
        map.put("1", "");
        map.put("2", "");
        when(preferences.getAll()).thenReturn(map);
        when(preferences.getString(eq("1"), anyString())).thenReturn("json1");
        when(preferences.getString(eq("2"), anyString())).thenReturn("json2");
        SessionsLog.Entry result1 = mock(SessionsLog.Entry.class);
        when(factory.createFromJson("json1")).thenReturn(result1);
        SessionsLog.Entry result2 = mock(SessionsLog.Entry.class);
        when(factory.createFromJson("json2")).thenReturn(result2);

        // when
        List<SessionsLog.Entry> sessionLogEntries = sessionsLog.getSessions();

        // then
        assertEquals(2, sessionLogEntries.size());
        assertTrue(sessionLogEntries.contains(result1));
        assertTrue(sessionLogEntries.contains(result2));
    }
}