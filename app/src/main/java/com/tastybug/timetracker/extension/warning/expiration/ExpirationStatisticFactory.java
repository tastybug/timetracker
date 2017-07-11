package com.tastybug.timetracker.extension.warning.expiration;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.Expiration;

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

    public Expiration getExpirationOnCheckOutOfPreviousSession(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        Collections.sort(trackingRecordArrayList);
        if (trackingRecordArrayList.size() < 2) {
            return new Expiration(trackingConfiguration, new Date(0));
        } else {
            return new Expiration(trackingConfiguration, trackingRecordArrayList.get(trackingRecordArrayList.size() - 2).getEnd().get());
        }
    }

    public Expiration getExpirationOnCheckoutOfLastSession(String projectUuid) {
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
        ArrayList<TrackingRecord> trackingRecordArrayList = trackingRecordDAO.getByProjectUuid(projectUuid);

        Collections.sort(trackingRecordArrayList);
        return new Expiration(trackingConfiguration, trackingRecordArrayList.get(trackingRecordArrayList.size() - 1).getEnd().get());
    }
}
