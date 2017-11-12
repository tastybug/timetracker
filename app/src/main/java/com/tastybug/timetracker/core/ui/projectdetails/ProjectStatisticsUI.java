package com.tastybug.timetracker.core.ui.projectdetails;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.statistics.Completion;
import com.tastybug.timetracker.core.model.statistics.Expiration;
import com.tastybug.timetracker.core.model.statistics.ProjectDuration;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.Duration;

import java.util.List;

class ProjectStatisticsUI {

    private ImageButton trackingStartStopButton;
    private TextView expirationTextView, completionTextView;
    private ProgressBar expirationProgressBar, completionProgressBar;
    private Context context;

    ProjectStatisticsUI(Context context) {
        this.context = context;
    }

    View inflateWidgets(LayoutInflater inflater,
                        ViewGroup container,
                        View.OnClickListener toggleButtonListener) {
        View rootView = inflater.inflate(R.layout.fragment_project_statistics, container);

        expirationTextView = rootView.findViewById(R.id.expiration_statistic_text);
        completionTextView = rootView.findViewById(R.id.completion_statistic_text);
        expirationProgressBar = rootView.findViewById(R.id.expiration_progressbar);
        completionProgressBar = rootView.findViewById(R.id.completion_progressbar);
        trackingStartStopButton = rootView.findViewById(R.id.trackingStartStop);
        trackingStartStopButton.setOnClickListener(toggleButtonListener);

        return rootView;
    }

    void visualizeOngoingTracking() {
        trackingStartStopButton.setVisibility(View.VISIBLE);
        trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);
    }

    void visualizeNoOngoingTracking() {
        trackingStartStopButton.setVisibility(View.VISIBLE);
        trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
    }

    void visualizeProjectClosed() {
        trackingStartStopButton.setVisibility(View.INVISIBLE);
    }

    void renderProjectDuration(Project project) {
        renderCompletionStatisticText(project);
        renderProjectCompletionProgress(project);
    }

    private void renderCompletionStatisticText(Project project) {
        TrackingConfiguration configuration = getTrackingConfigurationForProject(project);
        Duration duration = new ProjectDuration(getTrackingRecordsByProject(project)).getDuration();

        if (configuration.getHourLimit().isPresent()) {
            if (duration.getStandardHours() < 1) {
                completionTextView.setText(
                        context.getString(R.string.completion_statistic_X_of_Y_recorded,
                            context.getString(R.string.less_than_one),
                            configuration.getHourLimit().get()));
            } else {
                completionTextView.setText(
                        context.getString(R.string.completion_statistic_X_of_Y_recorded,
                            duration.getStandardHours() + "",
                            configuration.getHourLimit().get()));
            }
        } else {
            if (duration.getMillis() == 0) {
                completionTextView.setText(R.string.nothing_recorded_yet);
            } else {
                if (duration.getStandardHours() < 1) {
                    completionTextView.setText(
                            context.getString(R.string.completion_statistic_X_recorded,
                                context.getString(R.string.less_than_one)));
                } else {
                    completionTextView.setText(
                            context.getString(R.string.completion_statistic_X_recorded,
                                duration.getStandardHours() + ""));
                }
            }
        }
    }

    private void renderProjectCompletionProgress(Project project) {
        TrackingConfiguration configuration = getTrackingConfigurationForProject(project);
        Completion completion = new Completion(configuration, getTrackingRecordsByProject(project), true);

        completionProgressBar.setVisibility(View.VISIBLE);
        completionProgressBar.setProgress(completion.getCompletionPercent().or(0d).intValue());
        renderProjectCompletionProgressColoring(completion);
    }

    private TrackingConfiguration getTrackingConfigurationForProject(Project project) {
        return new TrackingConfigurationDAO(context).getByProjectUuid(project.getUuid()).get();
    }

    private List<TrackingRecord> getTrackingRecordsByProject(Project project) {
        return new TrackingRecordDAO(context).getByProjectUuid(project.getUuid());
    }

    private void renderProjectCompletionProgressColoring(Completion completion) {
        if (completion.isOverbooked()) {
            completionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.RED));
        } else if (completion.getCompletionPercent().or(0d).intValue() > 80) {
            completionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.YELLOW));
        } else {
            completionProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.GREEN));
        }
    }

    void renderExpiration(Project project) {
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationForProject(project);
        Expiration statistic = new Expiration(trackingConfiguration);
        renderExpirationStatisticTextualDescription(statistic, trackingConfiguration);
        renderExpirationProgressBar(Optional.of(statistic));
    }

    private void renderExpirationStatisticTextualDescription(Expiration statistic, TrackingConfiguration trackingConfiguration) {
        Optional<Integer> expirationPercent = statistic.getExpirationPercent();
        if (expirationPercent.isPresent()) { // <- there is an end date that limits the time frame
            String endDateString = DefaultLocaleDateFormatter.date().format(trackingConfiguration.getEndDateAsInclusive().get());
            if (statistic.isExpired()) {
                expirationTextView.setText(context.getString(R.string.expiration_statistic_project_ended_on_X, endDateString));
            } else {
                expirationTextView.setText(context.getString(R.string.expiration_statistic_X_work_days_of_total_Y_days_remaining_until_end_date_Z,
                        statistic.getRemainingWorkDays().get(),
                        statistic.getRemainingDays().get(),
                        endDateString));
            }
        } else {
            expirationTextView.setText("");
        }
        expirationTextView.setVisibility(View.VISIBLE);
    }

    private void renderExpirationProgressBar(Optional<Expiration> statistic) {
        if (statistic.isPresent() && statistic.get().getExpirationPercent().isPresent()) {
            expirationProgressBar.setProgress(statistic.get().getExpirationPercent().get());
            expirationProgressBar.setVisibility(View.VISIBLE);
            renderExpirationProgressBarColoring(statistic.get());
        } else {
            expirationProgressBar.setVisibility(View.GONE);
        }
    }

    private void renderExpirationProgressBarColoring(Expiration statistic) {
        if (statistic.getExpirationPercent().get() > 100) {
            expirationProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.RED));
        } else if (statistic.getExpirationPercent().get() > 80) {
            expirationProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.YELLOW));
        } else {
            expirationProgressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, Color.GREEN));
        }
    }
}
