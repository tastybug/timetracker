package com.tastybug.timetracker.gui.project.detail;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.dialog.EditTrackingRecordDescriptionDialogFragment;
import com.tastybug.timetracker.gui.trackingrecord.edit.TrackingRecordModificationActivity;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

public class TrackingRecordListFragment extends ListFragment {

    private static final String PROJECT_UUID_OPT = "PROJECT_UUID_OPT";

    private Handler uiUpdateHandler = new Handler();
    private Optional<String> projectUuidOpt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            projectUuidOpt = (Optional<String>)savedInstanceState.getSerializable(PROJECT_UUID_OPT);
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setBackgroundDrawable( getResources().getDrawable(R.drawable.white_shadowbox) );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PROJECT_UUID_OPT, projectUuidOpt);
    }

    public void showProject(String projectUuid) {
        projectUuidOpt = Optional.of(projectUuid);
        setListAdapter(new TrackingRecordListAdapter(getActivity(), projectUuid));
    }

    public void showNoProject() {
        projectUuidOpt = Optional.absent();
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        TrackingRecord selectedTrackingRecord = (TrackingRecord) listView.getAdapter().getItem(position);
        showTrackingRecordDescriptionEditingDialog(selectedTrackingRecord);
    }

    private void showTrackingRecordDescriptionEditingDialog(TrackingRecord trackingRecord) {
        EditTrackingRecordDescriptionDialogFragment
                .aDialog()
                .forTrackingRecord(trackingRecord)
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    private void showTrackingRecordCreationActivity() {
        Intent intent = new Intent(getActivity(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.PROJECT_UUID, projectUuidOpt.get());
        startActivity(intent);
    }

    @Subscribe
    public void handleTrackingRecordCreatedEvent(CreatedTrackingRecordEvent event) {
        if (projectUuidOpt.isPresent()
                && projectUuidOpt.get().equals(event.getTrackingRecord().getProjectUuid())) {
            ((TrackingRecordListAdapter) getListAdapter()).rebuildModel(event.getTrackingRecord().getProjectUuid());
        }
    }

    @Subscribe public void handleTrackingRecordModifiedEvent(ModifiedTrackingRecordEvent event) {
        if (projectUuidOpt.isPresent()
                && projectUuidOpt.get().equals(event.getTrackingRecord().getProjectUuid())) {
            ((TrackingRecordListAdapter) getListAdapter()).rebuildModel(event.getTrackingRecord().getProjectUuid());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_tracking_record_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_custom_tracking_record:
                showTrackingRecordCreationActivity();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(projectUuidOpt.isPresent()) {
            showProject(projectUuidOpt.get());
        } else {
            showNoProject();
        }
        //
        uiUpdateHandler.removeCallbacks(updateUITask);
        uiUpdateHandler.postDelayed(updateUITask, 100);

        //
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //
        uiUpdateHandler.removeCallbacks(updateUITask);

        //
        new OttoProvider().getSharedBus().unregister(this);
    }

    private Runnable updateUITask = new Runnable() {
        public void run() {
            if (getListAdapter() != null) {
                ((TrackingRecordListAdapter)getListAdapter()).notifyDataSetChanged();
            }
            uiUpdateHandler.postDelayed(updateUITask, 1000);
        }
    };
}
