package com.tastybug.timetracker.extensions.checkoutreminder.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.model.TrackingRecord;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

class ReminderRepository {

    private static final String KEY = "CHECKOUT_REMINDER_KEYS";
    private static final String REPOSITORY_NAME = ReminderRepository.class.getName();
    private Context context;

    ReminderRepository(Context context) {
        this.context = context;
    }

    boolean hasTrackingRecord(TrackingRecord trackingRecord) {
        return getKnownTrackingUuids().contains(trackingRecord.getUuid());
    }

    @NonNull
    private Set<String> getKnownTrackingUuids() {
        return getSharedPreferences().getStringSet(KEY, new HashSet<String>());
    }

    void addTrackingRecord(TrackingRecord trackingRecord) {
        Set<String> knownUuids = getKnownTrackingUuids();
        knownUuids.add(trackingRecord.getUuid());
        getSharedPreferences().edit().putStringSet(KEY, knownUuids).apply();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(REPOSITORY_NAME, MODE_PRIVATE);
    }

}
