package com.tastybug.timetracker.core.ui.projectdetails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.task.project.update.UpdateProjectTask;

public class ConfirmProjectCloseToggleDialogFragment extends DialogFragment {

    private Project project;

    public static ConfirmProjectCloseToggleDialogFragment aDialog() {
        return new ConfirmProjectCloseToggleDialogFragment();
    }

    public ConfirmProjectCloseToggleDialogFragment forProject(Project project) {
        this.project = project;
        return this;
    }

    private CharSequence getDialogMessage(Project project) {
        return getText(project.isClosed() ? R.string.confirm_project_reopen : R.string.confirm_project_close);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (project == null) {
            throw new IllegalStateException("No project given for close/re-open.");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_project_closure_change)
                .setMessage(getDialogMessage(project))
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new UpdateProjectTask(getActivity())
                                .withProjectUuid(project.getUuid())
                                .withClosureState(!project.isClosed())
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
}
