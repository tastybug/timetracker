package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static com.tastybug.timetracker.model.json.TrackingRecordJSON.DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingRecordJSON.END_DATE_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingRecordJSON.ID_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingRecordJSON.PROJECT_UUID_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingRecordJSON.START_DATE_COLUMN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        trackingRecord.setStart(Optional.of(date));

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
        trackingRecord.setEnd(Optional.of(date));

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

    @Test
    public void can_import_a_json() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();

        // when
        TrackingRecordJSON subject = new TrackingRecordJSON(toImportFrom);

        // then
        assertEquals(toImportFrom.get(ID_COLUMN), subject.get(ID_COLUMN));
        assertEquals(toImportFrom.get(PROJECT_UUID_COLUMN), subject.get(PROJECT_UUID_COLUMN));
        assertEquals(toImportFrom.getString(START_DATE_COLUMN), subject.getString(START_DATE_COLUMN));
        assertEquals(toImportFrom.getString(END_DATE_COLUMN), subject.getString(END_DATE_COLUMN));
        assertEquals(toImportFrom.getString(DESCRIPTION_COLUMN), subject.getString(DESCRIPTION_COLUMN));
    }

    @Test
    public void can_import_a_json_without_start_date() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(START_DATE_COLUMN, null);

        // when
        TrackingRecordJSON subject = new TrackingRecordJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(START_DATE_COLUMN));
    }

    @Test
    public void can_import_a_json_without_end_date() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(END_DATE_COLUMN, null);

        // when
        TrackingRecordJSON subject = new TrackingRecordJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(END_DATE_COLUMN));
    }

    @Test
    public void can_import_a_json_without_description() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(DESCRIPTION_COLUMN, null);

        // when
        TrackingRecordJSON subject = new TrackingRecordJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(DESCRIPTION_COLUMN));
    }

    @Test
    public void to_tracking_record_contains_all_attributes_if_set() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();

        // when
        TrackingRecord trackingRecord = new TrackingRecordJSON(toImportFrom).toTrackingRecord();

        // then
        assertEquals(toImportFrom.get(ID_COLUMN), trackingRecord.getUuid());
        assertEquals(toImportFrom.get(PROJECT_UUID_COLUMN), trackingRecord.getProjectUuid());
        assertEquals(toImportFrom.getString(START_DATE_COLUMN), Formatter.iso8601().format(trackingRecord.getStart().get()));
        assertEquals(toImportFrom.getString(END_DATE_COLUMN), Formatter.iso8601().format(trackingRecord.getEnd().get()));
        assertEquals(toImportFrom.getString(DESCRIPTION_COLUMN), trackingRecord.getDescription().get());
    }

    @Test
    public void to_tracking_record_can_deal_with_missing_start_dates() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(START_DATE_COLUMN, null);

        // when
        TrackingRecord trackingRecord = new TrackingRecordJSON(toImportFrom).toTrackingRecord();

        // then
        assertFalse(trackingRecord.getStart().isPresent());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_missing_end_dates() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(END_DATE_COLUMN, null);

        // when
        TrackingRecord trackingRecord = new TrackingRecordJSON(toImportFrom).toTrackingRecord();

        // then
        assertFalse(trackingRecord.getEnd().isPresent());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_missing_descriptions() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingRecordJSONToImport();
        toImportFrom.put(DESCRIPTION_COLUMN, null);

        // when
        TrackingRecord trackingRecord = new TrackingRecordJSON(toImportFrom).toTrackingRecord();

        // then
        assertFalse(trackingRecord.getDescription().isPresent());
    }

    private JSONObject aTrackingRecordJSONToImport() throws JSONException {
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setUuid("uuid");
        trackingRecord.setProjectUuid("projectuuid");
        trackingRecord.setStart(Optional.of(new Date()));
        trackingRecord.setEnd(Optional.of(new Date()));
        trackingRecord.setDescription(Optional.of("desc"));


        return new TrackingRecordJSON(trackingRecord);
    }
}