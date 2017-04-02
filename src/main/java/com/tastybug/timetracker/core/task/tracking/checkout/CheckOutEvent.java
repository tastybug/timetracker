package com.tastybug.timetracker.core.task.tracking.checkout;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;

import static com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent.Type.CHECKOUT;

public class CheckOutEvent extends UpdateTrackingRecordEvent {

    CheckOutEvent(TrackingRecord trackingRecord) {
        super(trackingRecord, true);
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new TrackingRecordChangeIntent(trackingRecord.getProjectUuid(),
                trackingRecord.getUuid(),
                CHECKOUT,
                true);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}
