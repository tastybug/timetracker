package com.tastybug.timetracker.core.ui.dashboard;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.task.project.create.ProjectCreatedEvent;
import com.tastybug.timetracker.core.task.tracking.checkin.CheckInEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;
import com.tastybug.timetracker.core.ui.core.AbstractOttoEventHandler;
import com.tastybug.timetracker.core.ui.dialog.project.ProjectCreationDialog;
import com.tastybug.timetracker.core.ui.projectdetails.ProjectDetailsActivity;
import com.tastybug.timetracker.core.ui.settings.SettingsActivity;

public class ProjectListFragment extends ListFragment {

    private DomainEventsHandler domainEventsHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ProjectListAdapter(getActivity()));

        domainEventsHandler = new DomainEventsHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        domainEventsHandler.stop();
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
            case R.id.menu_item_create_project:
                showProjectCreationDialog();
                return true;
            case R.id.menu_item_settings:
                showSettingsActivity();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void showProjectCreationDialog() {
        ProjectCreationDialog
                .aDialog()
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    private void showSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private class DomainEventsHandler extends AbstractOttoEventHandler {

        @SuppressWarnings("unused")
        @Subscribe
        public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void handleCheckIn(CheckInEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void handleTrackingRecordUpdate(UpdateTrackingRecordEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void handleCheckOut(CheckOutEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void handleProjectCreated(ProjectCreatedEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }
}
