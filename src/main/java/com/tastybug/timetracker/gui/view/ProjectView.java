package com.tastybug.timetracker.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.statistics.StatisticProjectCompletion;

import java.util.ArrayList;

public class ProjectView extends LinearLayout {

    private TextView projectTitleView, lastRecordSummaryView;
    private View projectAmountMeterContainer, projectAmountMeter1, projectAmountMeter2;

    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_project, this, true);

        projectTitleView = (TextView) findViewById(R.id.project_title);
        lastRecordSummaryView = (TextView) findViewById(R.id.project_last_tracking_record_summary);
        projectAmountMeterContainer = findViewById(R.id.project_amount_meter_container);
        projectAmountMeter1 = findViewById(R.id.project_amount_meter_1);
        projectAmountMeter2 = findViewById(R.id.project_amount_meter_2);
    }

    public void showProject(Project project,
                            Optional<TrackingRecord> lastTrackingRecordOpt,
                            ArrayList<TrackingRecord> trackingRecords,
                            TrackingConfiguration configuration) {
        renderProjectTitle(project);
        renderLastTrackingRecord(lastTrackingRecordOpt);
        renderProjectAmountMeter(configuration, trackingRecords);
    }

    private void renderProjectTitle(Project project) {
        projectTitleView.setText(project.getTitle());

    }

    private void renderLastTrackingRecord(Optional<TrackingRecord> lastTrackingRecordOpt) {
        if(lastTrackingRecordOpt.isPresent()) {
            lastRecordSummaryView.setText(lastTrackingRecordOpt.get().getStart().get().toString());
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
