package com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;

import java.util.List;

public class TrackingRecordToAggregatedDayMapper {

    public List<AggregatedDay> mapTrackingRecordsToAggregatedDays(List<AggregatedDay> aggregatedDayList,
                                                                  List<TrackingRecord> trackingRecords) {
        Preconditions.checkNotNull(aggregatedDayList);
        Preconditions.checkNotNull(trackingRecords);

        for (TrackingRecord trackingRecord : trackingRecords) {
            for (AggregatedDay aggregatedDay : aggregatedDayList) {
                aggregatedDay.addRecord(trackingRecord);
            }
        }

        return aggregatedDayList;
    }
}
