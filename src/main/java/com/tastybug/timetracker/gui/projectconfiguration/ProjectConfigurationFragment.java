package com.tastybug.timetracker.gui.projectconfiguration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

public class ProjectConfigurationFragment extends Fragment {

    private EditText projectTitleEditText;
    private EditText projectDescriptionEditText;

    private Project currentProject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_project_configuration, container);

        projectTitleEditText = (EditText) rootview.findViewById(R.id.project_title);
        projectDescriptionEditText = (EditText) rootview.findViewById(R.id.project_description);

        return rootview;
    }

    public void showProject(Project project) {
        this.currentProject = project;

        projectTitleEditText.setText(project.getTitle());
        projectDescriptionEditText.setText(project.getDescription().isPresent() ? project.getDescription().get() : "");
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
