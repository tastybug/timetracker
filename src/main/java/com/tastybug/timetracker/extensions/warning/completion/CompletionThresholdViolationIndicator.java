package com.tastybug.timetracker.extensions.warning.completion;

import android.content.Context;

import com.tastybug.timetracker.model.dao.ProjectDAO;

class CompletionThresholdViolationIndicator {

    private static final int THRESHOLD = 90;

    private CompletionStatisticFactory completionStatisticFactory;
    private ProjectDAO projectDAO;

    CompletionThresholdViolationIndicator(Context context) {
        this(new CompletionStatisticFactory(context),
                new ProjectDAO(context));
    }

    CompletionThresholdViolationIndicator(CompletionStatisticFactory statisticFactory,
                                          ProjectDAO projectDAO) {
        this.completionStatisticFactory = statisticFactory;
        this.projectDAO = projectDAO;
    }

    boolean isWarning(String projectUuid) {
        return !isProjectClosed(projectUuid)
                && isBelowThresholdBeforeLastSession(projectUuid)
                && isAboveThresholdAfterLastSession(projectUuid);
    }

    private boolean isBelowThresholdBeforeLastSession(String projectUuid) {
        return completionStatisticFactory.getCompletionBeforeLastRun(projectUuid).getCompletionPercent().or(0d) < THRESHOLD;
    }

    private boolean isAboveThresholdAfterLastSession(String projectUuid) {
        return completionStatisticFactory.getCompletionCurrent(projectUuid).getCompletionPercent().or(0d) >= THRESHOLD;
    }

    private boolean isProjectClosed(String projectUuid) {
        return projectDAO.get(projectUuid).get().isClosed();
    }
}
