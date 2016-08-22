package com.tastybug.timetracker.ui.dashboard;

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
import com.tastybug.timetracker.BuildConfig;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.backup.in.BackupRestoredEvent;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.testdata.TestDataGenerationTask;
import com.tastybug.timetracker.task.testdata.TestdataGeneratedEvent;
import com.tastybug.timetracker.task.tracking.CheckInEvent;
import com.tastybug.timetracker.task.tracking.CheckOutEvent;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;
import com.tastybug.timetracker.ui.core.AbstractOttoEventHandler;
import com.tastybug.timetracker.ui.dialog.project.ProjectCreationDialog;
import com.tastybug.timetracker.ui.projectdetails.ProjectDetailsActivity;
import com.tastybug.timetracker.ui.settings.SettingsActivity;

public class ProjectListFragment extends ListFragment {

    private UpdateProjectListOnTrackingEventsHandler updateProjectListOnTrackingEventsHandler;
    private TestDataCreationHandler testDataCreationHandler;
    private BackupRestoredHandler backupRestoredHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ProjectListAdapter(getActivity()));

        updateProjectListOnTrackingEventsHandler = new UpdateProjectListOnTrackingEventsHandler();
        testDataCreationHandler = new TestDataCreationHandler();
        backupRestoredHandler = new BackupRestoredHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateProjectListOnTrackingEventsHandler.stop();
        testDataCreationHandler.stop();
        backupRestoredHandler.stop();
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
        if (BuildConfig.DEBUG) {
            menu.add(Menu.NONE, R.id.menu_item_generate_testdata, 100, getString(R.string.menu_item_generate_testdata));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_project:
                showProjectCreationDialog();
                return true;
            case R.id.menu_application_settings:
                showSettingsActivity();
                return true;
            case R.id.menu_item_generate_testdata:
                generateTestdata();
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

    private void generateTestdata() {
        new TestDataGenerationTask(getActivity()).execute();
    }

    private void showSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    class UpdateProjectListOnTrackingEventsHandler extends AbstractOttoEventHandler {

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
        public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void handleCheckOut(CheckOutEvent event) {
            ((ProjectListAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    class BackupRestoredHandler extends AbstractOttoEventHandler {

        @SuppressWarnings("unused")
        @Subscribe
        public void handleBackupRestored(BackupRestoredEvent event) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.msg_backup_has_been_restored, Toast.LENGTH_LONG).show();
                    setListAdapter(new ProjectListAdapter(getActivity()));
                }
            });
        }
    }

    class TestDataCreationHandler extends AbstractOttoEventHandler {

        @SuppressWarnings("unused")
        @Subscribe
        public void handleTestdataGenerated(TestdataGeneratedEvent event) {
            Toast.makeText(getActivity(), "DEBUG: Testdaten generiert!", Toast.LENGTH_LONG).show();
            setListAdapter(new ProjectListAdapter(getActivity()));
        }
    }
}
