package com.tastybug.timetracker.extension.reporting.controller.internal;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;

import java.util.Date;
import java.util.List;

public interface ReportFactory {

    Report createReport(Project project, Date firstDay, Date lastDay, List<TrackingRecord> trackingRecords);

}
