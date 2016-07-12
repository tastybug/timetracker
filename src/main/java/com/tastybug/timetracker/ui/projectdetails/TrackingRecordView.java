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

    private TextView durationTextView, timeFrameTextView, descriptionTextView;

    public TrackingRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tracking_record, this, true);

        durationTextView = (TextView) findViewById(R.id.effective_duration);
        timeFrameTextView = (TextView) findViewById(R.id.time_frame);
        descriptionTextView = (TextView) findViewById(R.id.description);
    }

    public void showTrackingRecord(TrackingConfiguration trackingConfiguration,
                                   TrackingRecord trackingRecord) {
        renderTimeFrame(trackingRecord);
        renderDuration(trackingConfiguration, trackingRecord);
        renderDescription(trackingRecord);
    }

    private void renderTimeFrame(TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");
        String timeFrameText;

        if (trackingRecord.isRunning()) {
            DateFormat ongoingFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            timeFrameText = getContext().getString(R.string.running_since_X,
                    ongoingFormatter.format(trackingRecord.getStart().get()));
        } else {
            DateFormat finishedFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
            if (isCompletedOnSameDay(trackingRecord)) {
                timeFrameText = finishedFormatter.format(trackingRecord.getStart().get());
            } else {
                timeFrameText = getContext().getString(R.string.running_from_X_until_Y,
                        finishedFormatter.format(trackingRecord.getStart().get()),
                        finishedFormatter.format(trackingRecord.getEnd().get()));
            }
        }

        timeFrameTextView.setText(timeFrameText);
    }

    private void renderDuration(TrackingConfiguration trackingConfiguration,
                                TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");

        if (trackingRecord.isFinished()
                && trackingConfiguration.hasAlteringRoundingStrategy()) {
            durationTextView.setText(DurationFormatter.a().formatEffectiveDuration(getContext(), trackingRecord));
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
