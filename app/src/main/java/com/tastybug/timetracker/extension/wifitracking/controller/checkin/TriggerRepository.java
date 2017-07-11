package com.tastybug.timetracker.extension.wifitracking.controller.checkin;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class TriggerRepository {

    private static final String PREFERENCES_NAME = "WIFI_TRACKING_PREFERENCES";

    private SharedPreferences preferences;

    public TriggerRepository(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    public void addOrReplace(String projectUuid, String ssidName) {
        Trigger trigger = new Trigger(projectUuid, ssidName);
        logInfo(getClass().getSimpleName(), "Storing %s.", trigger.toString());
        preferences.edit()
                .putString(trigger.getProjectUuid(), trigger.getSsid())
                .apply();
    }

    List<String> getProjectUuidsBoundToSsid(String ssid) {
        List<String> projectUuids = new ArrayList<>();
        for (Trigger trigger : getAll()) {
            if (trigger.getSsid().equals(ssid)) {
                projectUuids.add(trigger.getProjectUuid());
            }
        }
        return projectUuids;
    }

    public void deleteByProjectUuid(String projectUuid) {
        logInfo(getClass().getSimpleName(), "Deleting Wifi-Trigger for project %s.", projectUuid);
        preferences.edit().remove(projectUuid).apply();
    }

    public void deleteAll() {
        for (String projectUuid : preferences.getAll().keySet()) {
            deleteByProjectUuid(projectUuid);
        }
    }

    private List<Trigger> getAll() {
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        for (String projectUuid : preferences.getAll().keySet()) {
            triggerArrayList.add(new Trigger(projectUuid, preferences.getString(projectUuid, "")));
        }
        return triggerArrayList;
    }

    public Optional<Trigger> getByProjectUuid(String projectUuid) {
        if (preferences.contains(projectUuid)) {
            return Optional.of(new Trigger(projectUuid, preferences.getString(projectUuid, "")));
        } else {
            return Optional.absent();
        }
    }

    public static class Trigger {

        private String projectUuid, ssid;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        Trigger(String projectUuid, String ssid) {
            Preconditions.checkNotNull(projectUuid);
            Preconditions.checkNotNull(ssid);
            this.projectUuid = projectUuid;
            this.ssid = ssid;
        }

        public String getProjectUuid() {
            return projectUuid;
        }

        public String getSsid() {
            return ssid;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("projectUuid", getProjectUuid())
                    .add("ssid", getSsid())
                    .toString();
        }
    }
}
