package com.tastybug.timetracker.report.internal.nonaggregated;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.report.internal.ReportableItem;

import java.util.ArrayList;
import java.util.List;

class TrackingRecordToReportableMapper {


    List<ReportableItem> mapRecords(List<TrackingRecord> trackingRecords,
                                    TrackingConfiguration trackingConfiguration) {

        Preconditions.checkNotNull(trackingRecords);
        Preconditions.checkNotNull(trackingConfiguration);

        List<ReportableItem> simpleReportableList = new ArrayList<>();

        for (TrackingRecord trackingRecord : trackingRecords) {
            simpleReportableList.add(new TrackingRecordReportItem(trackingRecord, trackingConfiguration));
        }

        return simpleReportableList;
    }
}
