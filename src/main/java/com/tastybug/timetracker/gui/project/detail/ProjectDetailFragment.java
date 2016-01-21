package com.tastybug.timetracker.gui.project.detail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.project.configuration.ProjectConfigurationActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.project.DeleteProjectTask;

public class ProjectDetailFragment extends Fragment {

    private TextView someTextView;
    private Project currentProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_project_details, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_project_detail, container);

        someTextView = (TextView) rootview.findViewById(R.id.someTextview);

        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_project:
                DeleteProjectTask.aTask(getActivity()).withProjectUuid(currentProject.getUuid()).execute();
                return true;
            case R.id.menu_configure_project:
                showProjectConfigurationActivity();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void showProjectConfigurationActivity() {
        Intent intent = new Intent(getActivity(), ProjectConfigurationActivity.class);
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, currentProject.getUuid());
        startActivity(intent);
    }

    public void showProjectDetailsFor(Project project) {
        this.currentProject = project;
        someTextView.setText(currentProject.toString());
    }

    public void showNoProject() {
        this.currentProject = null;
        someTextView.setText("//Nothing selected");
    }

}
