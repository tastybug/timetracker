package com.tastybug.timetracker.task.tracking.checkout;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.modify.ModifiedTrackingRecordEvent;

public class CheckOutEvent extends ModifiedTrackingRecordEvent {

    CheckOutEvent(TrackingRecord trackingRecord) {
        super(trackingRecord, true);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}
