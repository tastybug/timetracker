package com.tastybug.timetracker.core.task.tracking.create;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;

import static com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent.Type.CREATE;

public class CreatedTrackingRecordEvent implements LifecycleEvent {

    protected TrackingRecord trackingRecord;

    public CreatedTrackingRecordEvent(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new TrackingRecordChangeIntent(trackingRecord.getProjectUuid(),
                trackingRecord.getUuid(),
                CREATE,
                false);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}