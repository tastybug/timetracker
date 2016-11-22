package com.tastybug.timetracker.ui.dialog.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.statistics.Duration;
import com.tastybug.timetracker.task.project.DeleteProjectTask;
import com.tastybug.timetracker.ui.util.LocalizedDurationFormatter;

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
        if (project.getTrackingRecords(getActivity()).isEmpty()) {
            return getString(R.string.msg_you_lose_no_tracking_records);
        } else {
            org.joda.time.Duration effectiveProjectDuration = new Duration(
                    project.getTrackingConfiguration(getActivity()),
                    project.getTrackingRecords(getActivity()),
                    true
            ).getDuration();
            return getString(R.string.msg_you_lose_X, LocalizedDurationFormatter.a(getActivity()).formatDuration(effectiveProjectDuration));
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
