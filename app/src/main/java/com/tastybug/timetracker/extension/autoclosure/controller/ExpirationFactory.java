package com.tastybug.timetracker.extension.autoclosure.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.statistics.Expiration;

class ExpirationFactory {

    private TrackingConfigurationDAO trackingConfigurationDAO;

    ExpirationFactory(Context context) {
        this(new TrackingConfigurationDAO(context));
    }

    private ExpirationFactory(TrackingConfigurationDAO trackingConfigurationDAO) {
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    Expiration createExpiration(Project project) {
        return new Expiration(getTrackingConfiguration(project.getUuid()));
    }

    private TrackingConfiguration getTrackingConfiguration(String projectUuid) {
        return trackingConfigurationDAO.getByProjectUuid(projectUuid).get();
    }
}
