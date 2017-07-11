package com.tastybug.timetracker.core.task.tracking.delete;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;

import static com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent.Type.DELETE;

public class DeletedTrackingRecordEvent implements LifecycleEvent {

    private String projectUuid, trackingRecordUuid;

    DeletedTrackingRecordEvent(String projectUuid, String trackingRecordUuid) {
        this.projectUuid = projectUuid;
        this.trackingRecordUuid = trackingRecordUuid;
    }

    public String getTrackingRecordUuid() {
        return trackingRecordUuid;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new TrackingRecordChangeIntent(projectUuid, trackingRecordUuid, DELETE, false);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecordUuid", getTrackingRecordUuid())
                .toString();
    }
}