package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.util.Formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingRecordJSONTest {

    @Test
    public void can_marshal_project_uuid() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setProjectUuid("project-uuid");

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(record);

        // then
        assertEquals(trackingRecordJSON.getString(TrackingRecordJSON.PROJECT_UUID_COLUMN),
                "project-uuid");
    }

    @Test
    public void can_marshal_existing_description() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.of("desc"));

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(record);

        // then
        assertEquals(trackingRecordJSON.getString(TrackingRecordJSON.DESCRIPTION_COLUMN), "desc");
    }

    @Test
    public void will_omit_non_existing_description() throws Exception {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.<String>absent());

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(record);

        // then
        assertTrue(trackingRecordJSON.isNull(TrackingRecordJSON.DESCRIPTION_COLUMN));
    }

    @Test
    public void can_marshal_start_date() throws Exception {
        // given
        Date date = new Date();
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setStart(date);

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(trackingRecord);

        // then
        assertEquals(date, Formatter.iso8601().parse(trackingRecordJSON.getString(TrackingRecordJSON.START_DATE_COLUMN)));
    }

    @Test
    public void will_omit_non_existing_start_date() throws Exception {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(trackingRecord);

        // then
        assertTrue(trackingRecordJSON.isNull(TrackingRecordJSON.START_DATE_COLUMN));

        // and: make sure the initial date is actually null
        assertNull(trackingRecord.getStart().orNull());
    }

    @Test
    public void can_marshal_end_date() throws Exception {
        // given
        Date date = new Date();
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setEnd(date);

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(trackingRecord);

        // then
        assertEquals(date, Formatter.iso8601().parse(trackingRecordJSON.getString(TrackingRecordJSON.END_DATE_COLUMN)));
    }

    @Test
    public void will_omit_non_existing_end_date() throws Exception {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        TrackingRecordJSON trackingRecordJSON = new TrackingRecordJSON(trackingRecord);

        // then
        assertTrue(trackingRecordJSON.isNull(TrackingRecordJSON.END_DATE_COLUMN));

        // and: make sure the initial date is actually null
        assertNull(trackingRecord.getStart().orNull());
    }

}