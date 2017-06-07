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
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.ProjectDuration;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.core.ui.delegate.CheckInPreconditionCheckDelegate;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.Duration;

import java.util.Date;

public class ProjectView extends LinearLayout implements View.OnClickListener {

    private Project project;
    private ImageButton trackingStartStopButton;
    private TextView projectTitleView, recentRecordSummaryView;
    private TextView projectRemainingDaysLabel, projectRemainingDaysValue;
    private View projectRemainingDaysContainer, projectDurationContainer;
    private TextView projectDurationStatisticLabel, projectDurationStatisticValue;
    private ColorStateList regularTextColor;

    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_project, this, true);

        projectTitleView = (TextView) findViewById(R.id.project_title);
        recentRecordSummaryView = (TextView) findViewById(R.id.recent_record_summary);
        trackingStartStopButton = (ImageButton) findViewById(R.id.trackingStartStop);
        projectDurationContainer = findViewById(R.id.project_duration_container);
        projectDurationStatisticLabel = (TextView) findViewById(R.id.project_duration_statistic_label);
        projectDurationStatisticValue = (TextView) findViewById(R.id.project_duration_statistic_value);
        projectRemainingDaysContainer = findViewById(R.id.project_remaining_days_container);
        projectRemainingDaysLabel = (TextView) findViewById(R.id.project_remaining_days_label);
        projectRemainingDaysValue = (TextView) findViewById(R.id.project_remaining_days_value);

        regularTextColor = new TextView(context).getTextColors();

        trackingStartStopButton.setOnClickListener(this);
    }

    public void showProject(Project project,
                            Optional<TrackingRecord> lastTrackingRecordOpt,
                            TrackingConfiguration trackingConfiguration,
                            ProjectDuration projectDuration) {
        this.project = project;
        renderProjectTitle(project);
        renderMostRecentTrackingRecord(lastTrackingRecordOpt);
        renderTrackingControlButton();
        renderProjectRemainingTimeFrameInfo(trackingConfiguration);
        renderProjectDurationStatistic(trackingConfiguration, projectDuration.getDuration());
    }

    public void showClosedProject(Project project) {
        this.project = project;
        renderProjectTitle(project);
        renderMostRecentTrackingRecord(Optional.<TrackingRecord>absent());
        renderTrackingControlButton();
        hideProjectRemainingTimeFrameInfo();
        hideProjectDurationStatistic();
    }

    private void renderProjectRemainingTimeFrameInfo(TrackingConfiguration trackingConfiguration) {
        projectRemainingDaysContainer.setVisibility(View.VISIBLE);
        if (trackingConfiguration.getEnd().isPresent()) {
            long remainingDays = getEffectiveRemainingProjectDays(trackingConfiguration.getStart(),
                    trackingConfiguration.getEnd().get());
            if (remainingDays > 0) {
                projectRemainingDaysLabel.setText(R.string.label_remaining_days_until_date_Y);
                projectRemainingDaysValue.setText(getContext().getString(R.string.remaining_days_X_until_date_Y,
                        remainingDays,
                        DefaultLocaleDateFormatter.date().format(trackingConfiguration.getEnd().get())));
            } else {
                projectRemainingDaysLabel.setText(R.string.label_remaining_days_over);
                projectRemainingDaysValue.setText(getContext().getString(R.string.remaining_days_over_since_X,
                        DefaultLocaleDateFormatter.date().format(trackingConfiguration.getEnd().get())));
            }
            projectRemainingDaysContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideProjectRemainingTimeFrameInfo() {
        projectRemainingDaysContainer.setVisibility(View.GONE);
        projectRemainingDaysContainer.setVisibility(View.GONE);
    }

    private void renderProjectDurationStatistic(TrackingConfiguration configuration,
                                               Duration duration) {
        projectDurationContainer.setVisibility(View.VISIBLE);
        projectDurationStatisticLabel.setText(configuration.getHourLimit().isPresent()
                ? R.string.duration_label_X_of_Y
                : R.string.duration_label_X_no_max);
        if (configuration.getHourLimit().isPresent()) {
            if (duration.getStandardHours() < 1) {
                projectDurationStatisticValue.setText(getContext().getString(R.string.less_than_one_hour_of_X,
                        configuration.getHourLimit().get()));
            } else {
                projectDurationStatisticValue.setText(getContext().getString(R.string.duration_X_of_Y,
                        duration.getStandardHours(),
                        configuration.getHourLimit().get()));
            }
        } else {
            if (duration.getMillis() == 0) {
                projectDurationStatisticValue.setText(R.string.duration_zero);
            } else {
                if (duration.getStandardHours() < 1) {
                    projectDurationStatisticValue.setText(getContext().getString(R.string.less_than_one_hour));
                } else {
                    projectDurationStatisticValue.setText(getContext().getString(R.string.duration_X_no_max,
                            duration.getStandardHours()));
                }
            }
        }
    }

    private void hideProjectDurationStatistic() {
        projectDurationContainer.setVisibility(View.GONE);
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

    private long getEffectiveRemainingProjectDays(Optional<Date> startDateOpt, Date endDateExclusive) {
        // if the start date lies in the future, only count from that date onwards
        // otherwise count from NOW
        Date start = startDateOpt.isPresent() && startDateOpt.get().after(new Date()) ? startDateOpt.get() : new Date();
        Duration duration = new Duration(start.getTime(), endDateExclusive.getTime());
        return duration.getStandardDays() < 0 ? 0 : duration.getStandardDays();
    }

}
