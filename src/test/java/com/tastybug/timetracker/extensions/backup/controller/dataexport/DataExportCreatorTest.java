package com.tastybug.timetracker.extensions.backup.controller.dataexport;

import android.os.Build;

import com.tastybug.timetracker.model.json.JsonMarshallingBuilder;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DataExportCreatorTest {

    private JsonMarshallingBuilder jsonMarshallingBuilder = mock(JsonMarshallingBuilder.class);
    private DataExportCreator subject = new DataExportCreator(jsonMarshallingBuilder);

    @Test
    public void getDataAsByteArray_returns_utf8_byte_array_of_json_dump() throws Exception {
        // given
        JSONArray jsonDataDump = new JSONArray();
        when(jsonMarshallingBuilder.build()).thenReturn(jsonDataDump);

        // when
        byte[] data = subject.getDataAsByteArray();

        // then
        assertArrayEquals(jsonDataDump.toString().getBytes("utf-8"), data);
    }
}