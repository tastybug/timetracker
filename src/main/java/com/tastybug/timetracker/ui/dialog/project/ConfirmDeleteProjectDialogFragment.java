package com.tastybug.timetracker.ui.dialog.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.statistics.ProjectDuration;
import com.tastybug.timetracker.task.project.delete.DeleteProjectTask;
import com.tastybug.timetracker.ui.util.LocalizedDurationFormatter;

import org.joda.time.Duration;

import java.util.List;

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
        if (getTrackingRecordsByProject(project).isEmpty()) {
            return getString(R.string.msg_you_lose_no_tracking_records);
        } else {
            Duration effectiveProjectDuration = new ProjectDuration(
                    getTrackingConfigurationForProject(project),
                    getTrackingRecordsByProject(project),
                    true
            ).getDuration();
            return getString(R.string.msg_you_lose_X, LocalizedDurationFormatter.a(getActivity()).formatDuration(effectiveProjectDuration));
        }
    }

    private TrackingConfiguration getTrackingConfigurationForProject(Project project) {
        return new TrackingConfigurationDAO(getActivity()).getByProjectUuid(project.getUuid()).get();
    }

    private List<TrackingRecord> getTrackingRecordsByProject(Project project) {
        return new TrackingRecordDAO(getActivity()).getByProjectUuid(project.getUuid());
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
                        new DeleteProjectTask(getActivity()).withProjectUuid(project.getUuid()).run();
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
