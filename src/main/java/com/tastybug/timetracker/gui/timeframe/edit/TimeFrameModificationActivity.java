package com.tastybug.timetracker.gui.timeframe.edit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreateTimeFrameTask;
import com.tastybug.timetracker.task.tracking.ModifyTimeFrameTask;
import com.tastybug.timetracker.task.tracking.TimeFrameCreatedEvent;
import com.tastybug.timetracker.task.tracking.TimeFrameModifiedEvent;

public class TimeFrameModificationActivity extends Activity {

    public static final String PROJECT_UUID = "PROJECT_UUID";
    public static final String TIME_FRAME_UUID = "TIME_FRAME_UUID";

    private Optional<String> projectUuidOpt = Optional.absent();
    private Optional<String> timeFrameUuidOpt = Optional.absent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame_editing);
        setupActionBar();
        setOrRestoreState(savedInstanceState);
    }

    private void setOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuidOpt = Optional.fromNullable(savedInstanceState.getString(PROJECT_UUID));
            timeFrameUuidOpt = Optional.fromNullable(savedInstanceState.getString(TIME_FRAME_UUID));
        } else {
            Intent intent = getIntent();
            projectUuidOpt = Optional.fromNullable(intent.getStringExtra(PROJECT_UUID));
            timeFrameUuidOpt = Optional.fromNullable(intent.getStringExtra(TIME_FRAME_UUID));
        }
        Preconditions.checkArgument(projectUuidOpt.isPresent() || timeFrameUuidOpt.isPresent(), "Neither project uuid nor time frame uuid available.");

        if(timeFrameUuidOpt.isPresent()) {
            setTitle(R.string.title_time_frame_editing);
            renderExistingTimeFrame(getTimeFrameByUuid(timeFrameUuidOpt.get()));
        } else {
            setTitle(R.string.title_time_frame_creation);
            renderTimeFrameCreation(projectUuidOpt.get());
        }
    }

    private void renderExistingTimeFrame(TimeFrame timeFrame) {
        TimeFrameModificationFragment configurationFragment = getTimeFrameEditingFragment();
        configurationFragment.showTimeFrameData(timeFrame);
    }

    private void renderTimeFrameCreation(String forProjectUuid) {
        TimeFrameModificationFragment configurationFragment = getTimeFrameEditingFragment();
        configurationFragment.showCreationForProject(forProjectUuid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.activity_time_frame_editing, menu);

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
        if(timeFrameUuidOpt.isPresent()) {
            outState.putString(TIME_FRAME_UUID, timeFrameUuidOpt.get());
        } else {
            outState.putString(PROJECT_UUID, projectUuidOpt.get());
        }
    }

    protected TimeFrame getTimeFrameByUuid(String uuid) {
        return new TimeFrameDAO(this).get(uuid).get();
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
            case R.id.menu_confirm_time_frame_edit:
                    if (isConfigurationValid()) {
                        if (timeFrameUuidOpt.isPresent()) {
                            ModifyTimeFrameTask task = buildTimeFrameModificationTask();
                            task.execute();
                        } else {
                            CreateTimeFrameTask task = buildTimeFrameCreationTask();
                            task.execute();
                        }
                    }
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private boolean isConfigurationValid() {
        TimeFrameModificationFragment configurationFragment = getTimeFrameEditingFragment();
        return configurationFragment.validateData();
    }

    private ModifyTimeFrameTask buildTimeFrameModificationTask() {
        TimeFrameModificationFragment configurationFragment = getTimeFrameEditingFragment();

        ModifyTimeFrameTask task = ModifyTimeFrameTask.aTask(this).withTimeFrameUuid(timeFrameUuidOpt.get());
        task = configurationFragment.collectModificationsForEdit(task);

        return task;
    }

    private CreateTimeFrameTask buildTimeFrameCreationTask() {
        TimeFrameModificationFragment configurationFragment = getTimeFrameEditingFragment();

        CreateTimeFrameTask task = CreateTimeFrameTask.aTask(this).withProjectUuid(projectUuidOpt.get());
        task = configurationFragment.collectModificationsForCreate(task);
        return task;
    }

    private TimeFrameModificationFragment getTimeFrameEditingFragment() {
        return (TimeFrameModificationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_time_frame_editing);
    }

    @Subscribe
    public void handleTimeFrameCreatedEvent(TimeFrameCreatedEvent event) {
        onBackPressed();
    }

    @Subscribe
    public void handleTimeFrameModifiedEvent(TimeFrameModifiedEvent event) {
        onBackPressed();
    }
}
