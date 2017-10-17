package com.tastybug.timetracker.core.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.core.ui.delegate.CheckInPreconditionCheckDelegate;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

public class ProjectView extends LinearLayout implements View.OnClickListener {

    private Project project;
    private ImageButton trackingStartStopButton;
    private TextView projectTitleView, recentRecordSummaryView;
    private ColorStateList regularTextColor;

    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_project, this, true);

        projectTitleView = findViewById(R.id.project_title);
        recentRecordSummaryView = findViewById(R.id.recent_record_summary);
        trackingStartStopButton = findViewById(R.id.trackingStartStop);

        regularTextColor = new TextView(context).getTextColors();

        trackingStartStopButton.setOnClickListener(this);
    }

    public void showProject(Project project,
                            Optional<TrackingRecord> lastTrackingRecordOpt) {
        this.project = project;
        renderProjectTitle(project);
        renderMostRecentTrackingRecord(lastTrackingRecordOpt);
        renderTrackingControlButton();
    }

    public void showClosedProject(Project project) {
        this.project = project;
        renderProjectTitle(project);
        renderMostRecentTrackingRecord(Optional.<TrackingRecord>absent());
        renderTrackingControlButton();
    }

    public void onClick(View v) {
        String projectUuid = project.getUuid();
        Optional<TrackingRecord> ongoing = new TrackingRecordDAO(getContext()).getRunning(projectUuid);
        if (ongoing.isPresent()) {
            new CheckOutTask(getContext()).withTrackingRecordUuid(ongoing.get().getUuid()).run();
        } else {
            CheckInPreconditionCheckDelegate.aDelegate((Activity) getContext()).startTracking(project);
        }
    }

    private void renderTrackingControlButton() {
        Optional<TrackingRecord> ongoingTracking = new TrackingRecordDAO(getContext()).getRunning(project.getUuid());
        if (ongoingTracking.isPresent()) {
            trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);
        } else {
            trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        }
        trackingStartStopButton.setVisibility(project.isClosed() ? View.INVISIBLE : View.VISIBLE);
    }

    private void renderProjectTitle(Project project) {
        projectTitleView.setText(project.getTitle());

    }

    private void renderMostRecentTrackingRecord(Optional<TrackingRecord> lastTrackingRecordOpt) {
        if (project.isClosed()) {
            recentRecordSummaryView.setText(R.string.project_closed);
            recentRecordSummaryView.setTextColor(regularTextColor);
        } else if (lastTrackingRecordOpt.isPresent()) {
            if (lastTrackingRecordOpt.get().isRunning()) {
                recentRecordSummaryView.setText(getContext().getString(R.string.current_record_started_at_X,
                        DefaultLocaleDateFormatter.dateTime().format(lastTrackingRecordOpt.get().getStart().get())));
                recentRecordSummaryView.setTextColor(getResources().getColor(R.color.start_date_color));
            } else {
                recentRecordSummaryView.setText(getContext().getString(R.string.last_record_ended_at_X,
                        DefaultLocaleDateFormatter.dateTime().format(lastTrackingRecordOpt.get().getEnd().get())));
                recentRecordSummaryView.setTextColor(regularTextColor);
            }
        } else {
            recentRecordSummaryView.setText("");
            recentRecordSummaryView.setTextColor(regularTextColor);
        }
    }
}
