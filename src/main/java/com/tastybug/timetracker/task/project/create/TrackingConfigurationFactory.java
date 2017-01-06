package com.tastybug.timetracker.task.project.create;

import com.tastybug.timetracker.model.TrackingConfiguration;

public class TrackingConfigurationFactory {

    TrackingConfiguration aTrackingConfiguration(String projectUuid) {
        return new TrackingConfiguration(projectUuid);
    }
}
