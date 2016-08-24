package com.tastybug.timetracker.ui.warn.expiration;

import android.content.Context;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.statistics.StatisticProjectExpiration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ExpirationStatisticFactory {

    private TrackingRecordDAO trackingRecordDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    public ExpirationStatisticFactory(Context context) {
        this.trackingRecordDAO = new TrackingRecordDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    public ExpirationStatisticFactory(TrackingRecordDAO trackingRecordDAO,
                                      TrackingConfigurationDAO trackingConfigurationDAO) {
        this.trackingRecordDAO = trackingRecordDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    public StatisticProjectExpiration getExpirationOnCheckOutOfPreviousSession(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        Collections.sort(trackingRecordArrayList);
        if (trackingRecordArrayList.size() < 2) {
            return new StatisticProjectExpiration(trackingConfiguration, new Date(0));
        } else {
            return new StatisticProjectExpiration(trackingConfiguration, trackingRecordArrayList.get(trackingRecordArrayList.size() - 2).getEnd().get());
        }
    }

    public StatisticProjectExpiration getExpirationOnCheckoutOfLastSession(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        Collections.sort(trackingRecordArrayList);
        return new StatisticProjectExpiration(trackingConfiguration, trackingRecordArrayList.get(trackingRecordArrayList.size() - 1).getEnd().get());
    }
}
