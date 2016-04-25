package com.tastybug.timetracker.gui.eventhandler;

import android.app.Activity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.gui.dialog.trackingrecord.EditTrackingRecordDescriptionDialogFragment;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

/**
 * Whenever a rather short TrackingRecord has been finished, ask the user whether to keep or
 * drop it. If its a proper Record, ask for a description!
 *
 * https://bitbucket.org/tastybug/timetracker/issues/42
 */
public class DescribeOrDropTinyRecordHandler extends AbstractOttoEventHandler {

    private Activity activity;

    public DescribeOrDropTinyRecordHandler(Activity activity) {
        this.activity = activity;
    }

    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        if (event.getTrackingRecord().isVeryShort()) {
            Toast.makeText(activity, "Rather short, isnt it?", Toast.LENGTH_SHORT).show();
        } else {
            EditTrackingRecordDescriptionDialogFragment
                    .aDialog()
                    .forTrackingRecord(event.getTrackingRecord())
                    .show(activity.getFragmentManager(), getClass().getSimpleName());
        }
    }


}
