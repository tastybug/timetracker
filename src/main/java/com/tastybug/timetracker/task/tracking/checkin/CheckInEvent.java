package com.tastybug.timetracker.task.tracking.checkin;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.create.CreatedTrackingRecordEvent;

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
