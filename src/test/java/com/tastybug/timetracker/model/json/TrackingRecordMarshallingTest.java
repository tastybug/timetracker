package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingRecordMarshallingTest {

    TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);

    TrackingRecordMarshalling subject = new TrackingRecordMarshalling(trackingRecordDAO);

    @Test
    public void getAsJson_can_marshal_a_project_uuid() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setProjectUuid("project-uuid");

        // when
        JSONObject json = subject.getAsJson(record);

        // then
        assertEquals(json.getString(TrackingRecordMarshalling.PROJECT_UUID_COLUMN), "project-uuid");
    }

    @Test
    public void getAsJson_can_marshal_a_description() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.of("desc"));

        // when
        JSONObject json = subject.getAsJson(record);

        // then
        assertEquals(json.getString(TrackingRecordMarshalling.DESCRIPTION_COLUMN), "desc");
    }

    @Test
    public void getAsJson_will_omit_non_existing_description() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.<String>absent());

        // when
        JSONObject json = subject.getAsJson(record);

        // then
        assertTrue(json.isNull(TrackingRecordMarshalling.DESCRIPTION_COLUMN));
    }

    @Test
    public void getAsJson_can_marshal_a_start_date() throws Exception {
        // given
        Date date = new Date();
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setStart(date);

        // when
        JSONObject json = subject.getAsJson(trackingRecord);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingRecordMarshalling.START_DATE_COLUMN)));
    }

    @Test
    public void getAsJson_will_omit_non_existing_start_date() throws Exception {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        JSONObject json = subject.getAsJson(trackingRecord);

        // then
        assertTrue(json.isNull(TrackingRecordMarshalling.START_DATE_COLUMN));

        // and: make sure the initial date is actually null
        assertNull(trackingRecord.getStart().orNull());
    }

    @Test
    public void getAsJson_can_marshal_a_end_date() throws Exception {
        // given
        Date date = new Date();
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setEnd(date);

        // when
        JSONObject json = subject.getAsJson(trackingRecord);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingRecordMarshalling.END_DATE_COLUMN)));
    }

    @Test
    public void getAsJson_will_omit_non_existing_end_date() throws Exception {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        JSONObject json = subject.getAsJson(trackingRecord);

        // then
        assertTrue(json.isNull(TrackingRecordMarshalling.END_DATE_COLUMN));

        // and: make sure the initial date is actually null
        assertNull(trackingRecord.getStart().orNull());
    }

}