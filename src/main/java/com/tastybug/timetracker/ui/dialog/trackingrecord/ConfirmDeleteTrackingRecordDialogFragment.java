package com.tastybug.timetracker.ui.dialog.trackingrecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.tracking.DeleteTrackingRecordTask;

public class ConfirmDeleteTrackingRecordDialogFragment extends DialogFragment {

    private static final String TRACKING_RECORD_ID = "TRACKING_RECORD_ID";

    private String trackingRecordUuid;

    public static ConfirmDeleteTrackingRecordDialogFragment aDialog() {
        return new ConfirmDeleteTrackingRecordDialogFragment();
    }

    public ConfirmDeleteTrackingRecordDialogFragment forTrackingRecordUuid(String trackingRecordUuid) {
        this.trackingRecordUuid = trackingRecordUuid;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TRACKING_RECORD_ID, trackingRecordUuid);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            trackingRecordUuid = savedInstanceState.getString(TRACKING_RECORD_ID);
        }
        if (trackingRecordUuid == null) {
            throw new IllegalStateException("No tracking record UUID given for deletion.");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_delete_tracking_record)
                .setMessage(R.string.msg_delete_tracking_record)
                .setPositiveButton(R.string.button_delete_tracking_record, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteTrackingRecordTask.aTask(getActivity())
                                .withTrackingRecordUuid(trackingRecordUuid)
                                .execute();
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
