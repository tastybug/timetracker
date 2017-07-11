package com.tastybug.timetracker.extension.wifitracking.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class SessionsLog {

    private Context context;
    private SharedPreferences preferences;
    private SessionLogEntryFactory sessionLogEntryFactory;

    public SessionsLog(Context context) {
        this(context.getSharedPreferences("TRIGGER_LOG", MODE_PRIVATE), new SessionLogEntryFactory());
        this.context = context;
    }

    SessionsLog(SharedPreferences preferences, SessionLogEntryFactory factory) {
        this.preferences = preferences;
        this.sessionLogEntryFactory = factory;
    }

    public void addSession(String trackingRecordUuid, String ssid) {
        Entry log = sessionLogEntryFactory.create(trackingRecordUuid, ssid);
        preferences.edit()
                .putString(trackingRecordUuid, log.toJSON().toString())
                .apply();
    }

    public void updateSession(Entry entry) {
        preferences.edit()
                .putString(entry.getTrackingRecordUuid(), entry.toJSON().toString())
                .apply();
    }

    public List<Entry> getSessions() {
        List<Entry> results = new ArrayList<>();
        for (String trackingRecordUuid : preferences.getAll().keySet()) {
            results.add(sessionLogEntryFactory.createFromJson(preferences.getString(trackingRecordUuid, "")));
        }
        return results;
    }

    public Optional<Entry> getSession(String trackingRecordUuid) {
        if (preferences.contains(trackingRecordUuid)) {
            return Optional.of(sessionLogEntryFactory.createFromJson(preferences.getString(trackingRecordUuid,"")));
        } else {
            return Optional.absent();
        }
    }

    boolean deleteSession(String trackingRecordUuid) {
        if (!preferences.contains(trackingRecordUuid)) {
            return false;
        }
        logInfo(getClass().getSimpleName(), "Deleting wifi triggered session %s.", trackingRecordUuid);
        preferences.edit()
                .remove(trackingRecordUuid)
                .apply();
        return true;
    }

    void deleteInvalidSessions() {
        TrackingRecordDAO trackingRecordDAO = new TrackingRecordDAO(context);
        for (Entry entry : getSessions()) {
            if (!trackingRecordDAO.get(entry.getTrackingRecordUuid()).isPresent()) {
                deleteSession(entry.getTrackingRecordUuid());
                logInfo(getClass().getSimpleName(), "Removed session pointing to unknown TrackingRecord.");
            }
        }
    }

    public void deleteAllSessions() {
        for (Entry entry : getSessions()) {
            deleteSession(entry.getTrackingRecordUuid());
        }
    }

    static class SessionLogEntryFactory {
        Entry create(String trackingRecordUuid, String ssid) {
            return new Entry(ssid, trackingRecordUuid, new Date());
        }
        Entry createFromJson(String json) {
            try {
                return new Entry(new JSONObject(json));
            } catch (JSONException e) {
                throw new RuntimeException("Failed to find open sessions.", e);
            }
        }
    }

    public static class Entry {

        private static final String SSID_KEY = "SSID";
        private static final String TRACKING_RECORD_UUID_KEY = "TRACKING_RECORD_UUID_KEY";
        private static final String START_DATE_KEY = "START_DATE_KEY";
        private static final String END_DATE_KEY = "END_DATE_KEY";

        private String ssid, trackingRecordUuid;
        private Date startDate, endDate;

        Entry(String ssid, String trackingRecordUuid, Date startDate) {
            this.ssid = ssid;
            this.trackingRecordUuid = trackingRecordUuid;
            this.startDate = startDate;
        }

        Entry(JSONObject jsonObject) {
            try {
                this.ssid = jsonObject.getString(SSID_KEY);
                this.trackingRecordUuid = jsonObject.getString(TRACKING_RECORD_UUID_KEY);
                this.startDate = DefaultLocaleDateFormatter.iso8601().parse(jsonObject.getString(START_DATE_KEY));
                if (jsonObject.has(END_DATE_KEY)) {
                    this.endDate = DefaultLocaleDateFormatter.iso8601().parse(jsonObject.getString(END_DATE_KEY));
                }
            } catch (JSONException | ParseException e) {
                throw new RuntimeException("Failed to read json: " + jsonObject);
            }
        }

        public String getSsid() {
            return ssid;
        }

        public String getTrackingRecordUuid() {
            return trackingRecordUuid;
        }

        Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        JSONObject toJSON() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(TRACKING_RECORD_UUID_KEY, trackingRecordUuid);
                jsonObject.put(SSID_KEY, ssid);
                jsonObject.put(START_DATE_KEY, DefaultLocaleDateFormatter.iso8601().format(startDate));
                if (endDate != null) {
                    jsonObject.put(END_DATE_KEY, DefaultLocaleDateFormatter.iso8601().format(endDate));
                }
                return jsonObject;
            } catch (JSONException jsone) {
                throw new RuntimeException("Failed to create trigger log entry JSON.", jsone);
            }
        }

        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("getTrackingRecordUuid", getTrackingRecordUuid())
                    .add("ssid", getSsid())
                    .add("startDate", getStartDate())
                    .add("endDate", getEndDate())
                    .toString();
        }
    }
}
