package com.tastybug.timetracker.ui.dialog.trackingrecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.tracking.checkin.CheckInTask;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.util.Date;

public class ConfirmCheckInViolatesConfigurationDialog extends DialogFragment {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String START_DATE_OPT = "START_DATE_OPT";
    private static final String END_DATE_OPT = "END_DATE_OPT";
    private static final String MAX_HOURS_OPT = "MAX_HOURS_OPT";

    private String projectUuid;
    private Optional<Date> startDateOpt = Optional.absent();
    private Optional<Date> endDateOpt = Optional.absent();
    private Optional<Integer> maxHoursOpt = Optional.absent();

    public static ConfirmCheckInViolatesConfigurationDialog aDialog() {
        return new ConfirmCheckInViolatesConfigurationDialog();
    }

    public ConfirmCheckInViolatesConfigurationDialog forProjectUuid(String trackingRecordUuid) {
        this.projectUuid = trackingRecordUuid;
        return this;
    }

    public ConfirmCheckInViolatesConfigurationDialog withViolatedProjectStartDate(Date date) {
        this.startDateOpt = Optional.of(date);
        return this;
    }

    public ConfirmCheckInViolatesConfigurationDialog withViolatedProjectEndDate(Date date) {
        this.endDateOpt = Optional.of(date);
        return this;
    }

    public ConfirmCheckInViolatesConfigurationDialog withViolatedProjectAmount(int maxHours) {
        this.maxHoursOpt = Optional.of(maxHours);
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
        outState.putSerializable(START_DATE_OPT, startDateOpt);
        outState.putSerializable(END_DATE_OPT, endDateOpt);
        outState.putSerializable(MAX_HOURS_OPT, maxHoursOpt);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
            startDateOpt = (Optional<Date>) savedInstanceState.getSerializable(START_DATE_OPT);
            endDateOpt = (Optional<Date>) savedInstanceState.getSerializable(END_DATE_OPT);
            maxHoursOpt = (Optional<Integer>) savedInstanceState.getSerializable(MAX_HOURS_OPT);
        }
        if (projectUuid == null) {
            throw new IllegalStateException("No project UUID given for tracking.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getMessage())
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new CheckInTask(getActivity())
                                .withProjectUuid(projectUuid)
                                .run();
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
        if (startDateOpt.isPresent()) {
            return getString(R.string.warning_tracking_before_project_time_frame_which_starts_at_X,
                    DefaultLocaleDateFormatter.date().format(startDateOpt.get()));
        } else if (endDateOpt.isPresent()) {
            return getString(R.string.warning_tracking_after_project_time_frame_which_ended_on_X,
                    DefaultLocaleDateFormatter.date().format(endDateOpt.get()));
        } else if (maxHoursOpt.isPresent()) {
            return getString(R.string.warning_tracking_exceeds_project_amount_of_X_hours,
                    maxHoursOpt.get());
        } else {
            throw new IllegalStateException("Either start- or end date must be set!");
        }
    }
}