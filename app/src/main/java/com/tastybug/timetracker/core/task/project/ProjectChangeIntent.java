package com.tastybug.timetracker.core.task.project;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;

import static com.tastybug.timetracker.core.task.LifecycleEvent.LIFECYCLE_EVENT_CATEGORY;

public class ProjectChangeIntent extends Intent {

    public static final String ACTION = "com.tastybug.timetracker.PROJECT_CHANGE";
    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String TYPE = "TYPE";

    public enum Type {
        CREATE, DELETE, UPDATE
    }

    public ProjectChangeIntent(Intent intent) {
        super(intent);
    }

    public ProjectChangeIntent(String projectUuid, Type type) {
        setAction(ACTION);
        putExtra(PROJECT_UUID, projectUuid);
        putExtra(TYPE, type.name());
        addCategory(LIFECYCLE_EVENT_CATEGORY);
    }

    public TrackingRecordChangeIntent.Type getChangeType() {
        return TrackingRecordChangeIntent.Type.valueOf(getStringExtra(TYPE));
    }


    public String getProjectUuid() {
        return getStringExtra(PROJECT_UUID);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projectUuid", getProjectUuid())
                .add("changeType", getChangeType())
                .toString();
    }
}
