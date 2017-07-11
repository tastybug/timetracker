package com.tastybug.timetracker.extension.warning.expiration;

import android.content.Context;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class ExpirationWarningService {

    private ExpirationWarningSettings expirationWarningSettings;
    private ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator;
    private ExpirationNotificationStarter expirationNotificationStarter;

    public ExpirationWarningService(Context context) {
        this(new ExpirationWarningSettings(context),
                new ExpirationThresholdViolationIndicator(context),
                new ExpirationNotificationStarter(context));
    }

    ExpirationWarningService(ExpirationWarningSettings expirationWarningSettings,
                             ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator,
                             ExpirationNotificationStarter expirationNotificationStarter) {
        this.expirationWarningSettings = expirationWarningSettings;
        this.expirationThresholdViolationIndicator = expirationThresholdViolationIndicator;
        this.expirationNotificationStarter = expirationNotificationStarter;
    }

    public void handleProjectStopped(String uuid) {
        if (!isEnabled()) {
            logInfo(getClass().getSimpleName(), "Disabled: bailing out.");
            return;
        }
        showExpirationWarningWhenNecessary(uuid);
    }

    private boolean isEnabled() {
        return expirationWarningSettings.isEnabled();
    }

    private void showExpirationWarningWhenNecessary(String uuid) {
        boolean warnExpiration = expirationThresholdViolationIndicator.isWarning(uuid);
        if (warnExpiration) {
            logDebug(ExpirationWarningService.class.getSimpleName(), "Warning to be shown for " + uuid);
            expirationNotificationStarter.showExpirationWarningForProject(uuid);
        }
    }
}
