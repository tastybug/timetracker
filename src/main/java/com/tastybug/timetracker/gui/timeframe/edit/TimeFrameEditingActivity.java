package com.tastybug.timetracker.gui.timeframe.edit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.ModifyTimeFrameTask;
import com.tastybug.timetracker.task.tracking.TimeFrameModifiedEvent;

public class TimeFrameEditingActivity extends Activity {

    public static final String TIME_FRAME_UUID = "TIME_FRAME_UUID";

    private String timeFrameUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame_editing);
        setupActionBar();
        setOrRestoreState(savedInstanceState);
    }

    private void setOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            timeFrameUuid = savedInstanceState.getString(TIME_FRAME_UUID);
        } else {
            Intent intent = getIntent();
            timeFrameUuid = intent.getStringExtra(TIME_FRAME_UUID);
        }

        TimeFrame timeFrame = getTimeFrameByUuid(timeFrameUuid);

        setTitle(R.string.title_time_frame_editing);

        TimeFrameEditingFragment configurationFragment = getTimeFrameEditingFragment();
        configurationFragment.showTimeFrameData(timeFrame);
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
        outState.putString(TIME_FRAME_UUID, timeFrameUuid);
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
                    ModifyTimeFrameTask task = buildProjectConfigurationTask();
                    task.execute();
                }
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private boolean isConfigurationValid() {
        TimeFrameEditingFragment configurationFragment = getTimeFrameEditingFragment();
        return configurationFragment.validateData();
    }

    private ModifyTimeFrameTask buildProjectConfigurationTask() {
        TimeFrameEditingFragment configurationFragment = getTimeFrameEditingFragment();

        ModifyTimeFrameTask task = ModifyTimeFrameTask.aTask(this).withTimeFrameUuid(timeFrameUuid);
        configurationFragment.collectModifications(task);

        return task;
    }

    private TimeFrameEditingFragment getTimeFrameEditingFragment() {
        return (TimeFrameEditingFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_time_frame_editing);
    }

    @Subscribe
    public void handleTimeFrameModifiedEvent(TimeFrameModifiedEvent event) {
        onBackPressed();
    }
}
