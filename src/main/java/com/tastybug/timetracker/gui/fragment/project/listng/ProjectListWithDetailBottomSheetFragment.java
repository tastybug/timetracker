package com.tastybug.timetracker.gui.fragment.project.listng;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.activity.ProjectTrackingLogActivity;
import com.tastybug.timetracker.gui.dialog.project.ProjectCreationDialog;
import com.tastybug.timetracker.gui.eventhandler.AbstractOttoEventHandler;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.testdata.TestdataGeneratedEvent;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

public class ProjectListWithDetailBottomSheetFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private UpdateProjectListOnTrackingEventsHandler updateProjectListOnTrackingEventsHandler;
    private TestDataCreationHandler testDataCreationHandler;

    private ListView listView;
    private ProjectDetailsBottomSheet projectDetailsBottomSheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_list_with_detail_bottom_sheet, container);

        listView = (ListView) view.findViewById(R.id.project_list);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        projectDetailsBottomSheet = new ProjectDetailsBottomSheet(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listView.setAdapter(new ProjectListAdapter(getActivity()));
        hideListSeparators();

        updateProjectListOnTrackingEventsHandler = new UpdateProjectListOnTrackingEventsHandler();
        testDataCreationHandler = new TestDataCreationHandler();
    }

    private void hideListSeparators() {
        listView.setDivider(null);
        listView.setDividerHeight(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateProjectListOnTrackingEventsHandler.stop();
        testDataCreationHandler.stop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showProjectDetailsBottomSheet((Project) listView.getAdapter().getItem(position));
    }

    private void showProjectDetailsBottomSheet(Project project) {
        projectDetailsBottomSheet.showProject(getActivity(), project);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        goTrackingLogs((Project) listView.getAdapter().getItem(position));
        return true;
    }

    private void goTrackingLogs(Project project) {
        Intent intent = new Intent(getActivity(), ProjectTrackingLogActivity.class);
        intent.putExtra(ProjectTrackingLogActivity.PROJECT_UUID, project.getUuid());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_project_list_with_detail_bottom_sheet, menu);
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

    class UpdateProjectListOnTrackingEventsHandler extends AbstractOttoEventHandler {

        @Subscribe
        public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
            ((ProjectListAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

        @Subscribe public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
            ((ProjectListAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

        @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
            ((ProjectListAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

        @Subscribe public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
            ((ProjectListAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
    }

    class TestDataCreationHandler extends AbstractOttoEventHandler {

        @Subscribe public void handleTestdataGenerated(TestdataGeneratedEvent event) {
            Toast.makeText(getActivity(), "DEBUG: Testdaten generiert!", Toast.LENGTH_LONG).show();
            listView.setAdapter(new ProjectListAdapter(getActivity()));
        }
    }
}
