package com.tastybug.timetracker.extension.reporting.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.reporting.controller.internal.TrackingRecordTimeFrameFilter;

import java.util.Date;
import java.util.List;

class TrackingRecordFilteringService {

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingRecordTimeFrameFilter trackingRecordTimeFrameFilter;

    TrackingRecordFilteringService(Context context) {
        this(new TrackingRecordDAO(context),
                new TrackingRecordTimeFrameFilter());
    }

    private TrackingRecordFilteringService(TrackingRecordDAO trackingRecordDAO,
                                           TrackingRecordTimeFrameFilter timeFrameFilter) {
        this.trackingRecordDAO = trackingRecordDAO;
        this.trackingRecordTimeFrameFilter = timeFrameFilter;
    }

    List<TrackingRecord> getFilteredByTimeFrame(String projectUuid, Date notBefore, Date notAfter) {
        java.util.List<com.tastybug.timetracker.core.model.TrackingRecord> allTrackingRecords = trackingRecordDAO.getByProjectUuid(projectUuid);
        return trackingRecordTimeFrameFilter.getRecordsWithinTimeFrame(allTrackingRecords, notBefore, notAfter);
    }
}
