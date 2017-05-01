package com.tastybug.timetracker.extension.reporting.controller;


import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.reporting.controller.internal.TrackingRecordTimeFrameFilter;
import com.tastybug.timetracker.extension.reporting.controller.internal.aggregated.AggregatedReportFactory;
import com.tastybug.timetracker.extension.reporting.controller.internal.nonaggregated.NonAggregatedReportFactory;

import java.io.IOException;
import java.util.Date;

public class ReportService {

    private Context context;
    private ProjectDAO projectDAO;
    private TrackingRecordDAO trackingRecordDAO;

    public ReportService(Context context) {
        this.context = context;
        this.projectDAO = new ProjectDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public Report createReport(String projectUuid, Date firstDay, Date lastDay, boolean aggregateDays) throws IOException {
        Project project = projectDAO.get(projectUuid).get();
        TrackingRecordTimeFrameFilter filter = new TrackingRecordTimeFrameFilter()
                .withFirstDay(firstDay)
                .withLastDayInclusive(lastDay)
                .withTrackingRecordList(trackingRecordDAO.getByProjectUuid(projectUuid));

        if (!aggregateDays) {
            return new NonAggregatedReportFactory(context).create(project, firstDay, lastDay, filter.build(), filter.buildEdges());
        } else {
            return new AggregatedReportFactory(context).create(project, firstDay, lastDay, filter.build(), filter.buildEdges());
        }
    }
}
