package com.tastybug.timetracker.gui.project.detail;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.trackingrecord.edit.TrackingRecordModificationActivity;
import com.tastybug.timetracker.model.TrackingRecord;

public class TrackingRecordListFragment extends ListFragment {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private Handler uiUpdateHandler = new Handler();
    private String projectUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    public void showProject(String projectUuid) {
        this.projectUuid = projectUuid;
        setListAdapter(new TrackingRecordListAdapter(getActivity(), projectUuid));
    }

    public void showNoProject() {
        this.projectUuid = null;
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        TrackingRecord selectedTrackingRecord = (TrackingRecord) listView.getAdapter().getItem(position);
        showTrackingRecordEditingActivity(selectedTrackingRecord);
    }

    private void showTrackingRecordEditingActivity(TrackingRecord trackingRecord) {
        Intent intent = new Intent(getActivity(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.TRACKING_RECORD_UUID, trackingRecord.getUuid());
        startActivity(intent);
    }

    private void showTrackingRecordCreationActivity() {
        Intent intent = new Intent(getActivity(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.PROJECT_UUID, projectUuid);
        startActivity(intent);
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
        if(TextUtils.isEmpty(projectUuid)) {
            showNoProject();
        } else {
            showProject(projectUuid);
        }
        uiUpdateHandler.removeCallbacks(updateUITask);
        uiUpdateHandler.postDelayed(updateUITask, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiUpdateHandler.removeCallbacks(updateUITask);
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
