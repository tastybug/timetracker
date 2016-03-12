package com.tastybug.timetracker.gui.project.configuration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

public class ProjectConfigurationFragment extends Fragment {

    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";

    private ProjectConfigurationUI ui;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ui = new ProjectConfigurationUI(getActivity());
        View view = ui.inflateWidgets(inflater, container);

        if (savedInstanceState != null) {
            ui.showProjectData(savedInstanceState.getString(PROJECT_TITLE),
                    Optional.fromNullable(savedInstanceState.getString(PROJECT_DESCRIPTION)));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PROJECT_TITLE, ui.getTitleFromWidget(false).orNull());
        outState.putString(PROJECT_DESCRIPTION, ui.getDescriptionFromWidget().orNull());
    }

    public void showProject(Project project) {
        ui.showProjectData(project.getTitle(), project.getDescription());
    }

    public boolean validateSettings() {
        return ui.getTitleFromWidget(true).isPresent();
    }

    public void collectModifications(ConfigureProjectTask task) {
        Optional<String> newTitle = ui.getTitleFromWidget(true);
        Optional<String> newDescription = ui.getDescriptionFromWidget();

        task.withProjectTitle(newTitle.get());
        task.withProjectDescription(newDescription.orNull());
    }

    public boolean hasUnsavedModifications(Project project) {
        return !project.getTitle().equals(ui.getTitleFromWidget(false).orNull())
                || !project.getDescription().equals(ui.getDescriptionFromWidget());
    }

}
