package com.tastybug.timetracker.core.ui.trackingrecordmodification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.tracking.create.CreateTrackingRecordTask;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.delete.DeletedTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordTask;
import com.tastybug.timetracker.core.ui.core.BaseActivity;
import com.tastybug.timetracker.core.ui.dialog.navigation.ConfirmBackpressDialogFragment;
import com.tastybug.timetracker.core.ui.dialog.trackingrecord.ConfirmDeleteTrackingRecordDialogFragment;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class TrackingRecordModificationActivity extends BaseActivity {

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

        if (trackingRecordUuidOpt.isPresent()) {
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
        MenuInflater inflater = getMenuInflater();
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
        if (trackingRecordUuidOpt.isPresent()) {
            outState.putString(TRACKING_RECORD_UUID, trackingRecordUuidOpt.get());
        } else {
            outState.putString(PROJECT_UUID, projectUuidOpt.get());
        }
    }

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirm_tracking_record_edit:
                performSaveModifications();
                return true;
            case R.id.menu_delete_tracking_record:
                deleteTrackingRecord(trackingRecordUuidOpt.get());
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void performSaveModifications() {
        if (isConfigurationValid()) {
            if (trackingRecordUuidOpt.isPresent()) {
                UpdateTrackingRecordTask task = buildTrackingRecordModificationTask();
                task.run();
            } else {
                CreateTrackingRecordTask task = buildTrackingRecordCreationTask();
                task.run();
            }
        }
    }

    private void deleteTrackingRecord(String trackingRecordUuid) {
        ConfirmDeleteTrackingRecordDialogFragment
                .aDialog()
                .forTrackingRecordUuid(trackingRecordUuid)
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    private boolean isConfigurationValid() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();
        return configurationFragment.validateData();
    }

    private UpdateTrackingRecordTask buildTrackingRecordModificationTask() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();

        UpdateTrackingRecordTask task = new UpdateTrackingRecordTask(this).withTrackingRecordUuid(trackingRecordUuidOpt.get());
        task = configurationFragment.collectModificationsForEdit(task);

        return task;
    }

    private CreateTrackingRecordTask buildTrackingRecordCreationTask() {
        TrackingRecordModificationFragment configurationFragment = getTrackingRecordModificationFragment();

        CreateTrackingRecordTask task = new CreateTrackingRecordTask(this).withProjectUuid(projectUuidOpt.get());
        task = configurationFragment.collectModificationsForCreate(task);
        return task;
    }

    private TrackingRecordModificationFragment getTrackingRecordModificationFragment() {
        return (TrackingRecordModificationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_record_editing);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordCreatedEvent(CreatedTrackingRecordEvent event) {
        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordUpdate(UpdateTrackingRecordEvent event) {
        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordDeletedEvent(DeletedTrackingRecordEvent event) {
        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleSaveThenBackpressRequestedEvent(ConfirmBackpressDialogFragment.SaveThenBackpressRequestedEvent event) {
        performSaveModifications();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleDiscardThenBackpressRequestedEvent(ConfirmBackpressDialogFragment.DiscardThenBackpressRequestedEvent event) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (hasFragmentWithUnsavedModifications()) {
            showConfirmBackpressLossDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasFragmentWithUnsavedModifications() {
        Optional<TrackingRecord> trackingRecordOptional = projectUuidOpt.isPresent()
                ? Optional.<TrackingRecord>absent()
                : new TrackingRecordDAO(this).get(trackingRecordUuidOpt.get());
        return getTrackingRecordModificationFragment().hasUnsavedModifications(trackingRecordOptional);
    }

    private void showConfirmBackpressLossDialog() {
        ConfirmBackpressDialogFragment
                .aDialog()
                .forEntityUuid(trackingRecordUuidOpt.isPresent() ? trackingRecordUuidOpt.get() : projectUuidOpt.get())
                .show(getFragmentManager(), getClass().getSimpleName());
    }
}
