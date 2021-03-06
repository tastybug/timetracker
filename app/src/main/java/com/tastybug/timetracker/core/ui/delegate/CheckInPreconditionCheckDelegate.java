package com.tastybug.timetracker.core.ui.delegate;

import android.app.Activity;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.ProjectDuration;
import com.tastybug.timetracker.core.task.tracking.checkin.CheckInTask;
import com.tastybug.timetracker.core.ui.dialog.trackingrecord.ConfirmCheckInViolatesConfigurationDialog;

import java.util.ArrayList;
import java.util.Date;

/**
 * Dieser Delegate kapselt einige Vorpruefungen, die beim CheckIn durchgefuehrt werden
 * muessen und in der UI mittels Warnungsdialogen angezeigt werden muessen.
 * Da das CheckIn von mehreren Stellen aus gestartet werden kann, ist das hier zentralisiert.
 */
public class CheckInPreconditionCheckDelegate {

    private Activity activity;

    protected CheckInPreconditionCheckDelegate(Activity activity) {
        this.activity = activity;
    }

    public static CheckInPreconditionCheckDelegate aDelegate(Activity activity) {
        return new CheckInPreconditionCheckDelegate(activity);
    }

    public void startTracking(Project project) {

        if (blameProjectTimeFrameViolation(project)) {
            return;
        } else if (blameProjectAmountViolation(project)) {
            return;
        } else {
            new CheckInTask(activity).withProjectUuid(project.getUuid()).run();
        }
    }

    private boolean blameProjectAmountViolation(Project project) {
        Optional<Integer> violatedProjectAmount = getProjectAmountIfViolated(project);

        if (violatedProjectAmount.isPresent()) {
            ConfirmCheckInViolatesConfigurationDialog.aDialog()
                    .forProjectUuid(project.getUuid())
                    .withViolatedProjectAmount(violatedProjectAmount.get())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());
        }
        return violatedProjectAmount.isPresent();
    }

    private boolean blameProjectTimeFrameViolation(Project project) {
        Optional<Date> violatedStartDate = getProjectStartDateIfViolated(project);
        Optional<Date> violatedEndDate = getProjectEndDateIfViolated(project);

        if (violatedStartDate.isPresent()) {
            ConfirmCheckInViolatesConfigurationDialog.aDialog()
                    .forProjectUuid(project.getUuid())
                    .withViolatedProjectStartDate(violatedStartDate.get())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());

        } else if (violatedEndDate.isPresent()) {
            ConfirmCheckInViolatesConfigurationDialog.aDialog()
                    .forProjectUuid(project.getUuid())
                    .withViolatedProjectEndDate(violatedEndDate.get())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());
        }
        return violatedStartDate.isPresent() || violatedEndDate.isPresent();
    }

    private Optional<Date> getProjectStartDateIfViolated(Project project) {
        TrackingConfiguration configuration = new TrackingConfigurationDAO(activity)
                .getByProjectUuid(project.getUuid()).get();

        return configuration.getStart().isPresent()
                && configuration.getStart().get().after(new Date())
                ? configuration.getStart()
                : Optional.<Date>absent();
    }

    private Optional<Date> getProjectEndDateIfViolated(Project project) {
        TrackingConfiguration configuration = new TrackingConfigurationDAO(activity)
                .getByProjectUuid(project.getUuid()).get();

        return configuration.getEnd().isPresent()
                && configuration.getEnd().get().before(new Date())
                ? configuration.getEnd()
                : Optional.<Date>absent();
    }

    private Optional<Integer> getProjectAmountIfViolated(Project project) {
        TrackingConfiguration configuration = new TrackingConfigurationDAO(activity)
                .getByProjectUuid(project.getUuid()).get();

        if (configuration.getHourLimit().isPresent()) {
            ArrayList<TrackingRecord> trackingRecordList = new TrackingRecordDAO(activity).getByProjectUuid(project.getUuid());

            org.joda.time.Duration effectiveDuration = new ProjectDuration(trackingRecordList).getDuration();
            if (!effectiveDuration.isShorterThan(org.joda.time.Duration.standardHours(configuration.getHourLimit().get()))) {
                return configuration.getHourLimit();
            }
        }
        return Optional.absent();
    }
}
