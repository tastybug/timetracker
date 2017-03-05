package com.tastybug.timetracker.extensions.reporting.controller.internal;

import com.tastybug.timetracker.extensions.reporting.controller.Report;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

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
