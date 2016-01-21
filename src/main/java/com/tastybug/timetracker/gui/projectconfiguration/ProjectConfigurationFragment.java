package com.tastybug.timetracker.gui.projectconfiguration;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

public class ProjectConfigurationFragment extends Fragment {

    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";

    private EditText projectTitleEditText;
    private EditText projectDescriptionEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_configuration, container);

        projectTitleEditText = (EditText) view.findViewById(R.id.project_title);
        projectDescriptionEditText = (EditText) view.findViewById(R.id.project_description);

        if (savedInstanceState != null) {
            showProjectData(savedInstanceState.getString(PROJECT_TITLE),
                    savedInstanceState.getString(PROJECT_DESCRIPTION));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PROJECT_TITLE, getTitleFromWidget());
        outState.putString(PROJECT_DESCRIPTION, getDescriptionFromWidget());
    }

    public void showProject(Project project) {
        showProjectData(project.getTitle(), project.getDescription().orNull());
    }

    private void showProjectData(String title, String description) {
        projectTitleEditText.setText(title);
        projectDescriptionEditText.setText(description != null ? description : "");
    }

    public boolean validateSettings() {
        if (TextUtils.isEmpty(getTitleFromWidget())) {
            projectTitleEditText.setError(getString(R.string.error_project_title_empty));
            return false;
        } else {
            projectTitleEditText.setError(null);
            return true;
        }
    }

    public void collectModifications(ConfigureProjectTask task) {
        String newTitle = getTitleFromWidget();
        String newDescription = getDescriptionFromWidget();

        task.withProjectTitle(newTitle);
        task.withProjectDescription(newDescription);
    }

    private String getTitleFromWidget() {
        return projectTitleEditText.getText().toString();
    }

    private String getDescriptionFromWidget() {
        return projectDescriptionEditText.getText().toString();
    }

}
