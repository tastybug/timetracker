package com.tastybug.timetracker.ui.dialog.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.task.project.create.CreateProjectTask;
import com.tastybug.timetracker.task.project.create.ProjectCreatedEvent;
import com.tastybug.timetracker.ui.projectconfiguration.ProjectConfigurationActivity;
import com.tastybug.timetracker.ui.projectdetails.ProjectDetailsActivity;

public class ProjectCreationDialog extends DialogFragment {

    private static String TITLE_OPT = "TITLE_OPT";

    private EditText titleEditText;
    private boolean openConfigurationAfterCreation = false;

    private Optional<String> existingTitle = Optional.absent();

    public ProjectCreationDialog() {
        new OttoProvider().getSharedBus().register(this);
    }

    public static ProjectCreationDialog aDialog() {
        return new ProjectCreationDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            existingTitle = (Optional<String>) savedInstanceState.getSerializable(TITLE_OPT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(prepareView(existingTitle))
                .setTitle(R.string.title_project_creation_dialog)
                .setPositiveButton(R.string.button_just_create_project, null)
                .setNeutralButton(R.string.button_open_configuration_after_project_creation, null)
                .setNegativeButton(R.string.common_close, null);
        final AlertDialog alertDialog = builder.create();
        fixDialogButtonsDontDismiss(alertDialog);
        return alertDialog;
    }

    private void fixDialogButtonsDontDismiss(final AlertDialog alertDialog) {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {

                            public void onClick(View view) {
                                if (hasValidInput()) {
                                    startProjectCreation();
                                }
                            }
                        });
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setOnClickListener(new View.OnClickListener() {

                            public void onClick(View view) {
                                if (hasValidInput()) {
                                    startProjectCreation();
                                }
                                openConfigurationAfterCreation = true;
                            }
                        });
            }
        });
    }

    private boolean hasValidInput() {
        return getTitleFromWidget(true).isPresent();
    }

    private void startProjectCreation() {
        new CreateProjectTask(getActivity())
                .withProjectTitle(getTitleFromWidget(false).get())
                .run();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectCreatedEvent(ProjectCreatedEvent event) {
        if (openConfigurationAfterCreation) {
            openProjectConfigurationActivity(event.getProject().getUuid());
        } else {
            openProjectDetailsActivity(event.getProject().getUuid());
        }
        dismiss();
    }

    private void openProjectConfigurationActivity(String projectUuid) {
        Intent intent = new Intent(getActivity(), ProjectConfigurationActivity.class);
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, projectUuid);
        startActivity(intent);
    }

    private void openProjectDetailsActivity(String projectUuid) {
        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
        intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, projectUuid);
        startActivity(intent);
    }

    private View prepareView(Optional<String> titleOpt) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_project_creation, null);
        titleEditText = (EditText) rootView.findViewById(R.id.project_title);

        renderExistingTitle(titleOpt);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TITLE_OPT, getTitleFromWidget(false));
    }

    private void renderExistingTitle(Optional<String> titleOpt) {
        if (titleOpt.isPresent()) {
            titleEditText.setText(titleOpt.get());
        } else {
            titleEditText.setText("");
        }
    }

    private Optional<String> getTitleFromWidget(boolean blame) {
        Optional<String> titleOpt = TextUtils.isEmpty(titleEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(titleEditText.getText().toString());
        if (blame && !titleOpt.isPresent()) {
            titleEditText.setError(getString(R.string.error_project_title_missing));
        }
        return titleOpt;
    }
}
