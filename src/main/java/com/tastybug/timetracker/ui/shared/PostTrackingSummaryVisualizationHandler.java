package com.tastybug.timetracker.ui.shared;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.task.tracking.CheckOutEvent;
import com.tastybug.timetracker.ui.core.AbstractOttoEventHandler;
import com.tastybug.timetracker.ui.dialog.trackingrecord.EditTrackingRecordDescriptionDialogFragment;

public class PostTrackingSummaryVisualizationHandler extends AbstractOttoEventHandler {

    private Activity activity;

    public PostTrackingSummaryVisualizationHandler(Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        if(askForDescriptionDirectly(event.getTrackingRecord().getProjectUuid())) {
            EditTrackingRecordDescriptionDialogFragment
                    .aDialog()
                    .forTrackingRecord(event.getTrackingRecord())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());
        } else {
            showSummarySnackBar(event.getTrackingRecord());
        }
    }

    private boolean askForDescriptionDirectly(String projectUuid) {
        return new TrackingConfigurationDAO(activity).getByProjectUuid(projectUuid).get().isPromptForDescription();
    }

    private void showSummarySnackBar(final TrackingRecord trackingRecord) {
        Project project = new ProjectDAO(activity).get(trackingRecord.getProjectUuid()).get();
        String durationString = DurationFormatter.a().formatEffectiveDuration(activity, trackingRecord);

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
}
