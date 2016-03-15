package com.tastybug.timetracker.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.tracking.KickStartTrackingRecordTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfirmTrackingOutsideTimeFrameDialog extends DialogFragment {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String START_DATE_OPT = "START_DATE_OPT";
    private static final String END_DATE_OPT = "END_DATE_OPT";

    private String projectUuid;
    private Optional<Date> startDateOpt = Optional.absent();
    private Optional<Date> endDateOpt = Optional.absent();

    public static ConfirmTrackingOutsideTimeFrameDialog aDialog() {
        return new ConfirmTrackingOutsideTimeFrameDialog();
    }

    public ConfirmTrackingOutsideTimeFrameDialog forProjectUuid(String trackingRecordUuid) {
        this.projectUuid = trackingRecordUuid;
        return this;
    }

    public ConfirmTrackingOutsideTimeFrameDialog withViolatedProjectStartDate(Date date) {
        this.startDateOpt = Optional.of(date);
        return this;
    }

    public ConfirmTrackingOutsideTimeFrameDialog withViolatedProjectEndDate(Date date) {
        this.endDateOpt = Optional.of(date);
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
        outState.putSerializable(START_DATE_OPT, startDateOpt);
        outState.putSerializable(END_DATE_OPT, endDateOpt);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
            startDateOpt = (Optional<Date>) savedInstanceState.getSerializable(START_DATE_OPT);
            endDateOpt = (Optional<Date>) savedInstanceState.getSerializable(END_DATE_OPT);
        }
        if (projectUuid == null) {
            throw new IllegalStateException("No project UUID given for tracking.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getMessage())
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        KickStartTrackingRecordTask.aTask(getActivity())
                                .withProjectUuid(projectUuid)
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

    private String getMessage() {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        if(startDateOpt.isPresent()) {
           return getString(R.string.warning_tracking_before_project_time_frame_which_starts_at_X,
                   dateFormat.format(startDateOpt.get()));
        } else if (endDateOpt.isPresent()) {
            return getString(R.string.warning_tracking_after_project_time_frame_which_ended_on_X,
                    dateFormat.format(endDateOpt.get()));
        } else {
            throw new IllegalStateException("Either start- or end date must be set!");
        }
    }
}