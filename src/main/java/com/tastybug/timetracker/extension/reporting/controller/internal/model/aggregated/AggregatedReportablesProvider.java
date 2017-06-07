package com.tastybug.timetracker.extension.reporting.controller.internal.model.aggregated;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AggregatedReportablesProvider {

    private DayListFactory dayListFactory = new DayListFactory();
    private TrackingRecordToAggregatedDayMapper trackingRecordToAggregatedDayMapper = new TrackingRecordToAggregatedDayMapper();

    public List<ReportableItem> getReportables(Date firstDay,
                                               Date lastDay,
                                               List<TrackingRecord> includedTrackingRecords) {
        List<AggregatedDay> aggregatedDayList = dayListFactory.createList(firstDay, lastDay);
        aggregatedDayList = trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(aggregatedDayList, includedTrackingRecords);
        return filterEmptyDays(aggregatedDayList);
    }

    private List<ReportableItem> filterEmptyDays(List<AggregatedDay> aggregatedDays) {
        ArrayList<ReportableItem> results = new ArrayList<>();
        for (ReportableItem reportable : aggregatedDays) {
            if (reportable.getDuration().getMillis() > 0) {
                results.add(reportable);
            }
        }
        return results;
    }
}
