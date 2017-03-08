package com.tastybug.timetracker.core.task.tracking.checkin;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;

public class CheckInEvent extends CreatedTrackingRecordEvent {

    CheckInEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}
