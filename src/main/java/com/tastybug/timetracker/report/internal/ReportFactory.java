package com.tastybug.timetracker.report.internal;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.report.Report;

import java.util.Date;
import java.util.List;

public interface ReportFactory {

    Report create(Project project,
                  Date firstDay,
                  Date lastDay,
                  List<TrackingRecord> includedTrackingRecords,
                  List<TrackingRecord> edgeTrackingRecords,
                  TrackingConfiguration trackingConfiguration);
}
