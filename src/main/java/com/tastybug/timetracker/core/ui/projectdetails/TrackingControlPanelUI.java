package com.tastybug.timetracker.core.ui.projectdetails;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.LocalDate;

import java.util.Date;

public class TrackingControlPanelUI {

    private View projectOngoingContainer;
    private TextView lineOne, lineTwo, projectClosedMessage;
    private ImageButton trackingStartStopButton;
    private Handler uiUpdateHandler = new Handler();

    private Context context;

    private Optional<TrackingRecord> ongoingTrackingRecordOpt = Optional.absent();
    private Runnable updateUITask = new Runnable() {
        public void run() {
            if (TrackingControlPanelUI.this.ongoingTrackingRecordOpt.isPresent()) {
                visualizeOngoingTracking(TrackingControlPanelUI.this.ongoingTrackingRecordOpt);
            }
            uiUpdateHandler.postDelayed(updateUITask, 1000);
        }
    };

    TrackingControlPanelUI(Context context) {
        this.context = context;
    }

    View inflateWidgets(LayoutInflater inflater,
                        ViewGroup container,
                        View.OnClickListener toggleButtonListener) {
        View rootView = inflater.inflate(R.layout.fragment_tracking_control_panel, container);

        projectOngoingContainer = rootView.findViewById(R.id.ongoing_tracking_container);
        projectClosedMessage = (TextView) rootView.findViewById(R.id.project_closed_message);
        lineOne = (TextView) rootView.findViewById(R.id.lineOne);
        lineTwo = (TextView) rootView.findViewById(R.id.lineTwo);
        trackingStartStopButton = (ImageButton) rootView.findViewById(R.id.trackingStartStop);

        trackingStartStopButton.setOnClickListener(toggleButtonListener);

        return rootView;
    }

    void visualizeOngoingTracking(Optional<TrackingRecord> ongoingTrackingRecord) {
        this.ongoingTrackingRecordOpt = ongoingTrackingRecord;
        trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);

        lineOne.setText(context.getString(R.string.msg_tracking_since_X,
                getStartDateAsString(ongoingTrackingRecord.get().getStart().get())));
        lineTwo.setText(context.getString(R.string.msg_tracking_duration_X,
                LocalizedDurationFormatter.a().formatMeasuredDuration(ongoingTrackingRecordOpt.get())));
        projectOngoingContainer.setVisibility(View.VISIBLE);
        projectClosedMessage.setVisibility(View.GONE);
    }

    void visualizeNoOngoingTracking() {
        this.ongoingTrackingRecordOpt = Optional.absent();
        trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        lineOne.setText(R.string.msg_no_ongoing_tracking);
        lineTwo.setText("");
        projectOngoingContainer.setVisibility(View.VISIBLE);
        projectClosedMessage.setVisibility(View.GONE);
    }

    void visualizeProjectClosed() {
        projectOngoingContainer.setVisibility(View.GONE);
        projectClosedMessage.setVisibility(View.VISIBLE);
    }

    void startUiUpdater() {
        uiUpdateHandler.removeCallbacks(updateUITask);
        uiUpdateHandler.postDelayed(updateUITask, 1000);
    }

    void stopUiUpdater() {
        uiUpdateHandler.removeCallbacks(updateUITask);
    }

    private String getStartDateAsString(Date startDate) {
        return new LocalDate().isEqual(new LocalDate(startDate))
                ? DefaultLocaleDateFormatter.time().format(startDate) // today
                : DefaultLocaleDateFormatter.dateTime().format(startDate); // yesterday or even farther away
    }
}
