package com.tastybug.timetracker.core.task.tracking.checkin;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;

import static com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent.Type.CHECKIN;

public class CheckInEvent extends CreatedTrackingRecordEvent {

    CheckInEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new TrackingRecordChangeIntent(trackingRecord.getProjectUuid(),
                trackingRecord.getUuid(),
                CHECKIN,
                false);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}
