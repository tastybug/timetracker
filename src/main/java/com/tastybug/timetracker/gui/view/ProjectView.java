package com.tastybug.timetracker.gui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.delegate.TrackingDelegate;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.statistics.StatisticProjectCompletion;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProjectView extends LinearLayout implements View.OnClickListener {

    private Project project;
    private ImageButton trackingStartStopButton;
    private TextView projectTitleView, lastRecordSummaryView;
    private View projectAmountMeterContainer, projectAmountMeter1, projectAmountMeter2;

    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_project, this, true);

        projectTitleView = (TextView) findViewById(R.id.project_title);
        lastRecordSummaryView = (TextView) findViewById(R.id.project_last_tracking_record_summary);
        trackingStartStopButton = (ImageButton) findViewById(R.id.trackingStartStop);
        projectAmountMeterContainer = findViewById(R.id.project_amount_meter_container);
        projectAmountMeter1 = findViewById(R.id.project_amount_meter_1);
        projectAmountMeter2 = findViewById(R.id.project_amount_meter_2);

        trackingStartStopButton.setOnClickListener(this);
    }

    public void showProject(Project project,
                            Optional<TrackingRecord> lastTrackingRecordOpt,
                            ArrayList<TrackingRecord> trackingRecords,
                            TrackingConfiguration configuration) {
        this.project = project;
        renderProjectTitle(project);
        renderLastTrackingRecord(lastTrackingRecordOpt);
        renderProjectAmountMeter(configuration, trackingRecords);
        renderTrackingControlButton();
    }

    public void onClick(View v) {
        String projectUuid = project.getUuid();
        Optional<TrackingRecord> ongoing = new TrackingRecordDAO(getContext()).getRunning(projectUuid);
        if (ongoing.isPresent()) {
            KickStopTrackingRecordTask.aTask(getContext()).withProjectUuid(projectUuid).execute();
        } else {
            TrackingDelegate.aDelegate((Activity)getContext()).startTracking(project);
        }
    }

    private void renderTrackingControlButton() {
        Optional<TrackingRecord> ongoingTracking = new TrackingRecordDAO(getContext()).getRunning(project.getUuid());
        if(ongoingTracking.isPresent()) {
            trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);
        } else {
            trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        }
    }

    private void renderProjectTitle(Project project) {
        projectTitleView.setText(project.getTitle());

    }

    private void renderLastTrackingRecord(Optional<TrackingRecord> lastTrackingRecordOpt) {
        if(lastTrackingRecordOpt.isPresent()) {
            if (lastTrackingRecordOpt.get().isRunning()) {
                lastRecordSummaryView.setText(getContext().getString(R.string.current_record_started_at_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT).format(lastTrackingRecordOpt.get().getStart().get())));
            } else {
                lastRecordSummaryView.setText(getContext().getString(R.string.last_record_ended_at_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT).format(lastTrackingRecordOpt.get().getEnd().get())));
            }
        } else {
            lastRecordSummaryView.setText("");
        }
    }

    private void renderProjectAmountMeter(TrackingConfiguration configuration, ArrayList<TrackingRecord> trackingRecords) {
        if (!configuration.getHourLimit().isPresent()) {
            projectAmountMeterContainer.setVisibility(View.GONE);
        } else {
            StatisticProjectCompletion statistic = new StatisticProjectCompletion(configuration, trackingRecords, true);
            projectAmountMeterContainer.setVisibility(View.VISIBLE);
            projectAmountMeter1.setLayoutParams(new LinearLayout.LayoutParams(0, 20, statistic.getCompletionPercent().get().intValue()));
            projectAmountMeter2.setLayoutParams(new LinearLayout.LayoutParams(0, 20, statistic.isOverbooked() ? 0 : 100-statistic.getCompletionPercent().get().intValue()));
        }
    }
}
