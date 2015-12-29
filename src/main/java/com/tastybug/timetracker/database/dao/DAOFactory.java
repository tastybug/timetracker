package com.tastybug.timetracker.database.dao;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.ProjectTimeConstraints;
import com.tastybug.timetracker.model.TimeFrame;

public class DAOFactory {

    public EntityDAO getDao(Class entityClass, Context context) {
        if (entityClass == Project.class) {
            return new ProjectDAO(context);
        } else if (entityClass == ProjectTimeConstraints.class) {
            return new ProjectTimeConstraintsDAO(context);
        } else if (entityClass == TimeFrame.class) {
            return new TimeFrameDAO(context);
        } else {
            throw new IllegalArgumentException("Unexpected entity class: " + entityClass);
        }
    }

}