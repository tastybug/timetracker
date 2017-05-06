package com.tastybug.timetracker.extension.autoclosure.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.statistics.Expiration;

class ClosabilityIndicator {

    private ExpirationFactory expirationFactory;

    ClosabilityIndicator(Context context) {
        this(new ExpirationFactory(context));
    }

    ClosabilityIndicator(ExpirationFactory expirationFactory) {
        this.expirationFactory = expirationFactory;
    }

    boolean isProjectClosable(Project project) {
        if (!project.isClosed()) {
            Expiration expiration = expirationFactory.createExpiration(project);
            return expiration.isExpired();
        }
        return false;
    }

}
