package com.tastybug.timetracker.extension.warning.completion;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.Completion;

import java.util.ArrayList;

class CompletionStatisticFactory {

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    CompletionStatisticFactory(Context context) {
        this.trackingRecordDAO = new TrackingRecordDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    Completion getCompletionBeforeLastRun(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        return new Completion(trackingConfiguration,
                trackingRecordArrayList.isEmpty() ? trackingRecordArrayList : trackingRecordArrayList.subList(0, trackingRecordArrayList.size() - 1),
                false);
    }

    Completion getCompletionCurrent(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        return new Completion(trackingConfiguration, trackingRecordArrayList, false);
    }
}
