package com.tastybug.timetracker.gui.fragment.project.listng;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;

import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectDetailsBottomSheet {

    private TextView projectTitleTextView;
    private TextView projectTimeFrameTextView;
    private TextView projectDurationTextView;

    BottomSheetBehavior mBottomSheetBehavior;

    public ProjectDetailsBottomSheet(View rootView) {

        View bottomSheet = rootView.findViewById( R.id.project_details_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        projectTitleTextView = (TextView) rootView.findViewById(R.id.project_title);
        projectTimeFrameTextView = (TextView) rootView.findViewById(R.id.project_info_time_frame);
        projectDurationTextView = (TextView) rootView.findViewById(R.id.project_info_current_project_duration);
    }

    public void showProject(Context context, Project project) {
        showBottomSheetFully();
        projectTitleTextView.setText(project.getTitle());

        renderProjectTimeFrame(context, Optional.of(project));
        renderProjectDuration(context, Optional.of(project));
    }

    private void showBottomSheetFully() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void registerBehaviourCallback(BottomSheetBehavior.BottomSheetCallback callback) {
        mBottomSheetBehavior.setBottomSheetCallback(callback);
    }

    public void renderProjectTimeFrame(Context context, Optional<Project> project) {
        if (project.isPresent()) {
            TrackingConfiguration configuration = project.get().getTrackingConfiguration(context);
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

    public void renderProjectDuration(Context context, Optional<Project> projectOpt) {
        if (!projectOpt.isPresent()) {
            projectDurationTextView.setText("");
            return;
        }

        Project project = projectOpt.get();
        TrackingConfiguration configuration = project.getTrackingConfiguration(context);
        Duration duration = new StatisticProjectDuration(configuration, project.getTrackingRecords(context)).get();
        if (configuration.getHourLimit().isPresent()) {
            if (duration.getStandardHours() < 1) {
                projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far_from_a_total_of_Y,
                        "<1",
                        configuration.getHourLimit().get()));
            } else {
                projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far_from_a_total_of_Y,
                        duration.getStandardHours(),
                        configuration.getHourLimit().get()));
            }
        } else {
            if (duration.getMillis() == 0) {
                projectDurationTextView.setText(R.string.nothing_recorded_yet);
            } else {
                if (duration.getStandardHours() < 1) {
                    projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far,
                            "<1"));
                } else {
                    projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far,
                            duration.getStandardHours()));
                }
            }
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
