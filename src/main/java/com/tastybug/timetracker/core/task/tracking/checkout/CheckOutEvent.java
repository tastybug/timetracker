package com.tastybug.timetracker.core.task.tracking.checkout;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;

public class CheckOutEvent extends UpdateTrackingRecordEvent {

    CheckOutEvent(TrackingRecord trackingRecord) {
        super(trackingRecord, true);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}
