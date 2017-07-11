package com.tastybug.timetracker.extension.warning.completion;

import android.content.Context;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class CompletionWarningService {

    private CompletionWarningSettings completionWarningSettings;
    private CompletionThresholdViolationIndicator completionThresholdViolationIndicator;
    private CompletionNotificationStarter completionNotificationStarter;

    public CompletionWarningService(Context context) {
        this(new CompletionWarningSettings(context),
                new CompletionThresholdViolationIndicator(context),
                new CompletionNotificationStarter(context));
    }

    CompletionWarningService(CompletionWarningSettings completionWarningSettings,
                             CompletionThresholdViolationIndicator completionThresholdViolationIndicator,
                             CompletionNotificationStarter completionNotificationStarter) {
        this.completionWarningSettings = completionWarningSettings;
        this.completionThresholdViolationIndicator = completionThresholdViolationIndicator;
        this.completionNotificationStarter = completionNotificationStarter;
    }

    public void handleProjectStopped(String uuid) {
        if (!isEnabled()) {
            logInfo(getClass().getSimpleName(), "Disabled: bailing out.");
            return;
        }
        showCompletionWarningWhenNecessary(uuid);
    }

    private boolean isEnabled() {
        return completionWarningSettings.isEnabled();
    }

    private void showCompletionWarningWhenNecessary(String uuid) {
        boolean warnCompletion = completionThresholdViolationIndicator.isWarning(uuid);
        if (warnCompletion) {
            logDebug(CompletionWarningService.class.getSimpleName(), "Warning to be shown for " + uuid);
            completionNotificationStarter.showCompletionWarningForProject(uuid);
        }
    }
}
