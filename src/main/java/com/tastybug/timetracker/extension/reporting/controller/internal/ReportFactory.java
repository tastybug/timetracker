package com.tastybug.timetracker.extension.reporting.controller.internal;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.Report;

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
