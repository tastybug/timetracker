package com.tastybug.timetracker.extensions.warning.completion;

import android.content.Context;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;

public class CompletionWarningService {

    private CompletionThresholdViolationIndicator completionThresholdViolationIndicator;
    private CompletionNotificationStarter completionNotificationStarter;

    public CompletionWarningService(Context context) {
        this.completionThresholdViolationIndicator = new CompletionThresholdViolationIndicator(context);
        this.completionNotificationStarter = new CompletionNotificationStarter(context);
    }

    public CompletionWarningService(CompletionThresholdViolationIndicator completionThresholdViolationIndicator,
                                    CompletionNotificationStarter completionNotificationStarter) {
        this.completionThresholdViolationIndicator = completionThresholdViolationIndicator;
        this.completionNotificationStarter = completionNotificationStarter;
    }

    public void handleProjectStopped(String uuid) {
        showCompletionWarningWhenNecessary(uuid);
    }

    private void showCompletionWarningWhenNecessary(String uuid) {
        boolean warnCompletion = completionThresholdViolationIndicator.isWarning(uuid);
        if (warnCompletion) {
            logDebug(CompletionWarningService.class.getSimpleName(), "Warning to be shown for " + uuid);
            completionNotificationStarter.showCompletionWarningForProject(uuid);
        }
    }
}
