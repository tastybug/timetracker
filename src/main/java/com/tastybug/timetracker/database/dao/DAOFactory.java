package com.tastybug.timetracker.database.dao;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

public class DAOFactory {

    public EntityDAO getDao(Class entityClass, Context context) {
        if (entityClass == Project.class) {
            return new ProjectDAO(context);
        } else if (entityClass == TrackingConfiguration.class) {
            return new TrackingConfigurationDAO(context);
        } else if (entityClass == TrackingRecord.class) {
            return new TrackingRecordDAO(context);
        } else {
            throw new IllegalArgumentException("Unexpected entity class: " + entityClass);
        }
    }

}
