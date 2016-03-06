package com.tastybug.timetracker.gui.trackingrecord.edit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.shared.DialogConfirmDeleteTrackingRecord;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreateTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.DeletedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifyTrackingRecordTask;

public class TrackingRecordModificationActivity extends Activity {

    public static final String PROJECT_UUID = "PROJECT_UUID";
    public static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";

    private Optional<String> projectUuidOpt = Optional.absent();
    private Optional<String> trackingRecordUuidOpt = Optional.absent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_record_editing);
        setupActionBar();
        setOrRestoreState(savedInstanceState);
    }

    private void setOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuidOpt = Optional.fromNullable(savedInstanceState.getString(PROJECT_UUID));
            trackingRecordUuidOpt = Optional.fromNullable(savedInstanceState.getString(TRACKING_RECORD_UUID));
        } else {
            Intent intent = getIntent();
            projectUuidOpt = Optional.fromNullable(intent.getStringExtra(PROJECT_UUID));
            trackingRecordUuidOpt = Optional.fromNullable(intent.getStringExtra(TRACKING_RECORD_UUID));
        }
        Preconditions.checkArgument(projectUuidOpt.isPresent() || trackingRecordUuidOpt.isPresent(), "Neither project uuid nor tracking record uuid available.");

        if(trackingRecordUuidOpt.isPresent()) {
            setTitle(R.string.title_tracking_record_editing);
            renderExistingTrackingRecord(getTrackingRecordByUuid(trackingRecordUuidOpt.get()));
        } else {
            setTitle(R.string.title_tracking_record_creation);
            renderTrackingRecordCreation(projectUuidOpt.get());
        }
    }

    private void renderExistingTrackingRecord(TrackingRecord trackingRecord) {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();
        configurationFragment.showTrackingRecordData(trackingRecord);
    }

    private void renderTrackingRecordCreation(String forProjectUuid) {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();
        configurationFragment.showCreationForProject(forProjectUuid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.activity_tracking_record_editing, menu);
        menu.findItem(R.id.menu_delete_tracking_record).setVisible(trackingRecordUuidOpt.isPresent());

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(trackingRecordUuidOpt.isPresent()) {
            outState.putString(TRACKING_RECORD_UUID, trackingRecordUuidOpt.get());
        } else {
            outState.putString(PROJECT_UUID, projectUuidOpt.get());
        }
    }

    protected TrackingRecord getTrackingRecordByUuid(String uuid) {
        return new TrackingRecordDAO(this).get(uuid).get();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirm_tracking_record_edit:
                if (isConfigurationValid()) {
                    if (trackingRecordUuidOpt.isPresent()) {
                        ModifyTrackingRecordTask task = buildTrackingRecordModificationTask();
                        task.execute();
                    } else {
                        CreateTrackingRecordTask task = buildTrackingRecordCreationTask();
                        task.execute();
                    }
                }
                return true;
            case R.id.menu_delete_tracking_record:
                deleteTrackingRecord(trackingRecordUuidOpt.get());
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void deleteTrackingRecord(String trackingRecordUuid) {
        DialogConfirmDeleteTrackingRecord
                .aDialog()
                .forTrackingRecordUuid(trackingRecordUuid)
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    private boolean isConfigurationValid() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();
        return configurationFragment.validateData();
    }

    private ModifyTrackingRecordTask buildTrackingRecordModificationTask() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();

        ModifyTrackingRecordTask task = ModifyTrackingRecordTask.aTask(this).withTrackingRecordUuid(trackingRecordUuidOpt.get());
        task = configurationFragment.collectModificationsForEdit(task);

        return task;
    }

    private CreateTrackingRecordTask buildTrackingRecordCreationTask() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();

        CreateTrackingRecordTask task = CreateTrackingRecordTask.aTask(this).withProjectUuid(projectUuidOpt.get());
        task = configurationFragment.collectModificationsForCreate(task);
        return task;
    }

    private TrackingRecordModificationFragment getTrackingRecordModificationFragment() {
        return (TrackingRecordModificationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_record_editing);
    }

    @Subscribe
    public void handleTrackingRecordCreatedEvent(CreatedTrackingRecordEvent event) {
        Toast.makeText(this, "Created tracking record " + event.getTrackingRecord().getUuid(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Subscribe
    public void handleTrackingRecordModifiedEvent(ModifiedTrackingRecordEvent event) {
        Toast.makeText(this, "Modified tracking record " + event.getTrackingRecord().getUuid(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Subscribe
    public void handleTrackingRecordDeletedEvent(DeletedTrackingRecordEvent event) {
        Toast.makeText(this, "Deleted tracking record " + event.getTrackingRecordUuid(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
