package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackingRecordMarshalling {

    static String ID_COLUMN = "uuid";
    static String PROJECT_UUID_COLUMN = "project_uuid";
    static String START_DATE_COLUMN = "start_date";
    static String END_DATE_COLUMN = "end_date";
    static String DESCRIPTION_COLUMN = "description";

    private TrackingRecordDAO trackingRecordDAO;

    public TrackingRecordMarshalling(Context context) {
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public TrackingRecordMarshalling(TrackingRecordDAO trackingRecordDAO) {
        this.trackingRecordDAO = trackingRecordDAO;
    }

    protected JSONObject getAsJson(TrackingRecord trackingRecord) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ID_COLUMN, trackingRecord.getUuid());
        json.put(PROJECT_UUID_COLUMN, trackingRecord.getProjectUuid());
        if (trackingRecord.getStart().isPresent()) {
            json.put(START_DATE_COLUMN, Formatter.iso8601().format(trackingRecord.getStart().get()));
        }
        if (trackingRecord.getEnd().isPresent()) {
            json.put(END_DATE_COLUMN, Formatter.iso8601().format(trackingRecord.getEnd().get()));
        }
        if (trackingRecord.getDescription().isPresent()) {
            json.put(DESCRIPTION_COLUMN, trackingRecord.getDescription().get());
        }

        return json;
    }

    protected List<JSONObject> getAsJsonByProjectUuid(String projectUuid) throws JSONException {
        ArrayList<JSONObject> jsons = new ArrayList<>();
        for (TrackingRecord trackingRecord : trackingRecordDAO.getByProjectUuid(projectUuid)) {
            jsons.add(getAsJson(trackingRecord));
        }
        return jsons;
    }
}
