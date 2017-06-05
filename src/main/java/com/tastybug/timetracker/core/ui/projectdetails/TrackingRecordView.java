package com.tastybug.timetracker.core.ui.projectdetails;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.LocalDate;

import java.text.DateFormat;

public class TrackingRecordView extends LinearLayout {

    private TextView durationTextView,
            timeFrameDatesTextView,
            dayOfWeek,
            startTime,
            stopTime,
            descriptionTextView;

    public TrackingRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tracking_record, this, true);

        durationTextView = (TextView) findViewById(R.id.effective_duration);
        timeFrameDatesTextView = (TextView) findViewById(R.id.time_frame_dates);
        dayOfWeek = (TextView) findViewById(R.id.time_frame_day_of_week_start);
        startTime = (TextView) findViewById(R.id.start_time);
        stopTime = (TextView) findViewById(R.id.end_time);
        descriptionTextView = (TextView) findViewById(R.id.description);
    }

    public void showTrackingRecord(TrackingRecord trackingRecord) {
        renderDayOfWeek(trackingRecord);
        renderTimeFrameDates(trackingRecord);
        renderTimes(trackingRecord);
        renderDuration(trackingRecord);
        renderDescription(trackingRecord);
    }

    private void renderDayOfWeek(TrackingRecord trackingRecord) {
        dayOfWeek.setText(DefaultLocaleDateFormatter.dayOfWeek().format(trackingRecord.getStart().get()));
    }

    private void renderTimeFrameDates(TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");
        String timeFrameText;

        DateFormat formatter = DefaultLocaleDateFormatter.dateLong();
        if (trackingRecord.isRunning()) {
            timeFrameText = formatter.format(trackingRecord.getStart().get());
        } else {
            if (isCompletedOnSameDay(trackingRecord)) {
                timeFrameText = formatter.format(trackingRecord.getStart().get());
            } else {
                formatter = DefaultLocaleDateFormatter.date();
                timeFrameText = getContext().getString(R.string.running_from_X_until_Y,
                        formatter.format(trackingRecord.getStart().get()),
                        formatter.format(trackingRecord.getEnd().get()));
            }
        }

        timeFrameDatesTextView.setText(timeFrameText);
        timeFrameDatesTextView.setTypeface(null, trackingRecord.isRunning() ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void renderTimes(TrackingRecord trackingRecord) {
        DateFormat timeFormatter = DefaultLocaleDateFormatter.time();
        startTime.setText(timeFormatter.format(trackingRecord.getStart().get()));
        if (trackingRecord.isFinished()) {
            stopTime.setText(timeFormatter.format(trackingRecord.getEnd().get()));
            stopTime.setVisibility(View.VISIBLE);
        } else {
            stopTime.setVisibility(View.INVISIBLE);
        }
    }

    private void renderDuration(TrackingRecord trackingRecord) {
        Preconditions.checkState(trackingRecord.isFinished() || trackingRecord.isRunning(),
                "TrackingRecord not started yet, this is not supposed to happen!");

        if (trackingRecord.isFinished()
                && trackingRecord.hasAlteringRoundingStrategy()) {
            durationTextView.setText(LocalizedDurationFormatter.a().formatEffectiveDuration(trackingRecord));
        } else {
            durationTextView.setText(LocalizedDurationFormatter.a().formatMeasuredDuration(trackingRecord));
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
