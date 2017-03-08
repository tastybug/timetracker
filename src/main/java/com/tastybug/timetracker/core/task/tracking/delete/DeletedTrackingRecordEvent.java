package com.tastybug.timetracker.core.task.tracking.delete;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class DeletedTrackingRecordEvent implements OttoEvent {

    private String trackingRecordUuid;

    DeletedTrackingRecordEvent(String uuid) {
        this.trackingRecordUuid = uuid;
    }

    public String getTrackingRecordUuid() {
        return trackingRecordUuid;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecordUuid", getTrackingRecordUuid())
                .toString();
    }
}