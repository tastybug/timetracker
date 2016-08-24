package com.tastybug.timetracker.ui.warn.expiration;

import android.content.Context;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;

public class ExpirationWarningService {

    private ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator;
    private ExpirationNotificationStarter expirationNotificationStarter;

    public ExpirationWarningService(Context context) {
        this.expirationThresholdViolationIndicator = new ExpirationThresholdViolationIndicator(context);
        this.expirationNotificationStarter = new ExpirationNotificationStarter(context);
    }

    public ExpirationWarningService(ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator,
                                    ExpirationNotificationStarter expirationNotificationStarter) {
        this.expirationThresholdViolationIndicator = expirationThresholdViolationIndicator;
        this.expirationNotificationStarter = expirationNotificationStarter;
    }

    public void handleProjectStopped(String uuid) {
        showExpirationWarningWhenNecessary(uuid);
    }

    private void showExpirationWarningWhenNecessary(String uuid) {
        boolean warnExpiration = expirationThresholdViolationIndicator.isWarning(uuid);
        if (warnExpiration) {
            logDebug(ExpirationWarningService.class.getSimpleName(), "Warning to be shown for " + uuid);
            expirationNotificationStarter.showExpirationWarningForProject(uuid);
        }
    }
}
