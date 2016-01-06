package com.tastybug.timetracker.gui.projects;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.project.CreateProjectTask;

import java.util.Date;

public class ProjectListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ProjectListAdapter(getActivity()));
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        ((ProjectListSelectionListener)getActivity())
                .onProjectSelected((Project) listView.getAdapter().getItem(position));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_project_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_project:
                CreateProjectTask.aTask(getActivity()).withProjectTitle(new Date().toString()).execute();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    public interface ProjectListSelectionListener {

        void onProjectSelected(Project project);
    }
}
