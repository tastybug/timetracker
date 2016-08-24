package com.tastybug.timetracker.ui.warn.completion;

import android.content.Context;

public class CompletionThresholdViolationIndicator {

    private static final int THRESHOLD = 90;

    private CompletionStatisticFactory completionStatisticFactory;


    public CompletionThresholdViolationIndicator(Context context) {
        this.completionStatisticFactory = new CompletionStatisticFactory(context);
    }

    public CompletionThresholdViolationIndicator(CompletionStatisticFactory statisticFactory) {
        this.completionStatisticFactory = statisticFactory;
    }

    public boolean isWarning(String projectUuid) {

        return isBelowThresholdBeforeLastSession(projectUuid)
                && isAboveThresholdAfterLastSession(projectUuid);
    }

    private boolean isBelowThresholdBeforeLastSession(String projectUuid) {
        return completionStatisticFactory.getCompletionBeforeLastRun(projectUuid).getCompletionPercent().or(0d) < THRESHOLD;
    }

    private boolean isAboveThresholdAfterLastSession(String projectUuid) {
        return completionStatisticFactory.getCompletionCurrent(projectUuid).getCompletionPercent().or(0d) >= THRESHOLD;
    }
}
