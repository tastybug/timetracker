package com.tastybug.timetracker.gui.dashboard;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.dialog.ProjectCreationDialog;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
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
                ProjectCreationDialog
                        .aDialog()
                        .show(getFragmentManager(), getClass().getSimpleName());
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    public interface ProjectListSelectionListener {

        void onProjectSelected(Project project);
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
}
