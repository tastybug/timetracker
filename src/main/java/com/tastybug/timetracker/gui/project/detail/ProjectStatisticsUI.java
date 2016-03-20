package com.tastybug.timetracker.gui.project.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;

import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectStatisticsUI {

    private TextView projectTimeFrameTextView, projectDurationTextView;
    private Context context;

    public ProjectStatisticsUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_project_statistics, container);

        projectTimeFrameTextView = (TextView) rootView.findViewById(R.id.project_info_time_frame);
        projectDurationTextView = (TextView) rootView.findViewById(R.id.project_info_current_project_duration);

        return rootView;
    }

    public void renderProjectTimeFrame(Optional<Project> project) {
        if (project.isPresent()) {
            TrackingConfiguration configuration = project.get().getTrackingConfiguration();
            if (configuration.getEnd().isPresent()) { // <- theres an end date that limits the time frame
                long remainingDays = getEffectiveRemainingProjectDays(configuration.getStart(), configuration.getEnd().get());
                if (remainingDays == 0) {
                    projectTimeFrameTextView.setText(R.string.project_ends_today);
                } else {
                    String endDateString = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(configuration.getEndDateAsInclusive().get());
                    if (remainingDays < 0) {
                        projectTimeFrameTextView.setText(context.getString(R.string.project_ended_on_X, endDateString));
                    } else {
                        projectTimeFrameTextView.setText(context.getString(R.string.project_remaining_days_X_until_Y, remainingDays, endDateString));
                    }
                }
            } else {
                projectTimeFrameTextView.setText("");
            }
        } else {
            projectTimeFrameTextView.setText("");
        }
        projectTimeFrameTextView.setVisibility(TextUtils.isEmpty(projectTimeFrameTextView.getText()) ? View.GONE : View.VISIBLE);
    }

    public void renderProjectDuration(Optional<Project> projectOpt) {
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            TrackingConfiguration configuration = project.getTrackingConfiguration();
            Duration duration = new StatisticProjectDuration(configuration, project.getTrackingRecords()).get();
            if (configuration.getHourLimit().isPresent()) {
                projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far_from_a_total_of_Y,
                        duration.getStandardHours(),
                        configuration.getHourLimit().get()));
            } else {
                if (duration.getMillis() == 0) {
                    projectDurationTextView.setText(R.string.nothing_recorded_yet);
                } else {
                    projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far,
                            duration.getStandardHours()));
                }
            }
        } else {
            projectDurationTextView.setText("");
        }
    }

    private long getEffectiveRemainingProjectDays(Optional<Date> startDateOpt, Date endDateExclusive) {
        // if the start date lies in the future, only count from that date onwards
        // otherwise count from NOW
        Date start = startDateOpt.isPresent() && startDateOpt.get().after(new Date()) ? startDateOpt.get() : new Date();
        Duration duration = new Duration(start.getTime(), endDateExclusive.getTime());
        return duration.getStandardDays();
    }

}
