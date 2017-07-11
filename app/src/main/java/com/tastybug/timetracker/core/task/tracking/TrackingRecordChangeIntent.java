package com.tastybug.timetracker.core.task.tracking;

import android.content.Intent;

import com.google.common.base.MoreObjects;

import static com.tastybug.timetracker.core.task.LifecycleEvent.LIFECYCLE_EVENT_CATEGORY;

public class TrackingRecordChangeIntent extends Intent {

    public static final String ACTION = "com.tastybug.timetracker.TRACKING_RECORD_CHANGE";
    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    private static final String TYPE = "TYPE";
    private static final String WAS_STOPPED = "WAS_STOPPED";

    public enum Type {
        CREATE, DELETE, UPDATE, CHECKIN, CHECKOUT
    }

    public TrackingRecordChangeIntent(Intent intent) {
        super(intent);
    }

    public TrackingRecordChangeIntent(String projectUuid, String trackingRecordUuid, Type type, boolean wasStopped) {
        setAction(ACTION);
        putExtra(PROJECT_UUID, projectUuid);
        putExtra(TRACKING_RECORD_UUID, trackingRecordUuid);
        putExtra(TYPE, type.name());
        putExtra(WAS_STOPPED, wasStopped);
        addCategory(LIFECYCLE_EVENT_CATEGORY);
    }

    public String getTrackingRecordUuid() {
        return getStringExtra(TRACKING_RECORD_UUID);
    }

    public String getProjectUuid() {
        return getStringExtra(PROJECT_UUID);
    }

    public Type getChangeType() {
        return Type.valueOf(getStringExtra(TYPE));
    }

    public boolean wasStopped() {
        return getBooleanExtra(WAS_STOPPED, false);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .add("trackingRecordUuid", getTrackingRecordUuid())
                .add("changeType", getChangeType())
                .add("wasStopped", wasStopped())
                .toString();
    }
}
