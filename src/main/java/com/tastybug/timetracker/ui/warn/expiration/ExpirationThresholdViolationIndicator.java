package com.tastybug.timetracker.ui.warn.expiration;

import android.content.Context;

public class ExpirationThresholdViolationIndicator {

    private static final int THRESHOLD = 90;

    private ExpirationStatisticFactory expirationStatisticFactory;


    public ExpirationThresholdViolationIndicator(Context context) {
        this.expirationStatisticFactory = new ExpirationStatisticFactory(context);
    }

    public ExpirationThresholdViolationIndicator(ExpirationStatisticFactory expirationStatisticFactory) {
        this.expirationStatisticFactory = expirationStatisticFactory;
    }

    public boolean isWarning(String projectUuid) {
        return isLastRunBelowThresholdWhenEnding(projectUuid)
                && isAboveThresholdWhenFinishing(projectUuid);
    }

    private boolean isLastRunBelowThresholdWhenEnding(String projectUuid) {
        return expirationStatisticFactory.getExpirationOnCheckOutOfPreviousSession(projectUuid).getExpirationPercent().or(0) < THRESHOLD;
    }

    private boolean isAboveThresholdWhenFinishing(String projectUuid) {
        return expirationStatisticFactory.getExpirationOnCheckoutOfLastSession(projectUuid).getExpirationPercent().or(0) >= THRESHOLD;
    }
}
