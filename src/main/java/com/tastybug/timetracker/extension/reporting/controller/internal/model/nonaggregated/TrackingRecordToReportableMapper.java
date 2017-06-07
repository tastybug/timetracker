package com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import java.util.ArrayList;
import java.util.List;

public class TrackingRecordToReportableMapper {


    public List<ReportableItem> mapRecords(List<TrackingRecord> trackingRecords) {
        Preconditions.checkNotNull(trackingRecords);

        List<ReportableItem> simpleReportableList = new ArrayList<>();

        for (TrackingRecord trackingRecord : trackingRecords) {
            simpleReportableList.add(new TrackingRecordReportItem(trackingRecord));
        }

        return simpleReportableList;
    }
}
