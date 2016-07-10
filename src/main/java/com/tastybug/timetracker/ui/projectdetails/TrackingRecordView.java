package com.tastybug.timetracker.ui.projectdetails;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.ui.shared.DurationFormatter;

import org.joda.time.LocalDate;

import java.text.DateFormat;

public class TrackingRecordView extends LinearLayout {

    private TextView timeFrameTextView,
                     durationTextView,
                     descriptionTextView;


    public TrackingRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tracking_record, this, true);

        timeFrameTextView = (TextView) findViewById(R.id.time_frame_line);
        durationTextView = (TextView) findViewById(R.id.duration_line);
        descriptionTextView = (TextView) findViewById(R.id.description);
    }

    public void showTrackingRecord(TrackingConfiguration trackingConfiguration,
                                   TrackingRecord trackingRecord) {
        timeFrameTextView.setText(trackingRecord.toString());
        renderTimeFrame(trackingRecord);
        renderDuration(trackingConfiguration, trackingRecord);
        renderDescription(trackingRecord);
    }

    private void renderTimeFrame(TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");
        String timeFrameText;
        DateFormat startDateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);

        if (trackingRecord.isRunning()) {
            timeFrameText = getContext().getString(R.string.running_since_X,
                    startDateFormatter.format(trackingRecord.getStart().get()));
        } else {
            DateFormat endDateFormatter;
            if (isCompletedOnSameDay(trackingRecord)) {
                endDateFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            } else {
                endDateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
            }
            timeFrameText = getContext().getString(R.string.running_from_X_until_Y,
                    startDateFormatter.format(trackingRecord.getStart().get()),
                    endDateFormatter.format(trackingRecord.getEnd().get()));
        }
        timeFrameTextView.setText(timeFrameText);
    }

    private void renderDuration(TrackingConfiguration trackingConfiguration,
                                TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");

        if (trackingRecord.isFinished()
                && trackingConfiguration.hasAlteringRoundingStrategy()) {
            durationTextView.setText(
                    getContext().getString(R.string.duration_X_effectively_Y,
                    DurationFormatter.a().formatMeasuredDuration(getContext(), trackingRecord),
                    DurationFormatter.a().formatEffectiveDuration(getContext(), trackingRecord))
                    );
        } else {
            durationTextView.setText(DurationFormatter.a().formatMeasuredDuration(getContext(), trackingRecord));
        }
    }

    private void renderDescription(TrackingRecord trackingRecord) {
        descriptionTextView.setText(trackingRecord.getDescription().isPresent()
                ? trackingRecord.getDescription().get()
                : "");
        descriptionTextView.setVisibility(trackingRecord.getDescription().isPresent()
                ? View.VISIBLE
                : View.GONE);
    }

    private boolean isCompletedOnSameDay(TrackingRecord trackingRecord) {
        return trackingRecord.isFinished()
                && new LocalDate(trackingRecord.getStart().get())
                    .isEqual(new LocalDate(trackingRecord.getEnd().get()));
    }
}