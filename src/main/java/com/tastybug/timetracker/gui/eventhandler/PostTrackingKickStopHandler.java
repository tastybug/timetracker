package com.tastybug.timetracker.gui.eventhandler;

import android.app.Activity;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.dialog.trackingrecord.ConfirmKeepVeryShortTrackingRecordDialogFragment;
import com.tastybug.timetracker.gui.dialog.trackingrecord.EditTrackingRecordDescriptionDialogFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.util.DurationFormatterFactory;

import org.joda.time.Duration;

/**
 * Whenever a rather short TrackingRecord has been finished, ask the user whether to keep or
 * drop it. If its a proper Record, ask for a description!
 *
 * https://bitbucket.org/tastybug/timetracker/issues/42
 */
public class PostTrackingKickStopHandler extends AbstractOttoEventHandler {

    private Activity activity;

    public PostTrackingKickStopHandler(Activity activity) {
        this.activity = activity;
    }

    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        if (event.getTrackingRecord().isVeryShort()) {
            confirmKeepVeryShortRecord(event.getTrackingRecord().getUuid());
        } else {
            EditTrackingRecordDescriptionDialogFragment
                    .aDialog()
                    .forTrackingRecord(event.getTrackingRecord())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());
        }
        showTrackingSummary(event.getTrackingRecord());
    }

    private void confirmKeepVeryShortRecord(String trackingRecordUuid) {
        ConfirmKeepVeryShortTrackingRecordDialogFragment
                .aDialog()
                .forTrackingRecordUuid(trackingRecordUuid)
                .show(activity.getFragmentManager(), getClass().getSimpleName());
    }

    private void showTrackingSummary(TrackingRecord trackingRecord) {
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationDAO(activity).getByProjectUuid(trackingRecord.getProjectUuid()).get();
        Project project = new ProjectDAO(activity).get(trackingRecord.getProjectUuid()).get();
        Optional<Duration> roundedDurationOpt = trackingRecord.toEffectiveDuration(trackingConfiguration);
        String durationString = DurationFormatterFactory.getFormatter(activity, trackingRecord.toDuration().get()).print(roundedDurationOpt.get().toPeriod());
        Toast.makeText(activity, activity.getString(R.string.toast_X_booked_on_project_Y, durationString, project.getTitle()), Toast.LENGTH_LONG).show();
    }
}
