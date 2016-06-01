package com.tastybug.timetracker.gui.eventhandler;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.gui.dialog.trackingrecord.EditTrackingRecordDescriptionDialogFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.util.DurationFormatterFactory;

import org.joda.time.Duration;

public class ShowPostTrackingSummarySnackbarHandler extends AbstractOttoEventHandler {

    private Activity activity;

    public ShowPostTrackingSummarySnackbarHandler(Activity activity) {
        this.activity = activity;
    }

    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        showTrackingSummary(event.getTrackingRecord());
    }

    private void showTrackingSummary(final TrackingRecord trackingRecord) {
        Project project = new ProjectDAO(activity).get(trackingRecord.getProjectUuid()).get();
        String durationString = getEffectiveDurationDescriptionString(trackingRecord);

        Snackbar.make(getRootViewOfCurrentActivity(activity),
                activity.getString(R.string.snack_X_booked_on_project_Y, durationString, project.getTitle()),
                Snackbar.LENGTH_LONG)
            .setAction(R.string.snack_describe_tracking_record, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTrackingRecordDescriptionDialogFragment
                            .aDialog()
                            .forTrackingRecord(trackingRecord)
                            .show(activity.getFragmentManager(), getClass().getSimpleName());
                }
            })
            .show();
    }

    private View getRootViewOfCurrentActivity(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }

    private String getEffectiveDurationDescriptionString(TrackingRecord trackingRecord) {
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationDAO(activity).getByProjectUuid(trackingRecord.getProjectUuid()).get();
        Optional<Duration> roundedDurationOpt = trackingRecord.toEffectiveDuration(trackingConfiguration);
        return DurationFormatterFactory.getFormatter(activity, roundedDurationOpt.get()).print(roundedDurationOpt.get().toPeriod());
    }
}
