package com.tastybug.timetracker.extensions.warning.expiration;

import android.content.Context;

import com.tastybug.timetracker.model.dao.ProjectDAO;

class ExpirationThresholdViolationIndicator {

    private static final int THRESHOLD = 90;

    private ExpirationStatisticFactory expirationStatisticFactory;
    private ProjectDAO projectDAO;

    ExpirationThresholdViolationIndicator(Context context) {
        this(new ExpirationStatisticFactory(context),
                new ProjectDAO(context));
    }

    ExpirationThresholdViolationIndicator(ExpirationStatisticFactory expirationStatisticFactory, ProjectDAO projectDAO) {
        this.expirationStatisticFactory = expirationStatisticFactory;
        this.projectDAO = projectDAO;
    }

    boolean isWarning(String projectUuid) {
        return !isProjectClosed(projectUuid)
                && isLastRunBelowThresholdWhenEnding(projectUuid)
                && isAboveThresholdWhenFinishing(projectUuid);
    }

    private boolean isLastRunBelowThresholdWhenEnding(String projectUuid) {
        return expirationStatisticFactory.getExpirationOnCheckOutOfPreviousSession(projectUuid).getExpirationPercent().or(0) < THRESHOLD;
    }

    private boolean isAboveThresholdWhenFinishing(String projectUuid) {
        return expirationStatisticFactory.getExpirationOnCheckoutOfLastSession(projectUuid).getExpirationPercent().or(0) >= THRESHOLD;
    }

    private boolean isProjectClosed(String projectUuid) {
        return projectDAO.get(projectUuid).get().isClosed();
    }
}
