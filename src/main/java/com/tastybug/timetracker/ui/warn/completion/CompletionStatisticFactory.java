package com.tastybug.timetracker.ui.warn.completion;

import android.content.Context;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.statistics.Completion;

import java.util.ArrayList;

public class CompletionStatisticFactory {

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    public CompletionStatisticFactory(Context context) {
        this.trackingRecordDAO = new TrackingRecordDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    public CompletionStatisticFactory(TrackingRecordDAO trackingRecordDAO,
                                      TrackingConfigurationDAO trackingConfigurationDAO) {
        this.trackingRecordDAO = trackingRecordDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    public Completion getCompletionBeforeLastRun(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        return new Completion(trackingConfiguration,
                trackingRecordArrayList.isEmpty() ? trackingRecordArrayList : trackingRecordArrayList.subList(0, trackingRecordArrayList.size() - 1),
                false);
    }

    public Completion getCompletionCurrent(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        return new Completion(trackingConfiguration, trackingRecordArrayList, false);
    }
}