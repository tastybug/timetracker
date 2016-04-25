package com.tastybug.timetracker.gui.dialog.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;
import com.tastybug.timetracker.task.project.DeleteProjectTask;
import com.tastybug.timetracker.util.DurationFormatterFactory;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;

public class ConfirmDeleteProjectDialogFragment extends DialogFragment {

    private Project project;

    public static ConfirmDeleteProjectDialogFragment aDialog() {
        return new ConfirmDeleteProjectDialogFragment();
    }

    public ConfirmDeleteProjectDialogFragment forProject(Project project) {
        this.project = project;
        return this;
    }

    private String getDialogMessage(Project project) {
        project.setContext(getActivity());
        if (project.getTrackingRecords().isEmpty()) {
            return getString(R.string.msg_you_lose_no_tracking_records);
        } else {
            Duration currentDuration = new StatisticProjectDuration(
                    project.getTrackingConfiguration(),
                    project.getTrackingRecords(),
                    true
            ).get();
            PeriodFormatter formatter = DurationFormatterFactory.getFormatter(getActivity(), currentDuration);
            return getString(R.string.msg_you_lose_X, formatter.print(currentDuration.toPeriod()));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (project == null) {
            throw new IllegalStateException("No project given for deletion.");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_delete_project_X, project.getTitle()))
                .setMessage(getDialogMessage(project))
                .setPositiveButton(R.string.button_delete_project, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteProjectTask.aTask(getActivity()).withProjectUuid(project.getUuid()).execute();
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
