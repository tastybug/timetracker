package com.tastybug.timetracker.core.task.tracking.update;

import android.content.Intent;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.LifecycleEvent;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;

import static com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent.Type.UPDATE;

public class UpdateTrackingRecordEvent implements LifecycleEvent {

    protected TrackingRecord trackingRecord;
    protected boolean wasStopped = false;

    public UpdateTrackingRecordEvent(TrackingRecord trackingRecord, boolean wasStopped) {
        this.trackingRecord = trackingRecord;
        this.wasStopped = wasStopped;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }

    public boolean wasStopped() {
        return wasStopped;
    }

    @Override
    public Intent getAsBroadcastEvent() {
        return new TrackingRecordChangeIntent(trackingRecord.getProjectUuid(),
                trackingRecord.getUuid(),
                UPDATE,
                wasStopped());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .add("wasStopped", wasStopped())
                .toString();
    }
}