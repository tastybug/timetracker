package com.tastybug.timetracker.gui.fragment.project.list;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.activity.ProjectDetailsActivity;
import com.tastybug.timetracker.gui.dialog.project.ProjectCreationDialog;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.testdata.TestdataGeneratedEvent;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

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
        new OttoProvider().getSharedBus().register(this);
        hideListSeparators();
    }

    private void hideListSeparators() {
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        showProjectDetails((Project) listView.getAdapter().getItem(position));
    }

    private void showProjectDetails(Project project) {
        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
        intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        startActivity(intent);
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
                ProjectCreationDialog
                        .aDialog()
                        .show(getFragmentManager(), getClass().getSimpleName());
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    @Subscribe
    public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
        ((ProjectListAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Subscribe public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
        ((ProjectListAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
        ((ProjectListAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Subscribe public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        ((ProjectListAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Subscribe public void handleTestdataGenerated(TestdataGeneratedEvent event) {
        Toast.makeText(getActivity(), "DEBUG: Testdaten generiert!", Toast.LENGTH_LONG).show();
        setListAdapter(new ProjectListAdapter(getActivity()));
    }
}
