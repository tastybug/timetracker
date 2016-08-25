package com.tastybug.timetracker.ui.projectdetails;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.ui.util.DurationFormatter;
import com.tastybug.timetracker.util.Formatter;

import org.joda.time.LocalDate;

import java.util.Date;

public class TrackingControlPanelUI {

    private TextView lineOne, lineTwo;
    private ImageButton trackingStartStopButton;
    private Handler uiUpdateHandler = new Handler();

    private Context context;

    private Optional<TrackingRecord> ongoingTrackingRecordOpt = Optional.absent();

    public TrackingControlPanelUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container,
                               View.OnClickListener toggleButtonListener) {
        View rootView = inflater.inflate(R.layout.fragment_tracking_control_panel, container);

        lineOne = (TextView) rootView.findViewById(R.id.lineOne);
        lineTwo = (TextView) rootView.findViewById(R.id.lineTwo);
        trackingStartStopButton = (ImageButton) rootView.findViewById(R.id.trackingStartStop);

        trackingStartStopButton.setOnClickListener(toggleButtonListener);

        return rootView;
    }

    public void visualizeOngoingTracking(Optional<TrackingRecord> ongoingTrackingRecord) {
        this.ongoingTrackingRecordOpt = ongoingTrackingRecord;
        trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);

        lineOne.setText(context.getString(R.string.msg_tracking_since_X,
                getStartDateAsString(ongoingTrackingRecord.get().getStart().get())));
        lineTwo.setText(context.getString(R.string.msg_tracking_duration_X,
                DurationFormatter.a().formatMeasuredDuration(context, ongoingTrackingRecordOpt.get())));
    }

    public void visualizeNoOngoingTracking() {
        this.ongoingTrackingRecordOpt = Optional.absent();
        trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        lineOne.setText(R.string.msg_no_ongoing_tracking);
        lineTwo.setText("");
    }

    public void startUiUpdater() {
        uiUpdateHandler.removeCallbacks(updateUITask);
        uiUpdateHandler.postDelayed(updateUITask, 1000);
    }

    public void stopUiUpdater() {
        uiUpdateHandler.removeCallbacks(updateUITask);
    }

    private String getStartDateAsString(Date startDate) {
        return new LocalDate().isEqual(new LocalDate(startDate))
                ? Formatter.time().format(startDate) // today
                : Formatter.dateTime().format(startDate); // yesterday or even farther away
    }

    private Runnable updateUITask = new Runnable() {
        public void run() {
            if (TrackingControlPanelUI.this.ongoingTrackingRecordOpt.isPresent()) {
                visualizeOngoingTracking(TrackingControlPanelUI.this.ongoingTrackingRecordOpt);
            }
            uiUpdateHandler.postDelayed(updateUITask, 1000);
        }
    };
}
