package com.tastybug.timetracker.extension.reporting.controller.internal.aggregated;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;

import java.util.List;

class TrackingRecordToAggregatedDayMapper {

    List<AggregatedDay> mapTrackingRecordsToAggregatedDays(List<AggregatedDay> aggregatedDayList,
                                                           List<TrackingRecord> trackingRecords,
                                                           TrackingConfiguration trackingConfiguration) {
        Preconditions.checkNotNull(aggregatedDayList);
        Preconditions.checkNotNull(trackingRecords);
        Preconditions.checkNotNull(trackingConfiguration);

        for (TrackingRecord trackingRecord : trackingRecords) {
            for (AggregatedDay aggregatedDay : aggregatedDayList) {
                aggregatedDay.addRecord(trackingRecord, trackingConfiguration);
            }
        }

        return aggregatedDayList;
    }
}
