package com.tastybug.timetracker.extension.reporting.controller.internal;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TrackingRecordTimeFrameFilter {

    public TrackingRecordTimeFrameFilter() {}

    public List<TrackingRecord> getRecordsWithinTimeFrame(List<TrackingRecord> trackingRecords, Date notBefore, Date notAfter) {
        Preconditions.checkArgument(notBefore.before(notAfter));

        return getFilteredList(trackingRecords, notBefore, notAfter);
    }

    @NonNull
    private ArrayList<TrackingRecord> getFilteredList(List<TrackingRecord> trackingRecords, Date notBefore, Date notAfter) {

        ArrayList<TrackingRecord> resultList = new ArrayList<>();
        for (TrackingRecord trackingRecord : trackingRecords) {
            if (!trackingRecord.isFinished()) {
                continue;
            }
            if (isTrackingRecordEndingBeforeTimeFrame(trackingRecord, notBefore)) {
                continue;
            }
            if (isTrackingRecordEndingAfterTimeFrame(trackingRecord, notAfter)) {
                continue;
            }
            resultList.add(trackingRecord);
        }
        return resultList;
    }

    private boolean isTrackingRecordEndingBeforeTimeFrame(TrackingRecord trackingRecord, Date startDate) {
        return trackingRecord.getEnd().get().before(startDate);
    }

    private boolean isTrackingRecordEndingAfterTimeFrame(TrackingRecord trackingRecord, Date endExclusive) {
        return !trackingRecord.getEnd().get().before(endExclusive);
    }
}
