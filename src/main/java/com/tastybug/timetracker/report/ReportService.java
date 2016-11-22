package com.tastybug.timetracker.report;


import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.filter.TrackingRecordTimeFrameFilter;
import com.tastybug.timetracker.report.internal.aggregated.AggregatedReportFactory;
import com.tastybug.timetracker.report.internal.nonaggregated.NonAggregatedReportFactory;

import java.io.IOException;
import java.util.Date;

public class ReportService {

    private Context context;
    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    public ReportService(Context context) {
        this.context = context;
        this.projectDAO = new ProjectDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    public Report createReport(String projectUuid, Date firstDay, Date lastDay, boolean aggregateDays) throws IOException {
        Project project = projectDAO.get(projectUuid).get();
        TrackingRecordTimeFrameFilter filter = new TrackingRecordTimeFrameFilter()
                .withFirstDay(firstDay)
                .withLastDayInclusive(lastDay)
                .withTrackingRecordList(trackingRecordDAO.getByProjectUuid(projectUuid));
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();

        if (!aggregateDays) {
            return new NonAggregatedReportFactory(context).create(project, firstDay, lastDay, filter.build(), filter.buildEdges(), trackingConfiguration);
        } else {
            return new AggregatedReportFactory(context).create(project, firstDay, lastDay, filter.build(), filter.buildEdges(), trackingConfiguration);
        }
    }
}
