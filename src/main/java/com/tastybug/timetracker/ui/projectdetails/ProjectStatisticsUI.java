package com.tastybug.timetracker.ui.projectdetails;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.statistics.StatisticProjectCompletion;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;
import com.tastybug.timetracker.model.statistics.StatisticProjectExpiration;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import org.joda.time.Duration;

public class ProjectStatisticsUI {

    private TextView projectTimeFrameTextView, projectDurationTextView;
    private ProgressBar timeframeCompletionProgressBar, durationCompletionProgressBar;
    private Context context;

    public ProjectStatisticsUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_project_statistics, container);

        projectTimeFrameTextView = (TextView) rootView.findViewById(R.id.project_info_time_frame);
        projectDurationTextView = (TextView) rootView.findViewById(R.id.project_info_current_project_duration);
        timeframeCompletionProgressBar = (ProgressBar) rootView.findViewById(R.id.completion_timeframe);
        durationCompletionProgressBar = (ProgressBar) rootView.findViewById(R.id.completion_duration);

        return rootView;
    }

    public void renderProjectDuration(Optional<Project> projectOpt) {
        renderProjectDurationText(projectOpt);
        renderProjectCompletionProgress(projectOpt);

    }

    private void renderProjectDurationText(Optional<Project> projectOpt) {
        if (!projectOpt.isPresent()) {
            projectDurationTextView.setText("");
            return;
        }
        TrackingConfiguration configuration = projectOpt.get().getTrackingConfiguration(context);
        Duration duration = new StatisticProjectDuration(configuration, projectOpt.get().getTrackingRecords(context)).getDuration();

        if (configuration.getHourLimit().isPresent()) {
            if (duration.getStandardHours() < 1) {
                projectDurationTextView.setText(context.getString(R.string.less_than_one_hour_recorded_from_a_total_of_Y,
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
                    projectDurationTextView.setText(R.string.less_than_one_hour_recorded_so_far);
                } else {
                    projectDurationTextView.setText(context.getString(R.string.X_recorded_hours_so_far,
                            duration.getStandardHours()));
                }
            }
        }
    }

    private void renderProjectCompletionProgress(Optional<Project> projectOpt) {
        if (!projectOpt.isPresent()) {
//            durationCompletionProgressBar.setVisibility(View.GONE);
            return;
        }
        TrackingConfiguration configuration = projectOpt.get().getTrackingConfiguration(context);
        StatisticProjectCompletion statisticProjectCompletion = new StatisticProjectCompletion(configuration, projectOpt.get().getTrackingRecords(context), true);

        durationCompletionProgressBar.setVisibility(View.VISIBLE);
        durationCompletionProgressBar.setProgress(statisticProjectCompletion.getCompletionPercent().or(0d).intValue());
        renderProjectCompletionProgressColoring(statisticProjectCompletion);
    }

    private void renderProjectCompletionProgressColoring(StatisticProjectCompletion statisticProjectCompletion) {
        if (statisticProjectCompletion.isOverbooked()) {
            durationCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.RED));
        } else if (statisticProjectCompletion.getCompletionPercent().or(0d).intValue() > 80) {
            durationCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.YELLOW));
        } else {
            durationCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.GREEN));
        }
    }

    public void renderProjectTimeFrame(Optional<Project> project) {
        if (project.isPresent()) {
            TrackingConfiguration trackingConfiguration = project.get().getTrackingConfiguration(context);
            StatisticProjectExpiration statistic = new StatisticProjectExpiration(trackingConfiguration);
            renderProjectTimeFrameTextualDescription(statistic, trackingConfiguration);
            renderProjectTimeframeProgress(Optional.of(statistic));
        } else {
            renderNoProjectTimeFrameTextualDescription();
            renderProjectTimeframeProgress(Optional.<StatisticProjectExpiration>absent());
        }
    }

    private void renderProjectTimeFrameTextualDescription(StatisticProjectExpiration statistic, TrackingConfiguration trackingConfiguration) {
        Optional<Integer> expirationPercent = statistic.getExpirationPercent();
        if (expirationPercent.isPresent()) { // <- theres an end date that limits the time frame
            String endDateString = DefaultLocaleDateFormatter.date().format(trackingConfiguration.getEndDateAsInclusive().get());
            if (statistic.isExpired()) {
                projectTimeFrameTextView.setText(context.getString(R.string.project_ended_on_X, endDateString));
            } else {
                projectTimeFrameTextView.setText(context.getString(R.string.project_remaining_days_X_until_Y, statistic.getRemainingDays().get(), endDateString));
            }
        } else {
            projectTimeFrameTextView.setText("");
        }
        projectTimeFrameTextView.setVisibility(View.VISIBLE);
    }

    private void renderNoProjectTimeFrameTextualDescription() {
        projectTimeFrameTextView.setText("");
        projectTimeFrameTextView.setVisibility(View.GONE);
    }

    private void renderProjectTimeframeProgress(Optional<StatisticProjectExpiration> statistic) {
        if (statistic.isPresent() && statistic.get().getExpirationPercent().isPresent()) {
            timeframeCompletionProgressBar.setProgress(statistic.get().getExpirationPercent().get());
            timeframeCompletionProgressBar.setVisibility(View.VISIBLE);
            renderProjectTimeFrameProgressColoring(statistic.get());
        } else {
            timeframeCompletionProgressBar.setVisibility(View.GONE);
        }
    }

    private void renderProjectTimeFrameProgressColoring(StatisticProjectExpiration statistic) {
        if (statistic.getExpirationPercent().get() > 100) {
            timeframeCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.RED));
        } else if (statistic.getExpirationPercent().get() > 80) {
            timeframeCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.YELLOW));
        } else {
            timeframeCompletionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.GREEN));
        }
    }
}
